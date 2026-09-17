(ns starkinfra.individual-account-request-test
  "Mirrors sdk-python tests/sdk/testIndividualAccountRequest.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.individual-account-request :as individual-account-request]
            [starkinfra.individual-account-request.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-address []
  {:street "Rua do Estilo Barroco"
   :number "648"
   :neighborhood "Santo Amaro"
   :city "Sao Paulo"
   :state "SP"
   :zip-code "05724005"
   :complement "Apto. 123"})

(defn- example-request []
  {:name "Walter White"
   :tax-id "012.345.678-90"
   :address (example-address)
   :income 1000000
   :birth-date "1965-09-07"
   :tags ["breaking" "bad"]})


(deftest ^:sandbox create-and-get-individual-account-requests
  (set-project)
  (testing "every created request can be retrieved by its id and carries the account type"
    (let [requests (individual-account-request/create [(example-request)])]
      (doseq [request requests]
        (is (some? (:id request)))
        (is (= "individual" (:account-type request)))
        (is (= (:id request) (:id (individual-account-request/get (:id request)))))))))

(deftest ^:sandbox individual-account-request-address-round-trips
  (set-project)
  (testing "the created request's address matches what was sent"
    (let [request (first (individual-account-request/create [(example-request)]))]
      (is (= "Rua do Estilo Barroco" (:street (:address request))))
      (is (= "05724005" (:zip-code (:address request)))))))

(deftest ^:sandbox query-individual-account-requests
  (set-project)
  (testing "filters that match nothing return nothing"
    (is (= 0 (count (individual-account-request/query {:limit 10
                                                        :status "created"
                                                        :tags ["a" "b"]
                                                        :ids ["1" "2" "3"]}))))))

(deftest ^:sandbox page-individual-account-requests
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(individual-account-request/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-individual-account-request
  (set-project)
  (testing "a freshly created request can be patched to processing"
    (let [created (first (individual-account-request/create [(example-request)]))
          updated (individual-account-request/update (:id created) {:status "processing"})]
      (is (= (:id created) (:id updated))))))

(deftest ^:sandbox query-and-get-individual-account-request-logs
  (set-project)
  (testing "a log carries its request"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:request fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
