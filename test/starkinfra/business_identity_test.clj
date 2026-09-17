(ns starkinfra.business-identity-test
  "Mirrors sdk-python tests/sdk/testBusinessIdentity.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.business-identity :as business-identity]
            [starkinfra.business-identity.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-identity []
  {:tax-id "20.018.183/0001-80"
   :tags ["onboarding-123"]})


(deftest ^:sandbox create-and-get-business-identities
  (set-project)
  (testing "every created identity can be retrieved by its id"
    (let [identities (business-identity/create [(example-identity)])]
      (doseq [identity identities]
        (is (some? (:id identity)))
        (is (= (:id identity) (:id (business-identity/get (:id identity)))))))))

(deftest ^:sandbox query-business-identities
  (set-project)
  (testing "the stream honours the limit"
    (is (= 3 (count (take 200 (business-identity/query {:limit 3})))))))

(deftest ^:sandbox page-business-identities
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(business-identity/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-business-identity
  (set-project)
  (testing "a freshly created identity can be canceled before validation"
    (let [created (first (business-identity/create [(example-identity)]))
          canceled (business-identity/cancel (:id created))]
      (is (= (:id created) (:id canceled)))
      (is (= "canceled" (:status canceled))))))

(deftest ^:sandbox query-and-get-business-identity-logs
  (set-project)
  (testing "a log carries its identity"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:identity fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
