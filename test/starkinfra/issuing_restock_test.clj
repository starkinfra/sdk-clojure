(ns starkinfra.issuing-restock-test
  "Mirrors sdk-python tests/sdk/testIssuingRestock.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-restock :as issuing-restock]
            [starkinfra.issuing-restock.log :as log]
            [starkinfra.issuing-stock :as issuing-stock]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-restock []
  {:count (+ 100 (rand-int 900))
   :stock-id (:id (first (issuing-stock/query {:limit 1})))})


(deftest ^:sandbox create-and-get-issuing-restocks
  (set-project)
  (testing "every created restock can be retrieved by its id"
    (let [restocks (issuing-restock/create [(example-restock)])]
      (doseq [restock restocks]
        (is (= (:id restock) (:id (issuing-restock/get (:id restock)))))))))

(deftest ^:sandbox query-issuing-restocks
  (set-project)
  (testing "the stream honours the limit"
    (is (= 5 (count (take 200 (issuing-restock/query {:limit 5})))))))

(deftest ^:sandbox page-issuing-restocks
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-restock/page %) 2 {:limit 2})))))

(deftest ^:sandbox query-and-get-issuing-restock-logs
  (set-project)
  (testing "a log carries its restock"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:restock fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (sequential? (page/get-ids #(log/page %) 2 {:limit 2})))))
