(ns starkinfra.user-test
  "The sandbox credentials build and are usable as the default user."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-balance :as pix-balance]
            [starkinfra.settings :as settings]
            [starkinfra.utils.user :refer [set-organization set-project
                                           starkinfra-organization
                                           starkinfra-project]]))


(deftest ^:sandbox project-credentials
  (testing "the project builds from the environment and never points at production"
    (set-project)
    (is (= "sandbox" (:environment @starkinfra-project)))
    (is (= "project" (:type @starkinfra-project)))
    (is (= @starkinfra-project @settings/credentials))
    (is (integer? (:amount (pix-balance/get))))))

(deftest ^:sandbox organization-credentials
  (testing "the organization builds from the environment"
    (set-organization)
    (is (= "sandbox" (:environment @starkinfra-organization)))
    (is (= "organization" (:type @starkinfra-organization)))
    (is (= @starkinfra-organization @settings/credentials))))
