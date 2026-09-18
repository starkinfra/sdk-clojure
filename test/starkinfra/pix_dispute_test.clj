(ns starkinfra.pix-dispute-test
  "Mirrors sdk-python tests/sdk/testPixDispute.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-dispute :as pix-dispute]
            [starkinfra.pix-dispute.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-dispute []
  {:reference-id "E20018183202512191914WcfANNEIYnt"
   :method "scam"
   :operator-phone "+5511999999999"
   :operator-email "operator@example.com"})


(deftest ^:sandbox create-and-get-pix-disputes
  (set-project)
  (testing "every created dispute can be retrieved by its id"
    (let [disputes (pix-dispute/create [(example-dispute)])]
      (doseq [dispute disputes]
        (is (= (:id dispute) (:id (pix-dispute/get (:id dispute)))))))))

(deftest ^:sandbox query-pix-disputes
  (set-project)
  (testing "the stream honours the limit"
    (is (= 4 (count (take 200 (pix-dispute/query {:limit 4 :tags ["iron" "suit"]})))))))

(deftest ^:sandbox page-pix-disputes
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-dispute/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-pix-dispute
  (set-project)
  (testing "a created dispute can be canceled"
    (let [dispute (first (pix-dispute/create [(example-dispute)]))
          canceled (pix-dispute/cancel (:id dispute))]
      (is (= (:id dispute) (:id canceled))))))

(deftest ^:sandbox query-and-get-pix-dispute-logs
  (set-project)
  (testing "a log carries its dispute"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:dispute fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
