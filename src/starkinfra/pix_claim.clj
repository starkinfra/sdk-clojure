(ns starkinfra.pix-claim
  "PixClaims intend to transfer a PixKey from one account to another.
  When you initialize a PixClaim, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:account-created` [string]: opening date or datetime for the account claiming the PixKey. ex: \"2022-01-01\"
    - `:account-number` [string]: number of the account claiming the PixKey. ex: \"76543\"
    - `:account-type` [string]: type of the account claiming the PixKey. Options: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:branch-code` [string]: branch code of the account claiming the PixKey. ex: \"1234\"
    - `:name` [string]: holder's name of the account claiming the PixKey. ex: \"Jamie Lannister\"
    - `:tax-id` [string]: holder's taxId of the account claiming the PixKey (CPF/CNPJ). ex: \"012.345.678-90\"
    - `:key-id` [string]: id of the registered Pix Key to be claimed. ex: \"+5511989898989\". Whether the resulting claim is an \"ownership\" or \"portability\" claim depends on the key's type: only a phone key can be claimed as ownership (transferring the holder); phone, email or tax ID keys can be claimed as portability (moving the linked account without changing the holder).

  ## Parameters (optional):
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixClaim is created. ex: \"5656565656565656\"
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:status` [string]: current PixClaim status. Options: \"created\", \"failed\", \"delivered\", \"confirmed\", \"success\", \"canceled\"
    - `:type` [string]: type of Pix Claim. Options: \"ownership\", \"portability\"
    - `:key-type` [string]: keyType of the claimed PixKey. Options: \"CPF\", \"CNPJ\", \"phone\" or \"email\"
    - `:flow` [string]: direction of the Pix Claim. Options: \"in\" if you received the PixClaim or \"out\" if you created the PixClaim.
    - `:claimer-bank-code` [string]: bank_code of the Pix participant that created the PixClaim. ex: \"20018183\"
    - `:claimed-bank-code` [string]: bank_code of the account donating the PixKey. ex: \"20018183\"
    - `:created` [string]: creation datetime for the PixClaim. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: update datetime for the PixClaim. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           patch-id post-single]]))

(defn- resource []
  "pix-claim")


(defn create
  "Create a PixClaim to request the transfer of a PixKey to an account
  hosted at other Pix participants in the Stark Infra API.

  ## Parameters (required):
    - `claim` [map]: PixClaim map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixClaim map with updated attributes"
  ([claim]
   (post-single @credentials (resource) claim {}))

  ([claim user]
   (post-single user (resource) claim {})))

(defn get
  "Retrieve a PixClaim map linked to your Workspace in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixClaim map that corresponds to the given id"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixClaim maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"failed\", \"delivered\", \"confirmed\", \"success\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:type` [string, default nil]: filter for the type of retrieved PixClaims. Options: \"ownership\" or \"portability\"
    - `:key-type` [string, default nil]: filter for the PixKey type of retrieved PixClaims. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
    - `:key-id` [string, default nil]: filter PixClaims linked to a specific PixKey id. ex: \"+5511989898989\"
    - `:flow` [string, default nil]: direction of the Pix Claim. Options: \"in\" if you received the PixClaim or \"out\" if you created the PixClaim.
    - `:tags` [list of strings, default nil]: list of strings to filter retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixClaim maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixClaim maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"failed\", \"delivered\", \"confirmed\", \"success\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:type` [string, default nil]: filter for the type of retrieved PixClaims. Options: \"ownership\" or \"portability\"
    - `:key-type` [string, default nil]: filter for the PixKey type of retrieved PixClaims. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
    - `:key-id` [string, default nil]: filter PixClaims linked to a specific PixKey id. ex: \"+5511989898989\"
    - `:flow` [string, default nil]: direction of the Pix Claim. Options: \"in\" if you received the PixClaim or \"out\" if you created the PixClaim.
    - `:tags` [list of strings, default nil]: list of strings to filter retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixClaim maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a PixClaim by passing its id. You must answer an inbound PixClaim
  within 7 days of its status changing to \"delivered\": if you do not
  respond in time, Stark Infra rejects a portability claim and accepts an
  ownership claim by default (reason defaultBehavior). Only PixClaims with
  status \"delivered\" can be confirmed, and confirming permanently deletes
  the referenced PixKey from Stark Infra and the Central Bank. Only
  PixClaims with status \"delivered\" or \"confirmed\" can be canceled.

  ## Parameters (required):
    - `id` [string]: PixClaim id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:status` [string]: patched status for Pix Claim. Options: \"confirmed\" and \"canceled\"

  ## Options:
    - `:reason` [string, default \"userRequested\"]: reason why the PixClaim is being patched. Options: \"fraud\", \"userRequested\", \"accountClosure\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixClaim map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))
