(ns starkinfra.pix-request-test
  "Mirrors sdk-python tests/sdk/testPixRequest.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-request :as pix-request]
            [starkinfra.pix-request.log :as log]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [bank-code set-project]]))

(def ^:private content
  "{\"receiverBranchCode\": \"0001\", \"cashierBankCode\": \"\", \"senderTaxId\": \"20.018.183/0001-80\", \"senderName\": \"Stark Bank S.A. - Instituicao de Pagamento\", \"id\": \"4508348862955520\", \"senderAccountType\": \"payment\", \"fee\": 0, \"receiverName\": \"Cora\", \"cashierType\": \"\", \"externalId\": \"\", \"method\": \"manual\", \"status\": \"processing\", \"updated\": \"2022-02-16T17:23:53.980250+00:00\", \"description\": \"\", \"tags\": [], \"receiverKeyId\": \"\", \"cashAmount\": 0, \"senderBankCode\": \"20018183\", \"senderBranchCode\": \"0001\", \"bankCode\": \"34052649\", \"senderAccountNumber\": \"5647143184367616\", \"receiverAccountNumber\": \"5692908409716736\", \"initiatorTaxId\": \"\", \"receiverTaxId\": \"34.052.649/0001-78\", \"created\": \"2022-02-16T17:23:53.980238+00:00\", \"flow\": \"in\", \"endToEndId\": \"E20018183202202161723Y4cqxlfLFcm\", \"amount\": 1, \"receiverAccountType\": \"checking\", \"reconciliationId\": \"\", \"receiverBankCode\": \"34052649\"}")

(def ^:private valid-signature
  "MEUCIQC7FVhXdripx/aXg5yNLxmNoZlehpyvX3QYDXJ8o02X2QIgVwKfJKuIS5RDq50NC/+55h/7VccDkV1vm8Q/7jNu0VM=")

(def ^:private invalid-signature
  "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")

(defn- example-request []
  {:amount 100
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :sender-branch-code "0000"
   :sender-account-number "00000-0"
   :sender-account-type "checking"
   :sender-name "Tyrion Lannister"
   :sender-tax-id "012.345.678-90"
   :receiver-bank-code "00000001"
   :receiver-branch-code "0001"
   :receiver-account-number "00000-1"
   :receiver-account-type "checking"
   :receiver-name "Jamie Lannister"
   :receiver-tax-id "45.987.245/0001-92"
   :end-to-end-id (end-to-end-id/create (bank-code))
   :description "For saving my life"
   :reason "subscriptionFlaw"})


(deftest ^:sandbox create-and-get-pix-requests
  (set-project)
  (testing "every created request can be retrieved by its id"
    (let [requests (pix-request/create [(example-request) (example-request)])]
      (is (= 2 (count requests)))
      (doseq [request requests]
        (is (= (:id request) (:id (pix-request/get (:id request)))))))))

(deftest ^:sandbox query-pix-requests
  (set-project)
  (testing "the stream honours the limit"
    (is (= 10 (count (take 200 (pix-request/query {:limit 10}))))))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-request/query {:limit 10
                                        :status "failed"
                                        :tags ["iron" "bank"]
                                        :ids ["1" "2" "3"]
                                        :external-ids ["1" "2" "3"]
                                        :end-to-end-ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-pix-requests
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-request/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-pix-request-logs
  (set-project)
  (testing "a log carries its request"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:request fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))

(deftest ^:sandbox parse-pix-requests
  (set-project)
  (testing "the valid signature parses and python's defaults are filled in"
    (let [request (pix-request/parse content valid-signature)]
      (is (= "4508348862955520" (:id request)))
      (is (= "E20018183202202161723Y4cqxlfLFcm" (:end-to-end-id request)))
      (is (= 0 (:fee request)))
      (is (= [] (:tags request)))
      (is (= "" (:external-id request)))
      (is (= "" (:description request)))))

  (testing "a signature from another payload is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (pix-request/parse content invalid-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e)))))))

  (testing "a malformed signature is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (pix-request/parse content "something is definitely wrong")
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))
