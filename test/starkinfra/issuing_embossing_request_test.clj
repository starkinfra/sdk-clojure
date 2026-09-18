(ns starkinfra.issuing-embossing-request-test
  "Mirrors sdk-python tests/sdk/testIssuingEmbossingRequest.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-card :as issuing-card]
            [starkinfra.issuing-embossing-kit :as issuing-embossing-kit]
            [starkinfra.issuing-embossing-request :as issuing-embossing-request]
            [starkinfra.issuing-embossing-request.log :as log]
            [starkinfra.issuing-holder :as issuing-holder]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-request []
  (let [kit (first (issuing-embossing-kit/query {:limit 1}))
        holder (first (issuing-holder/create [{:name "Iron Bank S.A."
                                               :external-id (str "clojure-sdk-" (rand-int 1000000000))
                                               :tax-id "012.345.678-90"}]))
        card (first (issuing-card/create [{:holder-name (:name holder)
                                           :holder-tax-id (:tax-id holder)
                                           :holder-external-id (:external-id holder)
                                           :type "physical"}]))]
    {:card-id (:id card)
     :kit-id (:id kit)
     :display-name-1 "Anthony Stark"
     :shipping-city "Sao Paulo"
     :shipping-country-code "BRA"
     :shipping-district "Bela Vista"
     :shipping-service "loggi"
     :shipping-state-code "SP"
     :shipping-street-line-1 "Av. Paulista, 200"
     :shipping-street-line-2 "10 andar"
     :shipping-tracking-number (str "clojure-sdk-" (rand-int 1000000000))
     :shipping-zip-code "12345-678"}))


(deftest ^:sandbox create-and-get-issuing-embossing-requests
  (set-project)
  (testing "every created request can be retrieved by its id"
    (let [requests (issuing-embossing-request/create [(example-request)])]
      (doseq [request requests]
        (is (= (:id request) (:id (issuing-embossing-request/get (:id request)))))))))

(deftest ^:sandbox query-issuing-embossing-requests
  (set-project)
  (testing "the stream honours the limit"
    (is (= 5 (count (take 200 (issuing-embossing-request/query {:limit 5})))))))

(deftest ^:sandbox page-issuing-embossing-requests
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-embossing-request/page %) 2 {:limit 2})))))

(deftest ^:sandbox query-and-get-issuing-embossing-request-logs
  (set-project)
  (testing "a log carries its request"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:request fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (sequential? (page/get-ids #(log/page %) 2 {:limit 2})))))
