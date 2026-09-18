(ns starkinfra.issuing-product-test
  "Mirrors sdk-python tests/sdk/testIssuingProduct.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-product :as issuing-product]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox query-issuing-products
  (set-project)
  (testing "every product returned carries an id"
    (doseq [product (take 10 (issuing-product/query))]
      (is (some? (:id product))))))

(deftest ^:sandbox page-issuing-products
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-product/page %) 2 {:limit 2})))))
