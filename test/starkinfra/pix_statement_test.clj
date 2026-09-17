(ns starkinfra.pix-statement-test
  "Mirrors sdk-python tests/sdk/testPixStatement.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-statement :as pix-statement]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-statement []
  {:after "2022-01-01"
   :before "2022-01-01"
   :type (rand-nth ["interchange" "interchangeTotal" "transaction"])})


(deftest ^:sandbox create-and-get-pix-statements
  (set-project)
  (testing "a created statement can be retrieved by its id"
    (let [statement (pix-statement/create (example-statement))]
      (is (some? (:id statement)))
      (is (= (:id statement) (:id (pix-statement/get (:id statement))))))))

(deftest ^:sandbox query-pix-statements
  (set-project)
  (testing "the stream honours the limit"
    (is (= 5 (count (take 200 (pix-statement/query {:limit 5}))))))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-statement/query {:limit 10 :ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-pix-statements
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-statement/page %) 2 {:limit 2}))))))

(deftest ^:sandbox csv-pix-statement
  (set-project)
  (testing "the .csv content for an existing statement can be downloaded"
    (let [statement (first (pix-statement/query {:limit 1}))]
      (is (some? (pix-statement/csv (:id statement)))))))
