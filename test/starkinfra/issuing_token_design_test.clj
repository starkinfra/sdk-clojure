(ns starkinfra.issuing-token-design-test
  "Mirrors sdk-python tests/sdk/testIssuingTokenDesign.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-token-design :as issuing-token-design]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-and-get-issuing-token-designs
  (set-project)
  (testing "every queried design can be retrieved by its id"
    (let [designs (take 5 (issuing-token-design/query {:limit 5}))]
      (doseq [design designs]
        (is (= (:id design) (:id (issuing-token-design/get (:id design)))))))))

(deftest ^:sandbox page-issuing-token-designs
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(issuing-token-design/page %) 2 {:limit 2}))))))

(deftest ^:sandbox get-issuing-token-design-pdf
  (set-project)
  (testing "the pdf file is not empty"
    (let [design (first (issuing-token-design/query {:limit 1}))]
      (is (> (count (issuing-token-design/pdf (:id design))) 1000)))))
