(ns starkinfra.pix-reversal-test
    (:use [clojure.test])
    (:require   [starkinfra.pix-reversal :as reversal]
                [starkinfra.pix-request :as request]
                [starkinfra.pix-reversal.log :as log]
                [starkinfra.user-test :as user]
                [clojure.java.io :as io]
                [starkinfra.utils.date :as date]
                [starkinfra.utils.page :as page])
    (:import [com.starkinfra.utils EndToEndId]
             [java.util UUID]))


(deftest query-reversals
  (testing "query pix reversals"
    (user/set-test-project)
    (def reversals (take 200 (reversal/query {:limit 10})))
    (is (<= 10 (count reversals)))))

(deftest page-reversals
  (testing "page pix reversals"
    (user/set-test-project)
    (def get-page (fn [params] (reversal/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))

(deftest query-get-reversals
  (testing "query and get pix reversals"
    (user/set-test-project)
    (def reversals (take 200 (reversal/query {:limit 10})))
    (def id (:id (first reversals)))
    (is (= id (:id (reversal/get id))))))

(deftest create-get-reversal
  (testing "create and get pix reversal"
    (user/set-test-project)
    (def end-to-end-id (:end-to-end-id (first (request/query {:limit 1 :status "success"}))))
    (def external-id (str "clj-" (UUID/randomUUID)))
    (def created-reversal (reversal/create
        [{
            :amount 10000
            :external-id external-id
            :sender-account-number "5692908409716736"
            :sender-branch-code "0001"
            :sender-account-type "checking"
            :sender-name "Jon Snow"
            :sender-tax-id "34.052.649/0001-78"
            :receiver-bank-code "34052649"
            :receiver-account-number "5692908409716736"
            :receiver-branch-code "0001"
            :receiver-account-type "checking"
            :receiver-name "Jamie Lamister"
            :receiver-tax-id "34.052.649/0001-78"
            :end-to-end-id end-to-end-id
            :reason "fraud"
        }]
    ))
    (is (not (nil? (:id (first created-reversal)))))))

(deftest query-get-pix-reversal-logs
  (testing "query and get pix reversal logs"
    (user/set-test-project)
    (def reversal-logs (log/query {:limit 1 :type "success"}))
    (if (= 1 (count reversal-logs))
      ((def reversal-log (log/get (:id (first reversal-logs))))
      (is (not (nil? (:id reversal-log))))
      (is (string? (:created reversal-log)))))))

(deftest page-pix-reversal-log
  (testing "page pix-reversal-log"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))
