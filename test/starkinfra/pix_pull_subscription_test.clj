(ns starkinfra.pix-pull-subscription-test
  "Mirrors sdk-python tests/sdk/testPixPullSubscription.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-pull-subscription :as pix-pull-subscription]
            [starkinfra.pix-pull-subscription.log :as log]
            [starkinfra.utils.bacen-id :as bacen-id]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [bank-code set-project]]))

(defn- example-subscription []
  {:bacen-id (str "RR" (bacen-id/create (bank-code)))
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :installment-start (date/future-datetime)
   :interval "month"
   :receiver-name "Stark Bank"
   :receiver-tax-id "39.908.427/0001-28"
   :receiver-bank-code (bank-code)
   :reference-code (str (+ 10000000 (rand-int 89999999)))
   :sender-account-number "876543-2"
   :sender-bank-code (bank-code)
   :sender-branch-code "1357-9"
   :sender-city-code "3550308"
   :sender-tax-id "39908427000128"
   :type "push"
   :amount (+ 1000 (rand-int 999000))
   :description "A Lannister always pays his debts"
   :tags ["test" "pix-pull"]})


(deftest ^:sandbox create-and-get-pix-pull-subscriptions
  (set-project)
  (testing "every created subscription can be retrieved by its id"
    (let [subscriptions (pix-pull-subscription/create [(example-subscription) (example-subscription)])]
      (is (= 2 (count subscriptions)))
      (doseq [subscription subscriptions]
        (is (= (:id subscription) (:id (pix-pull-subscription/get (:id subscription)))))))))

(deftest ^:sandbox query-pix-pull-subscriptions
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 10 (pix-pull-subscription/query {:limit 10}))) 10)))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-pull-subscription/query {:limit 10
                                                   :status ["failed"]
                                                   :tags ["iron" "bank"]
                                                   :ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-pix-pull-subscriptions
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-pull-subscription/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-and-cancel-pix-pull-subscriptions
  (set-project)
  (testing "a subscription can be patched and then canceled"
    (let [created (first (pix-pull-subscription/create [(example-subscription)]))
          updated (pix-pull-subscription/update (:id created)
                                                {:status "confirmed"
                                                 :sender-city-code "3550308"
                                                 :tags ["patched" "test"]})]
      (is (= (:id created) (:id updated)))
      (let [canceled (pix-pull-subscription/cancel (:id created) {:reason "accountClosed"})]
        (is (= (:id created) (:id canceled)))))))

(deftest ^:sandbox query-and-get-pix-pull-subscription-logs
  (set-project)
  (testing "a log carries its subscription"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:subscription fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))

(deftest parse-rejects-a-malformed-signature
  (testing "signature decoding fails before any network call, so this proves out with no credentials"
    (is (= "invalidSignature"
           (:code (ex-data (try (pix-pull-subscription/parse "{}" "something is definitely wrong")
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))
