(ns starkinfra.issuing-design-test
  "Mirrors sdk-python tests/sdk/testIssuingDesign.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-design :as issuing-design]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox query-and-get-issuing-designs
  (set-project)
  (testing "every design queried can be retrieved by its id"
    (doseq [design (take 3 (issuing-design/query {:limit 3}))]
      (is (= (:id design) (:id (issuing-design/get (:id design))))))))

(deftest ^:sandbox page-issuing-designs
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-design/page %) 2 {:limit 2})))))

(deftest ^:sandbox pdf-issuing-design
  (set-project)
  (testing "the pdf content is a non-empty byte string"
    (let [design (first (issuing-design/query {:limit 1}))
          content (issuing-design/pdf (:id design))]
      (is (pos? (count content))))))
