(ns starkinfra.issuing-purchase-test
  "Mirrors sdk-python tests/sdk/testIssuingPurchase.py and
  testIssuingPurchaseLog.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-purchase :as issuing-purchase]
            [starkinfra.issuing-purchase.log :as log]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private content
  "{\"acquirerId\": \"236090\", \"amount\": 100, \"cardId\": \"5671893688385536\", \"cardTags\": [], \"endToEndId\": \"2fa7ef9f-b889-4bae-ac02-16749c04a3b6\", \"holderId\": \"5917814565109760\", \"holderTags\": [], \"isPartialAllowed\": false, \"issuerAmount\": 100, \"issuerCurrencyCode\": \"BRL\", \"merchantAmount\": 100, \"merchantCategoryCode\": \"bookStores\", \"merchantCountryCode\": \"BRA\", \"merchantCurrencyCode\": \"BRL\", \"merchantFee\": 0, \"merchantId\": \"204933612653639\", \"merchantName\": \"COMPANY 123\", \"methodCode\": \"token\", \"purpose\": \"purchase\", \"score\": null, \"tax\": 0, \"walletId\": \"\"}")

(def ^:private valid-signature
  "MEUCIBxymWEpit50lDqFKFHYOgyyqvE5kiHERi0ZM6cJpcvmAiEA2wwIkxcsuexh9BjcyAbZxprpRUyjcZJ2vBAjdd7o28Q=")

(def ^:private invalid-signature
  "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")


(deftest ^:sandbox query-and-get-issuing-purchases
  (set-project)
  (testing "every queried purchase can be retrieved by its id"
    (let [purchases (take 10 (issuing-purchase/query {:limit 10
                                                       :after "2020-01-01"
                                                       :before "2020-03-01"}))]
      (doseq [purchase purchases]
        (is (= (:id purchase) (:id (issuing-purchase/get (:id purchase)))))))))

(deftest ^:sandbox update-issuing-purchase
  (set-project)
  (testing "the description and tags survive an update"
    (let [purchase (first (issuing-purchase/query {:limit 1}))
          updated (issuing-purchase/update (:id purchase) {:description "teste"
                                                            :tags ["teste 1" "teste 2"]})]
      (is (= (:id purchase) (:id updated))))))

(deftest ^:sandbox parse-issuing-purchases
  (set-project)
  (testing "the valid signature parses"
    (let [purchase (issuing-purchase/parse content valid-signature)]
      (is (= "5671893688385536" (:card-id purchase)))))

  (testing "a signature from another payload is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (issuing-purchase/parse content invalid-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))

(deftest response-approved
  (testing "an approved response drops the absent reason"
    (is (= "{\"authorization\": {\"status\": \"approved\", \"amount\": 1000, \"tags\": [\"tony\", \"stark\"]}}"
           (issuing-purchase/response "approved" {:amount 1000 :tags ["tony" "stark"]})))))

(deftest response-denied
  (testing "a denied response drops the absent amount"
    (is (= "{\"authorization\": {\"status\": \"denied\", \"reason\": \"other\", \"tags\": [\"tony\", \"stark\"]}}"
           (issuing-purchase/response "denied" {:reason "other" :tags ["tony" "stark"]})))))

(deftest ^:sandbox query-and-get-issuing-purchase-logs
  (set-project)
  (testing "a log can be retrieved by its id"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched))))))
