(ns starkinfra.user-test
  (:use [clojure.test])
  (:require [starkinfra.user]
            [starkinfra.settings]))

(deftest set-test-project
  (testing "Set default user"
    (-> 
      (starkinfra.user/project
        "sandbox"
        (System/getenv "DEVELOPMENT_CORA_ID"); "9999999999999999"
        (System/getenv "DEVELOPMENT_PRIVATE_KEY")); "-----BEGIN EC PRIVATE KEY-----\nMHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA\nCqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y\nxX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=\n-----END EC PRIVATE KEY-----"
      (starkinfra.settings/user))))

(deftest get-test-organization
  (testing "Get organization user"
     (starkinfra.user/organization
      "sandbox"
      (System/getenv "SANDBOX_ORGANIZATION_ID"); "9999999999999999"
      (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY")))); "-----BEGIN EC PRIVATE KEY-----\nMHUCAQEEIUozJdDjfNVL9ulX1CmRW7a7TgmeaFsem7G5GzFAyky2HaAHBgUrgQQA\nCqFEA0IABJlS4omSpIcq/MC1a39wProUxPlpcsirelSHOzGmwKJ4ZtYHhW7bYr1Y\nxX4Ae2b2ff/v/GNgn3nSsJ73QaUgn7s=\n-----END EC PRIVATE KEY-----"