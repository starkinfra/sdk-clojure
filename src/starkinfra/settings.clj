(ns starkinfra.settings
  "Used to set options in SDK."
  (:refer-clojure :exclude [set])
  (:import [com.starkinfra Project])
  (:use [starkinfra.user]))

(defn user
  "sets a default user (project or organization) to be automatically used in all requests"
  ([user]
   (def java-project (#'starkinfra.user/get-java-user user))
   (set! (. com.starkinfra.Settings -user) java-project)))

(defn language
  "sets a default language to be automatically used in all requests. Options are en-US and pt-BR"
  ([language]
   (set! (. com.starkinfra.Settings -language) language)))

(defn- set-user-agent-override
  ([]
   (set! (. com.starkinfra.Settings -userAgentOverride) (str "Clojure-" (clojure-version) "-SDK-0.0.3"))))

(set-user-agent-override)
