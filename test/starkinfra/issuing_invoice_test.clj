(ns starkinfra.issuing-invoice-test
  "Mirrors sdk-python tests/sdk/testIssuingInvoice.py and
  testIssuingInvoiceLog.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-invoice :as issuing-invoice]
            [starkinfra.issuing-invoice.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox create-and-get-issuing-invoices
  (set-project)
  (testing "a created invoice can be retrieved by its id"
    (let [invoice (issuing-invoice/create {:amount (+ 1000 (rand-int 1000))})]
      (is (= (:id invoice) (:id (issuing-invoice/get (:id invoice))))))))

(deftest ^:sandbox query-issuing-invoices
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 200 (issuing-invoice/query {:limit 10
                                                      :after "2020-01-01"
                                                      :before "2020-03-01"})))
           10))))

(deftest ^:sandbox page-issuing-invoices
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(issuing-invoice/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-issuing-invoice-logs
  (set-project)
  (testing "a log carries its invoice"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:invoice fetched))))))

(deftest ^:sandbox page-issuing-invoice-logs
  (set-project)
  (testing "two pages of two log ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
