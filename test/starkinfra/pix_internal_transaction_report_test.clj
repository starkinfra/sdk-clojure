(ns starkinfra.pix-internal-transaction-report-test
  "Mirrors sdk-python tests/sdk/testPixInternalTransactionReport.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-internal-transaction-report :as report]
            [starkinfra.pix-internal-transaction-report.log :as log]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-report []
  {:amount 10000
   :created (date/future-datetime)
   :end-to-end-id "E12345678202401011234567890123456"
   :method "manual"
   :reference-type "request"
   :sender-account-number "12345"
   :sender-branch-code "0001"
   :sender-account-type "checking"
   :sender-bank-code "12345678"
   :sender-tax-id "123.456.789-01"
   :receiver-account-number "67890"
   :receiver-branch-code "0001"
   :receiver-account-type "savings"
   :receiver-bank-code "87654321"
   :receiver-tax-id "987.654.321-00"
   :receiver-key-id "user@example.com"})


(deftest ^:sandbox create-and-get-pix-internal-transaction-reports
  (set-project)
  (testing "every created report can be retrieved by its id"
    (let [reports (report/create [(example-report)])]
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
