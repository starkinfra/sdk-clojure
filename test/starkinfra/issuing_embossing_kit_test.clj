(ns starkinfra.issuing-embossing-kit-test
  "Mirrors sdk-python tests/sdk/testIssuingEmbossingKit.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-embossing-kit :as issuing-embossing-kit]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox query-and-get-issuing-embossing-kits
  (set-project)
  (testing "every kit queried can be retrieved by its id"
    (doseq [kit (take 3 (issuing-embossing-kit/query {:limit 3}))]
      (is (= (:id kit) (:id (issuing-embossing-kit/get (:id kit))))))))

(deftest ^:sandbox page-issuing-embossing-kits
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (sequential? (page/get-ids #(issuing-embossing-kit/page %) 2 {:limit 2})))))
