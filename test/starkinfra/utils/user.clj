(ns starkinfra.utils.user
  "Sandbox credentials for the ^:sandbox suites, read from the environment.
  Built lazily so requiring this namespace on a machine with no credentials
  does not throw, which is what keeps `lein test` green offline."
  (:require [starkinfra.settings :as settings]
            [starkinfra.user :as user]))


(defn- sandbox-only [environment]
  (when (= environment "production")
    (throw (ex-info "The test suite must never run against production" {:environment environment})))
  environment)

(def starkinfra-project
  (delay (user/project (sandbox-only "sandbox")
                       (System/getenv "SANDBOX_INFRA_ID")
                       (System/getenv "SANDBOX_INFRA_PRIVATE_KEY"))))

(def starkinfra-organization
  (delay (user/organization (sandbox-only "sandbox")
                            (System/getenv "SANDBOX_ORGANIZATION_ID")
                            (System/getenv "SANDBOX_ORGANIZATION_PRIVATE_KEY"))))

(defn set-project []
  (settings/user @starkinfra-project))

(defn set-organization []
  (settings/user @starkinfra-organization))

(defn bank-code []
  (System/getenv "SANDBOX_BANK_CODE"))

(defn template-id []
  (System/getenv "SANDBOX_TEMPLATE_ID"))
