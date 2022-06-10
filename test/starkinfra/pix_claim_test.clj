(ns starkinfra.pix-claim-test
  (:use [clojure.test])
  (:require [starkinfra.pix-claim :as claim]
            [starkinfra.pix-claim.log :as log]
            [starkinfra.user-test :as user]
            [clojure.java.io :as io]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.page :as page]))

(deftest query-claims
  (testing "query pix claims"
    (user/set-test-project)
    (def claims (take 200 (claim/query {:limit 10})))
    (is (<= 10 (count claims)))))

(deftest page-claims
  (testing "page pix claims"
    (user/set-test-project)
    (def get-page (fn [params] (claim/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))

(deftest query-get-claims
  (testing "query and get pix claims"
    (user/set-test-project)
    (def claims (take 200 (claim/query {:limit 10})))
    (def compare (fn [ch1, ch2] (is (:id ch1) (:id ch2))))
    (doseq [ch1 claims] (compare ch1 (claim/get (:id ch1))))))

(defn rand-int-min-max
  "max here is inclusive"
  [min max]
  (+ min (rand-int max)))

(deftest create-get-claim
  (testing "create and get pix claim"
    (user/set-test-project)
    (def created-claim (claim/create
      {
        :account-created "2022-02-01"
        :account-number "0000-1"
        :account-type "savings"
        :branch-code "0000-1"
        :name "Jamie Lannister"
        :tax-id "012.345.678-90"
        :key-id (str "+55" (rand-int-min-max 10 99) (rand-int-min-max 100000000 999999999))
      }))
    (is (nil? (:id created-claim)))))

(deftest query-get-pix-claim-logs
  (testing "query and get pix claim logs"
    (user/set-test-project)
    (def claim-logs (log/query {:limit 1 :type "solved"}))
    (is (= 1 (count claim-logs)))
    (def claim-log (log/get (:id (first claim-logs))))
    (is (map? (:claim claim-log)))
    (is (not (nil? (:id claim-log))))
    (is (string? (:created claim-log)))))

(deftest page-pix-claim-log
  (testing "page pix-claim-log"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))
