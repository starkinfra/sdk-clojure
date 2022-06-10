(ns starkinfra.pix-chargeback-test
  (:use [clojure.test])
  (:require [starkinfra.pix-chargeback :as chargeback]
            [starkinfra.user-test :as user]
            [clojure.java.io :as io]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.page :as page]))
(deftest query-chargebacks
    (testing "query pix chargebacks"
        (user/set-test-project)
        (def chargebacks (take 200 (chargeback/query {:limit 10})))
        (is (<= 10 (count chargebacks)))))

(deftest page-chargebacks
    (testing "page pix chargebacks"
        (user/set-test-project)
        (def get-page (fn [params] (chargeback/page params)))
        (def ids (page/get-ids get-page 1 {:limit 4}))
        (is (= 4 (count ids)))))


(deftest query-get-chargebacks
    (testing "query and get pix chargebacks"
        (user/set-test-project)
        (def chargebacks (take 200 (chargeback/query {:limit 10})))
        (def compare (fn [ch1, ch2] (is (:id ch1) (:id ch2))))
        (doseq [ch1 chargebacks] (compare ch1 (chargeback/get (:id ch1))))))