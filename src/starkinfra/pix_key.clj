(ns starkinfra.pix-key
  "PixKeys link bank account information to key ids.
  Key ids are a convenient way to search and pass bank account information.
  When you initialize a Pix Key, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:account-created` [string]: opening date or datetime for the linked account. ex: \"2022-01-01T12:00:00+00:00\"
    - `:account-number` [string]: number of the linked account. ex: \"76543\"
    - `:account-type` [string]: type of the linked account. Options: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:branch-code` [string]: branch code of the linked account. ex: \"1234\"
    - `:name` [string]: holder's name of the linked account. ex: \"Jamie Lannister\"
    - `:tax-id` [string]: holder's taxId (CPF/CNPJ) of the linked account. ex: \"012.345.678-90\"

  ## Parameters (optional):
    - `:id` [string, default nil]: id of the registered PixKey. Allowed types are: CPF, CNPJ, phone number or email. If this parameter is not passed, an EVP will be created. ex: \"+5511989898989\"
    - `:tags` [list of strings, default []]: list of strings for reference when searching for PixKeys. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:owned` [string]: datetime when the key was owned by the holder. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:owner-type` [string]: type of the owner of the PixKey. Options: \"business\" or \"individual\"
    - `:status` [string]: current PixKey status. Options: \"created\", \"registered\", \"canceled\", \"failed\"
    - `:bank-code` [string]: bank code of the account linked to the PixKey. ex: \"20018183\"
    - `:bank-name` [string]: name of the bank that holds the account linked to the PixKey. ex: \"StarkBank\"
    - `:type` [string]: type of the PixKey. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
    - `:created` [string]: creation datetime for the PixKey. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:statistics` [list of maps]: list of maps with data regarding the Pix key statistics.
    - `:owner-statistics` [list of maps]: list of maps with data regarding the Pix user statistics."
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-single]]))

(defn- resource []
  "pix-key")


(defn create
  "Create a PixKey linked to a specific account in the Stark Infra API.

  ## Parameters (required):
    - `key` [map]: PixKey map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixKey map with updated attributes"
  ([key]
   (post-single @credentials (resource) key {}))

  ([key user]
   (post-single user (resource) key {})))

(defn get
  "Retrieve the PixKey map linked to your Workspace in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:payer-id` [string]: deprecated and ignored by the API - the payer's tax ID is now always derived automatically from the calling Workspace's own registered tax ID for Central Bank rate-limiting purposes; do not rely on this value being sent to or used by the server.

  ## Options:
    - `:end-to-end-id` [string, default nil]: central bank's unique transaction id. If the request results in the creation of a PixRequest, the same endToEndId should be used. If this parameter is not passed, one endToEndId will be automatically created. ex: \"E00002649202201172211u34srod19le\"
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"statistics\", \"owner-statistics\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixKey map that corresponds to the given id"
  ([id params]
   (get-id @credentials (resource) id params))

  ([id params user]
   (get-id user (resource) id params)))

(defn query
  "Receive a stream of PixKey maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"registered\", \"canceled\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:type` [string, default nil]: filter for the type of retrieved PixKeys. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
    - `:tax-id` [string, default nil]: filter for the tax id (CPF/CNPJ) of the holder linked to the retrieved PixKeys. ex: \"012.345.678-90\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixKey maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixKey maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"registered\", \"canceled\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:type` [string, default nil]: filter for the type of retrieved PixKeys. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
    - `:tax-id` [string, default nil]: filter for the tax id (CPF/CNPJ) of the holder linked to the retrieved PixKeys. ex: \"012.345.678-90\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixKey maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a PixKey's parameters by passing its id.

  ## Parameters (required):
    - `id` [string]: PixKey id. Allowed types are: CPF, CNPJ, phone number or email. ex: \"5656565656565656\"
    - `params` [map]:
      - `:reason` [string]: reason why the PixKey is being patched. Options: \"branchTransfer\", \"reconciliation\", \"userRequested\" or \"entryInvalid\"

  ## Options:
    - `:account-created` [string, default nil]: opening date or datetime for the account to be linked. ex: \"2022-01-01\"
    - `:account-number` [string, default nil]: number of the account to be linked. ex: \"76543\"
    - `:account-type` [string, default nil]: type of the account to be linked. Options: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:branch-code` [string, default nil]: branch code of the account to be linked. ex: \"1234\"
    - `:name` [string, default nil]: holder's name of the account to be linked. ex: \"Jamie Lannister\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixKey map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel a PixKey entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:reason` [string, default \"userRequested\"]: reason why the PixKey is being cancelled. Options: \"userRequested\", \"accountClosure\", \"fraud\", \"entryInvalid\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixKey map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id params]
   (delete-id @credentials (resource) id params))

  ([id params user]
   (delete-id user (resource) id params)))
