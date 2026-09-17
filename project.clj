(defproject starkinfra/sdk "0.1.0"
  :description "SDK to make Clojure integrations with the Stark Infra API easier."
  :url "https://github.com/starkinfra/sdk-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.11.4"]
                 [com.starkinfra/starkcore "0.2.0"]
                 [clj-http "3.12.3"]
                 [org.clojure/data.json "2.4.0"]
                 [com.starkbank.ellipticcurve/starkbank-ecdsa "1.0.2"]
                 [clj-time "0.15.2"]
                 [cheshire "5.10.0"]]
  ;; Offline suites are the default so a checkout with no SANDBOX_* credentials
  ;; still has a green `lein test`; the sandbox suites are opt-in.
  :test-selectors {:default (complement :sandbox)
                   :sandbox :sandbox}
  :repl-options {:init-ns starkinfra.core})
