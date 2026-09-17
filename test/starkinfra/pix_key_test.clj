(ns starkinfra.pix-key-test
  "Mirrors sdk-python tests/sdk/testPixKey.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-key :as pix-key]
            [starkinfra.pix-key.log :as log]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [bank-code set-project]]))

(defn- example-key []
  {:account-created "2022-01-01T12:00:00+00:00"
   :account-number (str (+ 10000 (rand-int 89999)))
   :account-type "savings"
   :branch-code (str (+ 1000 (rand-int 8999)))
   :name "Jamie Lannister"
   :tax-id "012.345.678-90"})


(deftest ^:sandbox create-pix-keys
  (set-project)
  (testing "an EVP is created when no id is passed"
    (let [created (pix-key/create (example-key))]
      (is (some? (:id created)))
      (is (= "evp" (:type created))))))

(deftest ^:sandbox query-pix-keys
  (set-project)
  (testing "the stream honours the limit"
    (is (= 10 (count (take 200 (pix-key/query {:limit 10}))))))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-key/query {:limit 10
                                    :status "failed"
                                    :tags ["iron" "bank"]
                                    :ids ["+5511988887777"]
                                    :type "cpf"}))))))

(deftest ^:sandbox page-pix-keys
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-key/page %) 2 {:limit 2}))))))

(deftest ^:sandbox get-pix-key
  (set-project)
  (testing "a key is retrieved by id, with the payer id and an end-to-end id"
    (let [queried (first (pix-key/query {:limit 1}))
          fetched (pix-key/get (:id queried) {:payer-id "012.345.678-90"
                                              :end-to-end-id (end-to-end-id/create (bank-code))})]
      (is (= (:id queried) (:id fetched)))))

  (testing "expand returns the statistics blocks"
    (let [queried (first (pix-key/query {:limit 1}))
          fetched (pix-key/get (:id queried) {:payer-id "012.345.678-90"
                                              :expand ["statistics" "owner-statistics"]})]
      (is (= (:id queried) (:id fetched)))
      (is (some? (:statistics fetched)))
      (is (some? (:owner-statistics fetched))))))

(deftest ^:sandbox update-pix-key
  (set-project)
  (testing "the linked account information can be patched"
    (let [registered (first (pix-key/query {:status "registered" :type "phone" :limit 1}))]
      (when registered
        (let [name "Jamie Lannister"
              updated (pix-key/update (:id registered)
                                      {:reason "userRequested"
                                       :account-created "2022-01-01"
                                       :account-number (str (+ 10000 (rand-int 89999)))
                                       :account-type "checking"
                                       :branch-code (str (+ 1000 (rand-int 8999)))
                                       :name name})]
          (is (= name (:name updated))))))))

(deftest ^:sandbox cancel-pix-key
  (set-project)
  (testing "a registered key is cancelled with a reason"
    (let [registered (first (pix-key/query {:status "registered" :limit 1}))
          cancelled (pix-key/cancel (:id registered) {:reason "userRequested"})]
      (is (= (:id registered) (:id cancelled)))))

  (testing "an invalid reason is refused by the API"
    (let [registered (first (pix-key/query {:status "registered" :limit 1}))]
      (is (some? (try (pix-key/cancel (:id registered) {:reason "invalid_reason"})
                      nil
                      (catch clojure.lang.ExceptionInfo e e)))))))

(deftest ^:sandbox query-and-get-pix-key-logs
  (set-project)
  (testing "a log carries its key"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:key fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
