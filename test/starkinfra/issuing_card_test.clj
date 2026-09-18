(ns starkinfra.issuing-card-test
  "Mirrors sdk-python tests/sdk/testIssuingCard.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-card :as issuing-card]
            [starkinfra.issuing-card.log :as log]
            [starkinfra.issuing-holder :as issuing-holder]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-holder []
  {:name "Iron Bank S.A."
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :tax-id "012.345.678-90"
   :tags ["Traveler Employee"]})

(defn- example-card [holder]
  {:holder-name (:name holder)
   :holder-tax-id (:tax-id holder)
   :holder-external-id (:external-id holder)
   :rules [{:name "general"
            :interval "week"
            :amount 50000
            :currency-code "USD"}]})


(deftest ^:sandbox create-and-get-issuing-cards
  (set-project)
  (testing "every created card can be retrieved by its id, with expand"
    (let [holder (first (issuing-holder/create [(example-holder)]))
          cards (issuing-card/create [(example-card holder)] {:expand ["securityCode"]})]
      (doseq [card cards]
        (is (some? (:security-code card)))
        (is (= (:id card) (:id (issuing-card/get (:id card) {:expand ["securityCode"]}))))))))

(deftest ^:sandbox query-issuing-cards
  (set-project)
  (testing "the stream honours the limit"
    (is (= 10 (count (take 200 (issuing-card/query {:limit 10})))))))

(deftest ^:sandbox page-issuing-cards
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(issuing-card/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-and-cancel-issuing-card
  (set-project)
  (testing "a created card can be renamed and then cancelled"
    (let [holder (first (issuing-holder/create [(example-holder)]))
          card (first (issuing-card/create [(example-card holder)]))
          updated (issuing-card/update (:id card) {:display-name "Updated Name"})
          cancelled (issuing-card/cancel (:id card))]
      (is (= "Updated Name" (:display-name updated)))
      (is (= "canceled" (:status cancelled))))))

(deftest ^:sandbox query-and-get-issuing-card-logs
  (set-project)
  (testing "a log carries its card"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:card fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
