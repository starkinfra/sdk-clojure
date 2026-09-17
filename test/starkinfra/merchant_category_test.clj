(ns starkinfra.merchant-category-test
  "Mirrors sdk-python tests/sdk/testMerchantCategory.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.merchant-category :as merchant-category]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-merchant-categories
  (set-project)
  (testing "a search keyword returns matching categories"
    (let [categories (take 10 (merchant-category/query {:search "food"}))]
      (doseq [category categories]
        (is (string? (:name category)))))))
