(ns starkinfra.dynamic-brcode-test
  "Mirrors sdk-python tests/sdk/testDynamicBrcode.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.dynamic-brcode :as dynamic-brcode]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-brcode [type]
  {:name "Jamie Lannister"
   :city "Rio de Janeiro"
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :type type})


(deftest ^:sandbox create-and-get-dynamic-brcodes
  (set-project)
  (testing "every created brcode type can be retrieved by its uuid"
    (doseq [type ["instant" "due" "subscription" "subscriptionAndInstant" "dueAndOrSubscription"]]
      (let [brcodes (dynamic-brcode/create [(example-brcode type)])]
        (doseq [brcode brcodes]
          (is (= type (:type brcode)))
          (is (= (:uuid brcode) (:uuid (dynamic-brcode/get (:uuid brcode))))))))))

(deftest ^:sandbox query-dynamic-brcodes
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 5 (dynamic-brcode/query {:limit 5}))) 5))))

(deftest ^:sandbox page-dynamic-brcodes
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(dynamic-brcode/page %) 2 {:limit 2}))))))

(deftest verify-rejects-a-malformed-signature
  (testing "signature decoding fails before any network call, so this proves out with no credentials"
    (is (= "invalidSignature"
           (:code (ex-data (try (dynamic-brcode/verify "some-uuid" "something is definitely wrong")
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))

(deftest ^:sandbox verify-rejects-a-signature-from-another-payload
  (set-project)
  (testing "a well-formed but mismatched signature is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (dynamic-brcode/verify
                                  "21f174ab942843eb90837a5c3135dfd6"
                                  "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))

(deftest response-due-builds-the-expected-json
  (testing "the builder mirrors python's response_due bytes"
    (let [response (dynamic-brcode/response-due
                    {:version 1
                     :created "2022-03-10T10:30:00.000000+00:00"
                     :due "2022-07-15"
                     :key-id "+5511989898989"
                     :status "paid"
                     :reconciliation-id "b77f5236-7ab9-4487-9f95-66ee6eaf1781"
                     :nominal-amount 100
                     :sender-name "Anthony Edward Stark"
                     :sender-tax-id "012.345.678-90"
                     :receiver-name "Jamie Lannister"
                     :receiver-tax-id "20.018.183/0001-8"
                     :receiver-street-line "Av. Paulista, 200"
                     :receiver-city "Sao Paulo"
                     :receiver-state-code "SP"
                     :receiver-zip-code "01234-567"
                     :description "teste Clojure"})]
      (is (re-find #"\"version\": 1" response))
      (is (re-find #"\"keyId\": \"\+5511989898989\"" response))
      (is (re-find #"\"receiverZipCode\": \"01234-567\"" response))
      (is (not (re-find #"\"fine\"" response)))
      (is (not (re-find #"\"data\"" response))))))

(deftest response-instant-builds-the-expected-json
  (testing "the builder mirrors python's response_instant bytes"
    (let [response (dynamic-brcode/response-instant
                    {:version 1
                     :created "2022-07-01"
                     :key-id "+5511989898989"
                     :status "paid"
                     :reconciliation-id "b77f5236-7ab9-4487-9f95-66ee6eaf1781"
                     :amount 100})]
      (is (re-find #"\"amount\": 100" response))
      (is (re-find #"\"keyId\": \"\+5511989898989\"" response))
      (is (not (re-find #"\"cashierType\"" response))))))
