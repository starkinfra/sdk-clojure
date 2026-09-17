(ns starkinfra.credit-signer
  "CreditNote signer's information.

  ## Parameters (required):
    - `:name` [string]: signer's name. ex: \"Tony Stark\"
    - `:contact` [string]: signer's contact information. ex: \"tony@starkindustries.com\"
    - `:method` [string]: delivery method for the contract. Options: \"link\" (signing link sent to the contact), \"token\" (signing token sent to the contact), \"server\" and \"organization\" (automatic signatures, no contact delivery).

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the CreditSigner is created. ex: \"5656565656565656\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [patch-id]]))

(defn- resource []
  "credit-signer")


(defn resend-token
  "Resend token to a specific signer.

  ## Parameters (required):
    - `signer-id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - CreditSigner map with updated attributes"
  ([signer-id]
   (patch-id @credentials (resource) {:is-sent false} signer-id))

  ([signer-id user]
   (patch-id user (resource) {:is-sent false} signer-id)))
