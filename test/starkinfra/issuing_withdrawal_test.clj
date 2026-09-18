(ns starkinfra.issuing-withdrawal-test
  "Mirrors sdk-python tests/sdk/testIssuingWithdrawal.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-withdrawal :as issuing-withdrawal]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox create-and-get-issuing-withdrawals
  (set-project)
  (testing "a created withdrawal can be retrieved by its id"
    (let [withdrawal (issuing-withdrawal/create
                       {:amount 10000
                        :external-id (str "clojure-sdk-" (rand-int 1000000000))
                        :description "Sending back"})]
      (is (= (:id withdrawal) (:id (issuing-withdrawal/get (:id withdrawal))))))))

(deftest ^:sandbox query-issuing-withdrawals
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 200 (issuing-withdrawal/query {:limit 10
                                                         :after "2020-01-01"
                                                         :before "2020-03-01"})))
           10))))

(deftest ^:sandbox page-issuing-withdrawals
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(issuing-withdrawal/page %) 2 {:limit 2}))))))
