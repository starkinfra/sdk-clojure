(ns starkinfra.issuing-billing-transaction-test
  "Mirrors sdk-python tests/sdk/testIssuingBillingTransaction.py. Needs
  SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-billing-transaction :as issuing-billing-transaction]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-issuing-billing-transactions
  (set-project)
  (testing "every queried billing transaction carries a string id"
    (let [transactions (take 10 (issuing-billing-transaction/query {:after "2023-01-01"
                                                                     :before "2024-03-01"
                                                                     :limit 10}))]
      (doseq [transaction transactions]
        (is (= (:id transaction) (str (:id transaction))))))))

(deftest ^:sandbox page-issuing-billing-transactions
  (set-project)
  (testing "the page carries the same shape as query"
    (let [page (issuing-billing-transaction/page {:after "2023-01-01"
                                                  :before "2024-03-01"
                                                  :limit 10})]
      (doseq [transaction (:content page)]
        (is (= (:id transaction) (str (:id transaction))))))))
