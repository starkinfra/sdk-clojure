(ns starkinfra.card-method-test
  "Mirrors sdk-python tests/sdk/testCardMethod.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.card-method :as card-method]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-card-methods
  (set-project)
  (testing "a search keyword returns matching methods"
    (let [methods (take 10 (card-method/query {:search "token"}))]
      (doseq [method methods]
        (is (string? (:name method)))))))
