(ns starkinfra.pix-fraud-test
  "Mirrors sdk-python tests/sdk/testPixFraud.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-fraud :as pix-fraud]
            [starkinfra.pix-fraud.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-fraud []
  {:external-id (str "clojure-sdk-" (rand-int 1000000000))
   :type "mule"
   :tax-id "012.345.678-90"})


(deftest ^:sandbox create-and-get-pix-frauds
  (set-project)
  (testing "every created fraud can be retrieved by its id"
    (let [frauds (pix-fraud/create [(example-fraud)])]
      (doseq [fraud frauds]
        (is (= (:id fraud) (:id (pix-fraud/get (:id fraud)))))))))

(deftest ^:sandbox query-pix-frauds
  (set-project)
  (testing "the stream honours the limit"
    (is (= 4 (count (take 200 (pix-fraud/query {:limit 4})))))))

(deftest ^:sandbox page-pix-frauds
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-fraud/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-pix-fraud
  (set-project)
  (testing "a created fraud can be canceled"
    (let [fraud (first (pix-fraud/create [(example-fraud)]))
          canceled (pix-fraud/cancel (:id fraud))]
      (is (= (:id fraud) (:id canceled))))))

(deftest ^:sandbox query-and-get-pix-fraud-logs
  (set-project)
  (testing "a log carries its fraud"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:fraud fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
