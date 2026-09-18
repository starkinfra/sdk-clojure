(ns starkinfra.issuing-balance-test
  "Mirrors sdk-python tests/sdk/testIssuingBalance.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-balance :as issuing-balance]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox get-issuing-balance
  (set-project)
  (testing "the Workspace balance amount is an integer"
    (is (integer? (:amount (issuing-balance/get))))))
