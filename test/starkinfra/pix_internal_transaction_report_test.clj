(ns starkinfra.pix-internal-transaction-report-test
  "Mirrors sdk-python tests/sdk/testPixInternalTransactionReport.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-internal-transaction-report :as report]
            [starkinfra.pix-internal-transaction-report.log :as log]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.return-id :as return-id]
            [starkinfra.utils.user :refer [bank-code set-project]]))

(defn- accepted-report [reference-type]
  {:amount (+ 100 (rand-int 999900))
   :created (date/future-datetime (- (inc (rand-int 2))))
   :end-to-end-id (end-to-end-id/create (bank-code))
   :method (if (= reference-type "reversal") "dict" "manual")
   :reference-type reference-type
   :sender-account-number "00000-0"
   :sender-branch-code "0000"
   :sender-account-type "checking"
   :sender-bank-code (bank-code)
   :sender-tax-id "012.345.678-90"
   :receiver-account-number "00000-1"
   :receiver-branch-code "0001"
   :receiver-account-type "checking"
   :receiver-bank-code (rand-nth ["18236120" "60701190" "20018183"])
   :receiver-tax-id "012.345.678-90"})

(defn- example-request []
  (assoc (accepted-report "request") :receiver-key-id "+5511989898989"))

(defn- example-reversal [generated-return-id]
  (assoc (accepted-report "reversal") :return-id generated-return-id))


(deftest ^:sandbox create-and-get-pix-internal-transaction-reports
  (set-project)
  (testing "every created report can be retrieved by its id"
    (let [reports (report/create [(example-request)])]
      (doseq [r reports]
        (is (= (:id r) (:id (report/get (:id r)))))))))

(deftest ^:sandbox query-pix-internal-transaction-reports
  (set-project)
  (testing "the stream honours the limit"
    (is (= 4 (count (take 200 (report/query {:limit 4})))))))

(deftest ^:sandbox page-pix-internal-transaction-reports
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(report/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-pix-internal-transaction-report-logs
  (set-project)
  (testing "a log carries its report"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:report fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))

(deftest ^:sandbox create-pix-internal-transaction-report-request
  (set-project)
  (testing "a request report with valid ids is created"
    (let [[created] (report/create [(example-request)])]
      (is (some? (:id created)))
      (is (= "request" (:reference-type created))))))

(deftest ^:sandbox create-pix-internal-transaction-report-reversal
  (set-project)
  (testing "a reversal report carrying a library-generated return id is created"
    (let [generated-return-id (return-id/create (bank-code))]
      (is (some? generated-return-id))
      (is (= 32 (count generated-return-id)))
      (is (.startsWith ^String generated-return-id "D"))
      (let [[created] (report/create [(example-reversal generated-return-id)])]
        (is (some? (:id created)))
        (is (= "reversal" (:reference-type created)))))))
