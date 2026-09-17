(ns starkinfra.ledger-test
  "Mirrors sdk-python tests/sdk/testLedger.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.ledger :as ledger]
            [starkinfra.ledger.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-ledger []
  {:external-id (str "clojure-sdk-" (rand-int 1000000000))
   :tags ["savings account" "spending counter"]
   :metadata {:account-id "123"}
   :rules [{:key "minimumBalance" :value 0}]})


(deftest ^:sandbox create-and-get-ledgers
  (set-project)
  (testing "every created ledger can be retrieved by its id"
    (let [ledgers (ledger/create [(example-ledger) (example-ledger)])]
      (is (= 2 (count ledgers)))
      (doseq [entry ledgers]
        (is (= (:id entry) (:id (ledger/get (:id entry)))))))))

(deftest ^:sandbox query-ledgers
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 200 (ledger/query {:limit 10}))) 10)))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (ledger/query {:limit 10
                                   :tags ["iron" "bank"]
                                   :ids ["1" "2" "3"]
                                   :external-ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-ledgers
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(ledger/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-ledger
  (set-project)
  (testing "rules can be replaced on an existing ledger"
    (let [created (first (ledger/create [(example-ledger)]))
          updated (ledger/update (:id created) {:rules [{:key "minimumBalance" :value 100}]})]
      (is (= (:id created) (:id updated))))))

(deftest ^:sandbox query-and-get-ledger-logs
  (set-project)
  (testing "a log carries its ledger"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:ledger fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
