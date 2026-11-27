(ns starkinfra.issuing-card-without-holder-test
  "IssuingCard without a holder, holder assignment by `update` and `update-multi`.
  The *-payload tests are offline: only `clj-http.client/request` is stubbed, so payload building and signing run for real.
  The ^:sandbox tests run against the real API (SANDBOX_* credentials) and cancel every card they create."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-card :as issuing-card]
            [starkinfra.issuing-holder :as issuing-holder]
            [starkinfra.issuing-product :as issuing-product]
            [starkinfra.key :as key]
            [starkinfra.user :as user]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private project
  (delay (user/project "sandbox" "5656565656565656" (:private-pem (key/create)))))

(defn- recorder
  "Returns the requests atom and a `clj-http.client/request` stand-in that replies with `body`."
  [body]
  (let [sent (atom [])]
    [sent (fn [request]
            (swap! sent conj request)
            {:status 200 :body body})]))

(defn- sent-json [sent]
  (cheshire/parse-string (:body (first @sent))))

(deftest create-without-holder-payload-omits-the-holder-fields
  (let [[sent handler] (recorder "{\"cards\": [{\"id\": \"1\", \"status\": \"pending\", \"holderId\": \"\", \"holderName\": \"\"}]}")]
    (with-redefs [client/request handler]
      (let [cards (issuing-card/create [{:product-id "123" :type "physical"}] {} @project)]
        (is (= {"cards" [{"productId" "123" "type" "physical"}]} (sent-json sent)))
        (is (= "" (:holder-id (first cards))))
        (is (= "" (:holder-name (first cards))))))))

(deftest create-payload-sends-the-holder-fields-when-informed
  (let [[sent handler] (recorder "{\"cards\": [{\"id\": \"1\"}]}")]
    (with-redefs [client/request handler]
      (issuing-card/create [{:holder-name "Tony Stark" :holder-tax-id "012.345.678-90"
                             :holder-external-id "my-entity/123" :product-id "123"}] {} @project)
      (is (= {"cards" [{"holderName" "Tony Stark" "holderTaxId" "012.345.678-90"
                        "holderExternalId" "my-entity/123" "productId" "123"}]}
             (sent-json sent))))))

(deftest update-payload
  (doseq [[label params] [["holder fields are not sent when omitted" {:display-name "ANTHONY STARK"}]
                          ["holder fields are sent alone" {:holder-name "Tony Stark" :holder-tax-id "012.345.678-90"
                                                           :holder-external-id "my-entity/123"}]
                          ["holder fields are sent with status and pin" {:status "active" :pin "1234"
                                                                         :holder-name "Tony Stark" :holder-tax-id "012.345.678-90"
                                                                         :holder-external-id "my-entity/123"}]]]
    (testing label
      (let [[sent handler] (recorder "{\"card\": {\"id\": \"5656565656565656\", \"status\": \"pending\"}}")]
        (with-redefs [client/request handler]
          (issuing-card/update "5656565656565656" params @project)
          (is (= :patch (:method (first @sent))))
          (is (string/ends-with? (:url (first @sent)) "/v2/issuing-card/5656565656565656"))
          (is (= (cheshire/parse-string (cheshire/generate-string
                                         (into {} (map (fn [[k v]] [(string/replace (name k) #"-(\w)" (comp string/upper-case second)) v]) params))))
                 (sent-json sent))))))))

(deftest update-multi-payload
  (let [[sent handler] (recorder "{\"cards\": [{\"id\": \"1\", \"status\": \"pending\"}, {\"id\": \"2\", \"status\": \"pending\"}]}")]
    (with-redefs [client/request handler]
      (let [cards (issuing-card/update-multi [{:id "1" :holder-name "Tony Stark" :holder-tax-id "012.345.678-90"
                                               :holder-external-id "my-entity/123"}
                                              {:id "2" :tags ["batch"]}]
                                             @project)]
        (is (= :patch (:method (first @sent))))
        (is (string/ends-with? (:url (first @sent)) "/v2/issuing-card"))
        (is (= {"cards" [{"id" "1" "holderName" "Tony Stark" "holderTaxId" "012.345.678-90" "holderExternalId" "my-entity/123"}
                         {"id" "2" "tags" ["batch"]}]}
               (sent-json sent)))
        (is (= ["1" "2"] (map :id cards)))))))

;; ---- sandbox ----

(defn- first-product []
  (or (first (issuing-product/query {:limit 1}))
      (throw (ex-info "no issuing product in the sandbox Project" {}))))

(defn- digits [text]
  (count (filter #(Character/isDigit ^char %) text)))

;; A holder can only be linked to a product of its own type: business holders have a CNPJ (14 digits) and individual holders a
;; CPF (11 digits). Existing holders are used so no personal data is created by the test.
(defn- holder-for-product [product]
  (let [expected ({"business" 14 "individual" 11} (:holder-type product))]
    (when-not expected
      (throw (ex-info "unknown holder-type of the sandbox product" {:holder-type (:holder-type product)})))
    (first (filter #(= expected (digits (:tax-id %))) (issuing-holder/query {:status "active" :limit 100})))))

(defn- cards-without-holder [n card-type product-id]
  (vec (repeat n {:product-id product-id :type card-type :tags ["sdk-test"]})))

(defn- cancel-all [cards]
  (doseq [card cards]
    (try
      (issuing-card/cancel (:id card))
      (catch Exception _ (println "could not cancel card" (:id card))))))

(defn- holder-fields [holder]
  {:holder-name (:name holder) :holder-tax-id (:tax-id holder) :holder-external-id (:external-id holder)})

(defn- rejected-codes [f]
  (try
    (f)
    nil
    (catch clojure.lang.ExceptionInfo exception
      (is (= 400 (:status (ex-data exception))))
      (set (map :code (:errors (ex-data exception)))))))

(defn- with-pending-cards
  "Creates n pending physical cards without holder, runs (f cards holder) and always cancels the cards."
  [n f]
  (set-project)
  (let [product (first-product)
        holder (holder-for-product product)]
    (is (some? holder) "the sandbox needs an existing active holder compatible with the product")
    (when holder
      (let [cards (issuing-card/create (cards-without-holder n "physical" (:id product)))]
        (try
          (f cards holder)
          (finally (cancel-all cards)))))))

(deftest ^:sandbox physical-card-without-holder-is-created-pending
  (set-project)
  (let [cards (issuing-card/create (cards-without-holder 1 "physical" (:id (first-product))))]
    (try
      (is (= 1 (count cards)))
      (is (= "pending" (:status (first cards))))
      (is (= "physical" (:type (first cards))))
      (is (= "" (:holder-id (first cards))))
      (is (= "" (:holder-name (first cards))))
      (finally (cancel-all cards)))))

(deftest ^:sandbox api-rejects-virtual-card-without-holder
  (set-project)
  (is (contains? (rejected-codes #(cancel-all (issuing-card/create (cards-without-holder 1 "virtual" (:id (first-product))))))
                 "invalidHolder")))

(deftest ^:sandbox api-rejects-incomplete-holder-fields-on-create
  (set-project)
  (is (contains? (rejected-codes #(cancel-all (issuing-card/create
                                               [{:product-id (:id (first-product)) :type "physical" :holder-name "Tony Stark"}])))
                 "invalidHolder")))

(deftest ^:sandbox holder-is-assigned-to-a-pending-card
  (with-pending-cards 1
    (fn [cards holder]
      (let [card (issuing-card/update (:id (first cards)) (holder-fields holder))]
        (is (= "pending" (:status card)))
        (is (= (:id holder) (:holder-id card)))))))

(deftest ^:sandbox holder-is-assigned-and-card-activated-in-the-same-call
  (with-pending-cards 1
    (fn [cards holder]
      (let [card (issuing-card/update (:id (first cards)) (assoc (holder-fields holder) :status "active" :pin "1234"))]
        (is (= "active" (:status card)))
        (is (= (:id holder) (:holder-id card)))))))

(deftest ^:sandbox api-rejects-incomplete-holder-fields-on-update
  (with-pending-cards 1
    (fn [cards holder]
      (is (contains? (rejected-codes #(issuing-card/update (:id (first cards)) {:holder-name (:name holder)}))
                     "invalidHolder")))))

(deftest ^:sandbox api-rejects-activation-without-holder
  (with-pending-cards 1
    (fn [cards _]
      (is (contains? (rejected-codes #(issuing-card/update (:id (first cards)) {:status "active" :pin "1234"}))
                     "invalidHolder")))))

(deftest ^:sandbox holder-is-assigned-to-several-cards
  (with-pending-cards 2
    (fn [cards holder]
      (let [updated (issuing-card/update-multi (mapv #(merge {:id (:id %)} (holder-fields holder)) cards))]
        (is (= (sort (map :id cards)) (sort (map :id updated))))
        ;; ids only: a failing assertion prints its operands, and the cards carry the holder's personal data
        (is (= #{(:id holder)} (set (map :holder-id updated))))))))

(deftest ^:sandbox api-rejects-the-whole-list-when-one-item-is-invalid
  (with-pending-cards 2
    (fn [cards holder]
      (is (contains? (rejected-codes #(issuing-card/update-multi [(merge {:id (:id (first cards))} (holder-fields holder))
                                                                  (merge {:id "0000000000000001"} (holder-fields holder))]))
                     "invalidCard"))
      (is (= "" (:holder-id (issuing-card/get (:id (first cards)))))))))

(deftest ^:sandbox api-rejects-incomplete-holder-fields-on-update-multi
  (with-pending-cards 1
    (fn [cards holder]
      (is (contains? (rejected-codes #(issuing-card/update-multi [{:id (:id (first cards)) :holder-name (:name holder)}]))
                     "invalidHolder")))))

(deftest ^:sandbox api-rejects-pin-in-the-batch
  (with-pending-cards 1
    (fn [cards _holder]
      (is (contains? (rejected-codes #(issuing-card/update-multi [{:id (:id (first cards)) :pin "1234"}]))
                     "invalidJson")))))

(deftest ^:sandbox api-rejects-display-name-in-the-batch
  (with-pending-cards 1
    (fn [cards _holder]
      (is (contains? (rejected-codes #(issuing-card/update-multi [{:id (:id (first cards)) :display-name "Name"}]))
                     "invalidJson")))))
