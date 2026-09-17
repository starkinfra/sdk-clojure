(ns starkinfra.pix-chargeback-test
  "Mirrors sdk-python tests/sdk/testPixChargeback.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-chargeback :as pix-chargeback]
            [starkinfra.pix-chargeback.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-chargeback []
  {:amount 100
   :reference-id "E20018183202201201450u34sDGd19lz"
   :reason "fraud"})


(deftest ^:sandbox create-and-get-pix-chargebacks
  (set-project)
  (testing "every created chargeback can be retrieved by its id"
    (let [chargebacks (pix-chargeback/create [(example-chargeback)])]
      (doseq [chargeback chargebacks]
        (is (= (:id chargeback) (:id (pix-chargeback/get (:id chargeback)))))))))

(deftest ^:sandbox query-pix-chargebacks
  (set-project)
  (testing "the stream honours the limit"
    (is (= 4 (count (take 200 (pix-chargeback/query {:limit 4})))))))

(deftest ^:sandbox page-pix-chargebacks
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-chargeback/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-pix-chargeback
  (set-project)
  (testing "an existing chargeback can be patched"
    (let [chargeback (first (pix-chargeback/query {:limit 1}))
          updated (pix-chargeback/update (:id chargeback) {:result "accepted"})]
      (is (some? (:id updated))))))

(deftest ^:sandbox cancel-pix-chargeback
  (set-project)
  (testing "a created chargeback can be canceled"
    (let [chargeback (first (pix-chargeback/create [(example-chargeback)]))
          canceled (pix-chargeback/cancel (:id chargeback))]
      (is (= (:id chargeback) (:id canceled))))))

(deftest ^:sandbox query-and-get-pix-chargeback-logs
  (set-project)
  (testing "a log carries its chargeback"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:chargeback fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
