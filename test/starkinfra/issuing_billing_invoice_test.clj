(ns starkinfra.issuing-billing-invoice-test
  "Mirrors sdk-python tests/sdk/testIssuingBillingInvoice.py. Needs
  SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-billing-invoice :as issuing-billing-invoice]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-and-get-issuing-billing-invoices
  (set-project)
  (testing "every queried billing invoice can be retrieved by its id"
    (let [invoices (take 10 (issuing-billing-invoice/query {:after "2023-01-01"
                                                             :before "2024-03-01"
                                                             :limit 10}))]
      (doseq [invoice invoices]
        (is (= (:id invoice) (:id (issuing-billing-invoice/get (:id invoice)))))))))

(deftest ^:sandbox page-issuing-billing-invoices
  (set-project)
  (testing "the page carries the same shape as query"
    (let [page (issuing-billing-invoice/page {:after "2023-01-01"
                                              :before "2024-03-01"
                                              :limit 10})]
      (doseq [invoice (:content page)]
        (is (= (:id invoice) (str (:id invoice))))))))
