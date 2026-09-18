(ns starkinfra.issuing-token-request
  "The IssuingTokenRequest map displays the necessary information to proceed
  with the card tokenization.

  ## Parameters (required):
    - `:card-id` [string]: card id to be tokenized. ex: \"5734340247945216\"
    - `:wallet-id` [string]: desired wallet to be integrated. Options: \"apple\", \"google\", \"merchant\"
    - `:method-code` [string]: method code. ex: \"app\" or \"manual\"

  ## Parameters (optional):
    - `:metadata` [map, default nil]: additional information you want to send along with the tokenization request. ex: {:authorization-id \"OjZAqj\"}

  ## Attributes (return-only):
    - `:content` [string]: token request content. ex: \"eyJwdWJsaWNLZXlGaW5nZXJwcmludCI6ICJlNTNiZThjZTRhYWQxNWU2OWNmMjExOTA5Mjk4YzJkOTE0O...\"
    - `:signature` [string]: token request signature. ex: \"eyJwdWJsaWNLZXlGaW5nZXJwcmludCI6ICJlNTNiZThjZTRhYWQxNWU2OWNmMjExOTA5Mjk4YzJkOTE0O...\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [post-single]]))

(defn- resource []
  "issuing-token-request")


(defn create
  "Send an IssuingTokenRequest map to Stark Infra API to create the payload to
  proceed with the card tokenization.

  ## Parameters (required):
    - `request` [map]: IssuingTokenRequest map to the API to generate the payload

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingTokenRequest map with updated attributes"
  ([request]
   (post-single @credentials (resource) request {}))

  ([request user]
   (post-single user (resource) request {})))
