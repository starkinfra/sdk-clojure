(ns starkinfra.individual-identity-test
  "Mirrors sdk-python tests/sdk/testIndividualIdentity.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.individual-identity :as individual-identity]
            [starkinfra.individual-identity.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-identity []
  {:name "Walter White"
   :tax-id "012.345.678-90"
   :birth-date "1965-09-07"
   :tags ["breaking" "bad"]})


(deftest ^:sandbox create-and-get-individual-identities
  (set-project)
  (testing "every created identity can be retrieved by its id"
    (let [identities (individual-identity/create [(example-identity)])]
      (doseq [identity identities]
        (is (some? (:id identity)))
        (is (= (:id identity) (:id (individual-identity/get (:id identity)))))))))

(deftest ^:sandbox query-individual-identities
  (set-project)
  (testing "the stream honours the limit"
    (is (= 3 (count (take 200 (individual-identity/query {:limit 3})))))))

(deftest ^:sandbox page-individual-identities
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(individual-identity/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-individual-identity
  (set-project)
  (testing "a freshly created identity can be canceled before validation"
    (let [created (first (individual-identity/create [(example-identity)]))
          canceled (individual-identity/cancel (:id created))]
      (is (= (:id created) (:id canceled)))
      (is (= "canceled" (:status canceled))))))

(deftest ^:sandbox query-and-get-individual-identity-logs
  (set-project)
  (testing "a log carries its identity"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:identity fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
