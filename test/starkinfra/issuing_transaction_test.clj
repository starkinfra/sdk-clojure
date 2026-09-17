(ns starkinfra.issuing-transaction-test
  "Mirrors sdk-python tests/sdk/testIssuingTransaction.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-transaction :as issuing-transaction]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-and-get-issuing-transactions
  (set-project)
  (testing "every queried transaction can be retrieved by its id"
    (let [transactions (take 10 (issuing-transaction/query {:limit 10
                                                             :after "2020-01-01"
                                                             :before "2020-03-01"}))]
      (doseq [transaction transactions]
        (is (integer? (:amount transaction)))
        (is (= (:id transaction) (:id (issuing-transaction/get (:id transaction)))))))))

(deftest ^:sandbox page-issuing-transactions
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(issuing-transaction/page %) 2
                                   {:limit 2 :after "2020-01-01" :before "2020-03-01"}))))))
