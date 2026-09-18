(ns starkinfra.issuing-stock-test
  "Mirrors sdk-python tests/sdk/testIssuingStock.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-stock :as issuing-stock]
            [starkinfra.issuing-stock.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox query-and-get-issuing-stocks
  (set-project)
  (testing "every stock queried can be retrieved by its id, with expand"
    (doseq [stock (take 3 (issuing-stock/query {:limit 3 :expand ["balance"]}))]
      (let [fetched (issuing-stock/get (:id stock) {:expand ["balance"]})]
        (is (= (:id stock) (:id fetched)))
        (is (integer? (:balance fetched)))))))

(deftest ^:sandbox page-issuing-stocks
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-stock/page %) 2 {:limit 2})))))

(deftest ^:sandbox query-and-get-issuing-stock-logs
  (set-project)
  (testing "a log carries its stock"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:stock fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (sequential? (page/get-ids #(log/page %) 2 {:limit 2})))))
