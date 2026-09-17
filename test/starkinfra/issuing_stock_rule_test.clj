(ns starkinfra.issuing-stock-rule-test
  "Mirrors sdk-python tests/sdk/testIssuingStockRule.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-stock :as issuing-stock]
            [starkinfra.issuing-stock-rule :as issuing-stock-rule]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-rule []
  {:minimum-balance (+ 1000 (rand-int 99000))
   :stock-id (:id (first (issuing-stock/query {:limit 1})))
   :tags ["test"]
   :emails ["john.doe@enterprise.com"]
   :phones ["+55 (11) 91234 5678"]})


(deftest ^:sandbox create-and-get-issuing-stock-rules
  (set-project)
  (testing "every created rule can be retrieved by its id"
    (let [rules (issuing-stock-rule/create [(example-rule)])]
      (doseq [rule rules]
        (is (= (:id rule) (:id (issuing-stock-rule/get (:id rule)))))))))

(deftest ^:sandbox query-issuing-stock-rules
  (set-project)
  (testing "the stream honours the limit"
    (is (= 5 (count (take 200 (issuing-stock-rule/query {:limit 5})))))))

(deftest ^:sandbox page-issuing-stock-rules
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-stock-rule/page %) 2 {:limit 2})))))

(deftest ^:sandbox update-and-cancel-issuing-stock-rule
  (set-project)
  (testing "a created rule can have its minimum balance updated and then be cancelled"
    (let [rule (first (issuing-stock-rule/create [(example-rule)]))
          updated (issuing-stock-rule/update (:id rule) {:minimum-balance 20000})
          cancelled (issuing-stock-rule/cancel (:id rule))]
      (is (= 20000 (:minimum-balance updated)))
      (is (= "canceled" (:status cancelled))))))
