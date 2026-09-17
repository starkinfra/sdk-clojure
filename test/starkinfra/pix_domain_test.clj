(ns starkinfra.pix-domain-test
  "Mirrors sdk-python tests/sdk/testPixDomain.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-domain :as pix-domain]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox query-pix-domains
  (set-project)
  (testing "every registered domain carries a name"
    (doseq [domain (pix-domain/query)]
      (is (some? (:name domain))))))
