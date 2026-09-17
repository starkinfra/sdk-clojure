(ns starkinfra.credit-holmes-test
  "Mirrors sdk-python tests/sdk/testCreditHolmes.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.credit-holmes :as credit-holmes]
            [starkinfra.credit-holmes.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-holmes []
  {:tax-id "012.345.678-90"
   :competence "2022-10"
   :tags ["test"]})


(deftest ^:sandbox create-and-get-credit-holmes
  (set-project)
  (testing "every created holmes can be retrieved by its id"
    (let [holmes (credit-holmes/create [(example-holmes) (example-holmes)])]
      (is (= 2 (count holmes)))
      (doseq [sherlock holmes]
        (is (= (:id sherlock) (:id (credit-holmes/get (:id sherlock)))))))))

(deftest ^:sandbox query-credit-holmes
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 200 (credit-holmes/query {:limit 1 :tags ["test"]}))) 1))))

(deftest ^:sandbox page-credit-holmes
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(credit-holmes/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-credit-holmes-logs
  (set-project)
  (testing "a log carries its holmes"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:holmes fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
