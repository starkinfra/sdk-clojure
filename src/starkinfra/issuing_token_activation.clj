(ns starkinfra.issuing-token-activation
  "The IssuingTokenActivation map displays the necessary information to
  proceed with the card tokenization. You will receive this map at your
  registered URL to notify you which method your user want to receive the
  activation code. The POST request must be answered with no content,
  within 2 seconds, and with an HTTP status code 200. After that, you may
  generate the activation code and send it to the cardholder.

  ## Attributes (return-only):
    - `:card-id` [string]: card ID which the token is bounded to. ex: \"5656565656565656\"
    - `:token-id` [string]: token unique id. ex: \"5656565656565656\"
    - `:tags` [list of strings]: tags to filter retrieved map. ex: [\"tony\", \"stark\"]
    - `:activation-method` [map]: map object with \"type\" and \"value\" string pairs"
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.parse :refer [parse-and-verify]]))

(defn parse
  "Create a single verified IssuingTokenActivation request from a content string.
  Use this method to parse and verify the authenticity of the request received
  at the informed endpoint. Activation requests are posted to your registered
  endpoint whenever IssuingTokenActivations are received. If the provided
  digital signature does not check out with the Stark Infra public key, an
  ex-info carrying `:code \"invalidSignature\"` is thrown.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - parsed IssuingTokenActivation map"
  ([content signature]
   (parse-and-verify content signature @credentials nil))

  ([content signature user]
   (parse-and-verify content signature user nil)))
