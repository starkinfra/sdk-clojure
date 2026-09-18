(ns starkinfra.merchant-country-test
  "Mirrors sdk-python tests/sdk/testMerchantCountry.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.merchant-country :as merchant-country]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox query-merchant-countries
  (set-project)
  (testing "a search keyword returns matching countries"
    (let [countries (take 10 (merchant-country/query {:search "brazil"}))]
      (doseq [country countries]
        (is (string? (:name country)))))))
