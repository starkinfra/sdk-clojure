(ns starkinfra.pix-user-test
  "Mirrors sdk-python tests/sdk/testPixUser.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-user :as pix-user]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox get-pix-user
  (set-project)
  (testing "a user's fraud statistics can be retrieved by taxId"
    (let [user (pix-user/get "01234567890")]
      (is (some? (:id user)))
      (is (sequential? (:statistics user)))))

  (testing "the key-id filter is also accepted"
    (let [user (pix-user/get "01234567890" {:key-id "+5511989898989"})]
      (is (some? (:id user))))))
