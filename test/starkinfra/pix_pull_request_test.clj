(ns starkinfra.pix-pull-request-test
  "Mirrors sdk-python tests/sdk/testPixPullRequest.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-pull-request :as pix-pull-request]
            [starkinfra.pix-pull-request.log :as log]
            [starkinfra.pix-pull-subscription :as pix-pull-subscription]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [bank-code set-project]]))

(defn- example-request [subscription-id]
  {:amount (+ 1000 (rand-int 999000))
   :due (date/future-datetime 5)
   :end-to-end-id (end-to-end-id/create (bank-code))
   :receiver-account-number "876543-2"
   :receiver-account-type "checking"
   :receiver-bank-code (bank-code)
   :reconciliation-id (str "recon-" (rand-int 1000000000))
   :subscription-id subscription-id
   :attempt-type "default"
   :tags ["test" "pix-pull"]})

(defn- active-subscription-id []
  (:id (first (pix-pull-subscription/query {:status ["active"] :limit 1}))))


(deftest ^:sandbox create-and-get-pix-pull-requests
  (set-project)
  (testing "every created request can be retrieved by its id, given an active subscription"
    (when-let [subscription-id (active-subscription-id)]
      (let [requests (pix-pull-request/create [(example-request subscription-id)
                                                (example-request subscription-id)])]
        (is (= 2 (count requests)))
        (doseq [request requests]
          (is (= (:id request) (:id (pix-pull-request/get (:id request))))))))))

(deftest ^:sandbox query-pix-pull-requests
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 10 (pix-pull-request/query {:limit 10}))) 10)))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-pull-request/query {:limit 10
                                             :status ["created"]
                                             :tags ["iron"]
                                             :ids ["1" "2" "3"]
                                             :subscription-ids ["1" "2"]
                                             :flows ["out"]}))))))

(deftest ^:sandbox page-pix-pull-requests
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-pull-request/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-and-cancel-pix-pull-requests
  (set-project)
  (testing "a created request can be denied and a scheduled one canceled"
    (let [request (first (pix-pull-request/query {:status ["created"] :limit 1}))]
      (when request
        (let [updated (pix-pull-request/update (:id request) {:status "denied"
                                                               :reason "senderAccountClosed"})]
          (is (= (:id request) (:id updated))))))

    (let [request (first (pix-pull-request/query {:status ["created"] :limit 1}))]
      (when request
        (let [canceled (pix-pull-request/cancel (:id request) {:reason "senderUserRequested"})]
          (is (= (:id request) (:id canceled))))))))

(deftest ^:sandbox query-and-get-pix-pull-request-logs
  (set-project)
  (testing "a log carries its request"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:request fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
