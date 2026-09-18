(ns starkinfra.pix-reversal-test
  "Mirrors sdk-python tests/sdk/testPixReversal.py and
  tests/sdk/testPixReversalLog.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-reversal :as pix-reversal]
            [starkinfra.pix-reversal.log :as log]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [bank-code set-project]]))

(def ^:private content
  "{\"receiverBranchCode\": \"0001\", \"cashierBankCode\": \"\", \"senderTaxId\": \"20.018.183/0001-80\", \"senderName\": \"Stark Bank S.A. - Instituicao de Pagamento\", \"id\": \"4508348862955520\", \"senderAccountType\": \"payment\", \"fee\": 0, \"receiverName\": \"Cora\", \"cashierType\": \"\", \"externalId\": \"\", \"method\": \"manual\", \"status\": \"processing\", \"updated\": \"2022-02-16T17:23:53.980250+00:00\", \"description\": \"\", \"tags\": [], \"receiverKeyId\": \"\", \"cashAmount\": 0, \"senderBankCode\": \"20018183\", \"senderBranchCode\": \"0001\", \"bankCode\": \"34052649\", \"senderAccountNumber\": \"5647143184367616\", \"receiverAccountNumber\": \"5692908409716736\", \"initiatorTaxId\": \"\", \"receiverTaxId\": \"34.052.649/0001-78\", \"created\": \"2022-02-16T17:23:53.980238+00:00\", \"flow\": \"in\", \"endToEndId\": \"E20018183202202161723Y4cqxlfLFcm\", \"amount\": 1, \"receiverAccountType\": \"checking\", \"reconciliationId\": \"\", \"receiverBankCode\": \"34052649\"}")

(def ^:private valid-signature
  "MEUCIQC7FVhXdripx/aXg5yNLxmNoZlehpyvX3QYDXJ8o02X2QIgVwKfJKuIS5RDq50NC/+55h/7VccDkV1vm8Q/7jNu0VM=")

(def ^:private invalid-signature
  "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")

(defn- example-reversal []
  {:amount (inc (rand-int 10))
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :end-to-end-id (end-to-end-id/create (bank-code))
   :reason "bankError"
   :tags ["little" "girl"]})


(deftest ^:sandbox create-and-get-pix-reversals
  (set-project)
  (testing "every created reversal can be retrieved by its id"
    (let [reversals (pix-reversal/create [(example-reversal)])]
      (is (= 1 (count reversals)))
      (doseq [reversal reversals]
        (is (= (:id reversal) (:id (pix-reversal/get (:id reversal)))))))))

(deftest ^:sandbox query-pix-reversals
  (set-project)
  (testing "the stream honours the limit"
    (is (= 10 (count (take 200 (pix-reversal/query {:limit 10}))))))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-reversal/query {:limit 10
                                         :status "failed"
                                         :tags ["iron" "bank"]
                                         :ids ["1" "2" "3"]
                                         :external-ids ["1" "2" "3"]
                                         :return-ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-pix-reversals
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-reversal/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-pix-reversal-logs
  (set-project)
  (testing "a log carries its reversal"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:reversal fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))

(deftest ^:sandbox parse-pix-reversals
  (set-project)
  (testing "the valid signature parses and python's defaults are filled in"
    (let [reversal (pix-reversal/parse content valid-signature)]
      (is (= "4508348862955520" (:id reversal)))
      (is (= "E20018183202202161723Y4cqxlfLFcm" (:end-to-end-id reversal)))
      (is (= 0 (:fee reversal)))
      (is (= [] (:tags reversal)))
      (is (= "" (:external-id reversal)))
      (is (= "" (:description reversal)))))

  (testing "a signature from another payload is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (pix-reversal/parse content invalid-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e)))))))

  (testing "a malformed signature is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (pix-reversal/parse content "something is definitely wrong")
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))

(deftest response-builds-the-authorization-envelope
  (testing "a nil reason is dropped, as api-json drops every nil member"
    (is (= "{\"authorization\": {\"status\": \"approved\"}}"
           (pix-reversal/response "approved"))))

  (testing "a denied response carries its reason"
    (is (= "{\"authorization\": {\"status\": \"denied\", \"reason\": \"taxIdMismatch\"}}"
           (pix-reversal/response "denied" "taxIdMismatch")))))
