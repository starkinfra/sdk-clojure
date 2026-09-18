(ns starkinfra.settings
  "Used to set options in the SDK.")

;; The single source of truth for the version string: project.clj and the
;; User-Agent both have to agree with it, and `starkinfra.utils-test` fails the
;; build when project.clj drifts away from this def.
(def sdk-version "0.1.0")

(def host "infra")
(def api-version "v2")
(def timeout 15)

(def credentials (atom nil))
(def error-lang (atom "en-US"))


(defn user
  "Sets a default user (project or organization) to be automatically used in all requests.

  ## Parameters (required):
    - `user` [map]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization"
  [user]
  (reset! credentials user))

(defn language
  "Sets a default language to be automatically used in all requests.
   Options are \"en-US\" and \"pt-BR\".

  ## Parameters (required):
    - `language` [string]: error language. ex: \"pt-BR\""
  [language]
  (reset! error-lang language))
