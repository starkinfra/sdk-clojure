(ns starkinfra.business-account-request-test
  "Mirrors sdk-python tests/sdk/testBusinessAccountRequest.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.business-account-request :as business-account-request]
            [starkinfra.business-account-request.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-address []
  {:street "Av. Faria Lima"
   :number "2000"
   :neighborhood "Itaim Bibi"
   :city "Sao Paulo"
   :state "SP"
   :zip-code "04538-132"
   :complement "Sala 42"})

(defn- example-owners []
  [{:tax-id "012.345.678-90"
    :name "Jamie Lannister"
    :role "partner"}
   {:tax-id "812.531.960-36"
    :name "Cersei Lannister"
    :role "representative"}])

(defn- example-request []
  {:name "Stark Bank S.A."
   :tax-id "20.018.183/0001-80"
   :address (example-address)
   :revenue 100000000
   :owners (example-owners)})


(deftest ^:sandbox create-and-get-business-account-requests
  (set-project)
  (testing "every created request can be retrieved by its id and carries the account type"
    (let [requests (business-account-request/create [(example-request)])]
      (doseq [request requests]
        (is (some? (:id request)))
        (is (= "business" (:account-type request)))
        (is (= (:id request) (:id (business-account-request/get (:id request)))))))))

(deftest ^:sandbox business-account-request-owners-round-trip
  (set-project)
  (testing "each owner keeps its role and gets a per-owner validator link"
    (let [request (first (business-account-request/create [(example-request)]))
          owners (:owners request)]
      (is (= 2 (count owners)))
      (is (= #{"partner" "representative"} (set (map :role owners)))))))

(deftest ^:sandbox query-business-account-requests
  (set-project)
  (testing "filters that match nothing return nothing"
    (is (= 0 (count (business-account-request/query {:limit 10
                                                      :status "created"
                                                      :tags ["a" "b"]
                                                      :ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-business-account-requests
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(business-account-request/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-business-account-request-logs
  (set-project)
  (testing "a log carries its request"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:request fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
