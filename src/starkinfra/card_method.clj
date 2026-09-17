(ns starkinfra.card-method
  "CardMethod's codes are used to define methods filters in IssuingRules.

  ## Parameters (required):
    - `:code` [string]: method's code. Options: \"chip\", \"token\", \"server\", \"manual\", \"magstripe\", \"contactless\"

  ## Attributes (return-only):
    - `:name` [string]: method's name. ex: \"token\"
    - `:number` [string]: method's number. ex: \"81\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-stream]]))

(defn- resource []
  "card-method")


(defn query
  "Receive a stream of CardMethod maps available in the Stark Infra API.

  ## Options:
    - `:search` [string, default nil]: keyword to search for code, name or number. ex: \"token\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of CardMethod maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))
