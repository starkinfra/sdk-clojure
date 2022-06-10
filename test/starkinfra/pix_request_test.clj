(ns starkinfra.pix-request-test
    (:use [clojure.test])
    (:require [starkinfra.pix-request :as request]
                [starkinfra.pix-request.log :as log]
                [starkinfra.user-test :as user]
                [clojure.java.io :as io]
                [starkinfra.utils.date :as date]
                [starkinfra.utils.page :as page])
    (:import [com.starkinfra.utils EndToEndId]
             [java.util UUID]))


(deftest query-requests
  (testing "query pix requests"
    (user/set-test-project)
    (def requests (take 200 (request/query {:limit 10})))
    (is (<= 10 (count requests)))))

(deftest page-requests
  (testing "page pix requests"
    (user/set-test-project)
    (def get-page (fn [params] (request/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))

(deftest query-get-requests
  (testing "query and get pix requests"
    (user/set-test-project)
    (def requests (take 200 (request/query {:limit 10})))
    (def compare (fn [ch1, ch2] (is (:id ch1) (:id ch2))))
    (doseq [ch1 requests] (compare ch1 (request/get (:id ch1))))))

(deftest create-get-request
  (testing "create and get pix request"
    (user/set-test-project)
    (def end-to-end-id (EndToEndId/create "35547753"))
    (def external-id (str "clj-" (UUID/randomUUID)))
    (def created-request (request/create
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
        }]
    ))
    (is (not (nil? (:id (first created-request)))))))

(deftest query-get-pix-request-logs
  (testing "query and get pix request logs"
    (user/set-test-project)
    (def request-logs (log/query {:limit 1 :type "solved"}))
    (is (= 1 (count request-logs)))
    (def request-log (log/get (:id (first request-logs))))
    (is (map? (:request request-log)))
    (is (not (nil? (:id request-log))))
    (is (string? (:created request-log)))))

(deftest page-pix-request-log
  (testing "page pix-request-log"
    (user/set-test-project)
    (def get-page (fn [params] (log/page params)))
    (def ids (page/get-ids get-page 1 {:limit 4}))
    (is (= 4 (count ids)))))
