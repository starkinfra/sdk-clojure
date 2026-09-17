(ns starkinfra.pix-balance-test
  "Mirrors sdk-python tests/sdk/testPixBalance.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-balance :as pix-balance]
            [starkinfra.utils.user :refer [set-project starkinfra-project]]))


(deftest ^:sandbox get-pix-balance
  (set-project)
  (testing "the balance amount is an integer number of cents"
    (let [balance (pix-balance/get)]
      (is (integer? (:amount balance)))
      (is (= "BRL" (:currency balance)))))

  (testing "the user may also be passed per call"
    (is (integer? (:amount (pix-balance/get {} @starkinfra-project))))))
