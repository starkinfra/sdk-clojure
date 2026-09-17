(ns starkinfra.issuing-holder-test
  "Mirrors sdk-python tests/sdk/testIssuingHolder.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-holder :as issuing-holder]
            [starkinfra.issuing-holder.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-holder []
  {:name "Iron Bank S.A."
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :tax-id "012.345.678-90"
   :tags ["Traveler Employee"]
   :rules [{:name "General USD"
            :interval "day"
            :amount 100000
            :currency-code "USD"}]})


(deftest ^:sandbox create-and-get-issuing-holders
  (set-project)
  (testing "every created holder can be retrieved by its id, with expand"
    (let [holders (issuing-holder/create [(example-holder)] {:expand ["rules"]})]
      (doseq [holder holders]
        (is (= (:id holder) (:id (issuing-holder/get (:id holder) {:expand ["rules"]}))))))))

(deftest ^:sandbox query-issuing-holders
  (set-project)
  (testing "the stream honours the limit"
    (is (= 10 (count (take 200 (issuing-holder/query {:limit 10})))))))

(deftest ^:sandbox page-issuing-holders
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(issuing-holder/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-and-cancel-issuing-holder
  (set-project)
  (testing "a created holder can be renamed and then cancelled"
    (let [holder (first (issuing-holder/create [(example-holder)]))
          updated (issuing-holder/update (:id holder) {:name "Updated Name"})
          cancelled (issuing-holder/cancel (:id holder))]
      (is (= "Updated Name" (:name updated)))
      (is (= "canceled" (:status cancelled))))))

(deftest ^:sandbox query-and-get-issuing-holder-logs
  (set-project)
  (testing "a log carries its holder"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:holder fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
