(defproject starkinfra/sdk "0.0.3"
  :description "SDK to make Clojure integrations with the Stark Bank API easier."
  :url "https://github.com/starkinfra/sdk-clojure"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [
    [org.clojure/clojure "1.10.1"]]
  :resource-paths ["resources/starkinfra-0.0.3.jar"]
  :repl-options {:init-ns starkinfra.core})
