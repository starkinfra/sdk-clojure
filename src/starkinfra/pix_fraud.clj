(ns starkinfra.pix-fraud
  "PixFrauds are used to report a PixKey or taxId when a fraud
  has been confirmed.
  When you initialize a PixFraud, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:external-id` [string]: unique string to prevent duplicates among your PixFrauds. ex: \"my-internal-id-123456\"
    - `:type` [string]: type of PixFraud. Options: \"identity\", \"mule\", \"scam\", \"other\"
    - `:tax-id` [string]: user tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"

  ## Parameters (optional):
    - `:key-id` [string]: marked PixKey id. ex: \"+5511989898989\"
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"fraudulent\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixFraud is created. ex: \"5656565656565656\"
    - `:bacen-id` [string]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:status` [string]: current PixFraud status. Options: \"created\", \"failed\", \"registered\", \"canceled\"
    - `:created` [string]: creation datetime for the PixFraud. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixFraud. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "pix-fraud")


(defn create
  "Create PixFraud maps in the Stark Infra API.

  ## Parameters (required):
    - `frauds` [list of maps]: list of PixFraud maps to be created in the API. You can send up to 100 PixFraud maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixFraud maps with updated attributes"
  ([frauds]
   (post-multi @credentials (resource) frauds {}))

  ([frauds user]
   (post-multi user (resource) frauds {})))

(defn get
  "Retrieve the PixFraud map linked to your Workspace in the Stark Infra API using its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixFraud map that corresponds to the given id"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixFraud maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. Options: [\"created\", \"failed\", \"registered\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:type` [list of strings, default nil]: filter for the type of retrieved PixFrauds. Options: \"identity\", \"mule\", \"scam\", \"other\"
    - `:flow` [string, default nil]: direction of the PixFraud to filter retrieved maps. ex: \"in\" or \"out\"
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"fraudulent\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixFraud maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixFraud maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. Options: [\"created\", \"failed\", \"registered\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:type` [list of strings, default nil]: filter for the type of retrieved PixFrauds. Options: \"identity\", \"mule\", \"scam\", \"other\"
    - `:flow` [string, default nil]: direction of the PixFraud to filter retrieved maps. ex: \"in\" or \"out\"
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"fraudulent\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixFraud maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn cancel
  "Cancel a PixFraud entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: PixFraud unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixFraud map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
