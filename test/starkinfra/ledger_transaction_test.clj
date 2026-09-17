(ns starkinfra.ledger-transaction-test
  "Mirrors sdk-python tests/sdk/testLedgerTransaction.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.ledger :as ledger]
            [starkinfra.ledger-transaction :as ledger-transaction]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-ledger []
  {:external-id (str "clojure-sdk-" (rand-int 1000000000))})

(defn- example-transaction [ledger-id]
  {:amount (+ 1000 (rand-int 9000))
   :ledger-id ledger-id
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :source (str "account/" (rand-int 1000000))
   :tags ["savings account" "spending counter"]
   :metadata {:order-id "123"}
   :rules [{:key "minimumBalance" :value 0}]})


(deftest ^:sandbox create-and-get-ledger-transactions
  (set-project)
  (testing "every created transaction can be retrieved by its id"
    (let [ledger-id (:id (first (ledger/create [(example-ledger)])))
          transactions (ledger-transaction/create [(example-transaction ledger-id)
                                                    (example-transaction ledger-id)])]
      (is (= 2 (count transactions)))
      (doseq [transaction transactions]
        (is (= (:id transaction) (:id (ledger-transaction/get (:id transaction)))))))))

(deftest ^:sandbox query-ledger-transactions
  (set-project)
  (testing "querying by a fresh ledger id returns only its own transactions"
    (let [ledger-id (:id (first (ledger/create [(example-ledger)])))]
      (ledger-transaction/create [(example-transaction ledger-id) (example-transaction ledger-id)])
      (is (= 2 (count (ledger-transaction/query {:ledger-id ledger-id}))))))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (ledger-transaction/query {:limit 10
                                               :tags ["iron" "bank"]
                                               :ids ["1" "2" "3"]
                                               :external-ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-ledger-transactions
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (let [ledger-id (:id (first (ledger/create [(example-ledger)])))]
      (ledger-transaction/create [(example-transaction ledger-id) (example-transaction ledger-id)
                                  (example-transaction ledger-id) (example-transaction ledger-id)])
      (is (= 4 (count (page/get-ids #(ledger-transaction/page (assoc % :ledger-id ledger-id)) 2 {:limit 2})))))))
