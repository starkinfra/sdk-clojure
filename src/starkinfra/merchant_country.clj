(ns starkinfra.merchant-country
  "MerchantCountry's codes are used to define countries filters in
  IssuingRules.

  ## Parameters (required):
    - `:code` [string]: country's code. ex: \"BRA\"

  ## Attributes (return-only):
    - `:name` [string]: country's name. ex: \"Brazil\"
    - `:number` [string]: country's number. ex: \"076\"
    - `:short-code` [string]: country's short code. ex: \"BR\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-stream]]))

(defn- resource []
  "merchant-country")


(defn query
  "Receive a stream of MerchantCountry maps previously created in the Stark Infra API.

  ## Options:
    - `:search` [string, default nil]: keyword to search for code, name, number or short code
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of MerchantCountry maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))
