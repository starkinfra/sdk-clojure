(ns starkinfra.credit-note-test
  "Mirrors sdk-python tests/sdk/testCreditNote.py. The ^:sandbox tests need
  SANDBOX_* credentials; the template-replacements tests are offline and stub
  `clj-http.client/request`."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing]]
            [starkinfra.credit-note :as credit-note]
            [starkinfra.credit-note.log :as log]
            [starkinfra.key :as key]
            [starkinfra.user :as user]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project template-id]]))

(defn- example-invoice []
  {:amount (+ 100000 (rand-int 100000))
   :due (date/future-date (+ 500 (rand-int 100)))
   :descriptions [{:key "taxes" :value "R$100,00"}]})

(defn- example-signer []
  {:name "Jamie Lannister"
   :contact (str "jamie.lannister." (rand-int 1000000000) "@invaliddomain.com")
   :method "link"})

(defn- example-credit-note []
  {:template-id (template-id)
   :name "Jamie Lannister"
   :tax-id "012.345.678-90"
   :nominal-amount (+ 100000 (rand-int 900000))
   :scheduled (date/future-date (+ 10 (rand-int 10)))
   :invoices [(example-invoice) (example-invoice) (example-invoice)]
   :payment {:bank-code "60701190"
             :branch-code (str (+ 1000 (rand-int 8999)))
             :account-number (str (+ 1000000 (rand-int 8999999)))
             :name "Jamie Lannister"
             :tax-id "012.345.678-90"}
   :payment-type "transfer"
   :signers [(example-signer)]
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :street-line-1 "Rua ABC"
   :street-line-2 "Ap 123"
   :district "Jardim Paulista"
   :city "Sao Paulo"
   :state-code "SP"
   :zip-code "01234-567"
   :tags ["test" "testing"]})


(deftest ^:sandbox create-and-get-credit-notes
  (set-project)
  (testing "every created note can be retrieved by its id"
    (let [notes (credit-note/create [(example-credit-note)])]
      (is (= 1 (count notes)))
      (doseq [note notes]
        (is (some? (:id note)))
        (is (= (:id note) (:id (credit-note/get (:id note)))))))))

(deftest ^:sandbox create-credit-note-with-template-replacements
  (set-project)
  (testing "a note created with template-replacements is accepted"
    (let [replacements {:guarantor-name "Jon Snow"
                        :guarantor-tax-id "012.345.678-90"
                        :guarantor-address "Av. Paulista, 200 - Bela Vista - Sao Paulo, SP"}
          notes (credit-note/create [(assoc (example-credit-note)
                                            :tags ["sdk-test"]
                                            :template-replacements replacements)])]
      (is (= 1 (count notes)))
      (is (some? (:id (first notes)))))))

(deftest ^:sandbox query-credit-notes
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 200 (credit-note/query {:limit 1}))) 1))))

(deftest ^:sandbox page-credit-notes
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(credit-note/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-credit-note
  (set-project)
  (testing "a freshly created note can be canceled"
    (let [note (first (credit-note/create [(example-credit-note)]))]
      (is (= (:id note) (:id (credit-note/cancel (:id note))))))))

(deftest ^:sandbox pdf-and-payment-pdf
  (set-project)
  (testing "a signed note's disbursement pdf and payment pdf both start with %PDF"
    (let [signed (first (credit-note/query {:status "signed" :limit 1}))]
      (when signed
        (is (string/starts-with? (credit-note/pdf (:id signed)) "%PDF"))
        (is (string/starts-with? (credit-note/payment (:id signed)) "%PDF"))))))

(deftest ^:sandbox query-and-get-credit-note-logs
  (set-project)
  (testing "a log carries its note"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:note fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))


;; Offline: nothing below needs credentials or the network.

(def ^:private offline-project
  (delay (user/project "sandbox" "5656565656565656" (:private-pem (key/create)))))

(defn- create-offline
  "Runs credit-note/create with the HTTP layer stubbed. Returns the parsed JSON
  body the SDK would have sent (string keys, as on the wire) and the notes it
  made of `reply`."
  [note reply]
  (let [sent (atom nil)
        notes (with-redefs [client/request (fn [request]
                                             (reset! sent request)
                                             {:status 200 :body (cheshire/generate-string reply)})]
                (credit-note/create [note] @offline-project))]
    {:sent (cheshire/parse-string (:body @sent))
     :notes notes}))

(defn- sent-replacements [note]
  (get-in (create-offline note {:notes [{:id "1"}]}) [:sent "notes" 0 "templateReplacements"]))

(deftest template-replacements-are-sent-with-their-keys-intact
  (testing "camelCase string keys, as the placeholders are named, reach the wire untouched"
    (is (= {"guarantorName" "Jon Snow"
            "guarantorTaxId" "012.345.678-90"
            "cpfCnpjAvalista" "012.345.678-90"}
           (sent-replacements {:template-id "1"
                               :template-replacements {"guarantorName" "Jon Snow"
                                                       "guarantorTaxId" "012.345.678-90"
                                                       "cpfCnpjAvalista" "012.345.678-90"}}))))

  (testing "kebab-case keywords are sent camelCased"
    (is (= {"guarantorName" "Jon Snow" "cpfCnpjAvalista" "1"}
           (sent-replacements {:template-id "1"
                               :template-replacements {:guarantor-name "Jon Snow"
                                                       :cpf-cnpj-avalista "1"}}))))

  (testing "nested maps follow the same key rule and nested nil values are dropped"
    (is (= {"guarantor" {"fullName" "Jon Snow"}}
           (sent-replacements {:template-id "1"
                               :template-replacements {"guarantor" {:full-name "Jon Snow"
                                                                    "middleName" nil}}})))))

(deftest template-replacements-is-left-out-when-absent
  (testing "no key at all, and nil, never reach the wire"
    (is (not (contains? (get-in (create-offline {:template-id "1"} {:notes [{:id "1"}]})
                                [:sent "notes" 0])
                        "templateReplacements")))
    (is (not (contains? (get-in (create-offline {:template-id "1" :template-replacements nil}
                                                {:notes [{:id "1"}]})
                                [:sent "notes" 0])
                        "templateReplacements")))))

(deftest template-replacements-are-read-from-the-response
  (testing "the map comes back with its keys as kebab-case keywords and nil values kept"
    (let [notes (:notes (create-offline {:template-id "1"}
                                        {:notes [{:id "5656565656565656"
                                                  :templateReplacements {:guarantorName "Jon Snow"
                                                                         :cpfCnpjAvalista "1"
                                                                         :guarantorComplement nil}}]}))]
      (is (= {:guarantor-name "Jon Snow"
              :cpf-cnpj-avalista "1"
              :guarantor-complement nil}
             (:template-replacements (first notes)))))))
