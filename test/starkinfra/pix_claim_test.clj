(ns starkinfra.pix-claim-test
  "Mirrors sdk-python tests/sdk/testPixClaim.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-claim :as pix-claim]
            [starkinfra.pix-claim.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-claim []
  {:account-created "2022-02-01T00:00:00+00:00"
   :account-number "5692908409716736"
   :account-type "checking"
   :branch-code "0000"
   :name "testKey"
   :tax-id "012.345.678-90"
   :key-id "+5511989898989"})


(deftest ^:sandbox create-and-get-pix-claims
  (set-project)
  (testing "a created claim can be retrieved by its id"
    (let [claim (pix-claim/create (example-claim))]
      (is (= (:id claim) (:id (pix-claim/get (:id claim))))))))

(deftest ^:sandbox query-pix-claims
  (set-project)
  (testing "the stream honours the limit"
    (is (= 4 (count (take 200 (pix-claim/query {:limit 4}))))))

  (testing "filters that match nothing return nothing"
    (is (= 0 (count (pix-claim/query {:limit 10
                                      :status "failed"
                                      :ids ["1" "2" "3"]
                                      :type "ownership"
                                      :flow "out"
                                      :key-type "cpf"
                                      :key-id "123.456.789-09"
                                      :bacen-id "ccf9bd9c-e99d-999e-bab9-b999ca999f99"}))))))

(deftest ^:sandbox page-pix-claims
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-claim/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-pix-claim
  (set-project)
  (testing "an existing claim can be patched"
    (let [claim (first (pix-claim/query {:limit 1}))
          updated (pix-claim/update (:id claim) {:status "canceled" :reason "userRequested"})]
      (is (some? (:id updated))))))

(deftest ^:sandbox query-and-get-pix-claim-logs
  (set-project)
  (testing "a log carries its claim"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:claim fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
