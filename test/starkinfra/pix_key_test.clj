(ns starkinfra.pix-key-test
    (:use [clojure.test])
    (:require [starkinfra.pix-key :as key]
                [starkinfra.pix-key.log :as log]
                [starkinfra.user-test :as user]
                [clojure.java.io :as io]
                [starkinfra.utils.date :as date]
                [starkinfra.utils.page :as page])
    (:import [com.starkinfra.utils EndToEndId]))


(deftest query-keys
  (testing "query pix keys"
    (user/set-test-project)
    (def keys (take 200 (key/query {:limit 10})))
    (is (<= (count keys) 10))))

(deftest page-keys
  (testing "page pix keys"
    (user/set-test-project)
    (def get-page (fn [params] (key/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))

(deftest query-cancel-keys
  (testing "query and cancel pix keys"
    (user/set-test-project)
    (def keys (take 200 (key/query {:limit 10 :status "registered"})))
    (if (>= (count keys) 1)
      (def canceled-key (key/cancel (:id (first keys))))
      (is (:status canceled-key) "canceled"))))

(deftest query-get-keys
  (testing "query and get pix keys"
    (user/set-test-project)
    (def keys (take 200 (key/query {:limit 10 :status "registered"})))
    (def selected-key (first keys))
    (if (not (nil? selected-key))
      (def get-key (key/get (:id selected-key) "012.345.678-90")))))

(defn rand-int-min-max
  "max here is inclusive"
  [min max]
  (+ min (rand-int max)))

(deftest create-key
  (testing "create pix key"
    (user/set-test-project)
    (def created-key (key/create
        {
            :account-created "2022-02-01"
            :account-number "0000-1"
            :account-type "savings"
            :branch-code "0000-1"
            :name "testClaim"
            :tax-id "012.345.678-90"
            :id (str "+55" (rand-int-min-max 10 99) (rand-int-min-max 100000000 999999999))
        }
    ))
    (if (not (nil? created-key))
      (is (= (:status created-key) "created")))))

(deftest query-update-key
    (testing "query and update pix key"
        (user/set-test-project)
        (def pix-key (first (key/query {:limit 1 :status "created"})))
        (if (not (nil? pix-key))
          ((def id (:id  pix-key))
          (def reason (rand-nth ["branchTransfer" "reconciliation"]))
          (def - `:updated`-key (key/update id reason {:name "Arya Stark"}))
          (is (:name - `:updated`-key) "Arya Stark")
          (is (:id - `:updated`-key) id)))))

(deftest query-get-pix-key-logs
  (testing "query and get pix key logs"
    (user/set-test-project)
    (def key-logs (log/query {:limit 1 :type "solved"}))
    (if (= 1 (count key-logs))
      ((def key-log (log/get (:id (first key-logs))))
        (is (not (nil? (:id key-log))))))))

(deftest page-pix-key-log
  (testing "page pix-key-log"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))
