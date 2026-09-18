(ns starkinfra.pix-statement
  "The PixStatement object stores information about all the transactions that
  happened on a specific day at your settlement account according to the Central Bank.
  It must be created by the user before it can be accessed.
  This feature is only available for direct participants.
  When you initialize a PixStatement, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:after` [string]: transactions that happened at this date are stored in the PixStatement, must be the same as before. ex: \"2020-03-10\"
    - `:before` [string]: transactions that happened at this date are stored in the PixStatement, must be the same as after. ex: \"2020-03-10\"
    - `:type` [string]: types of entities to include in statement. Options: [\"interchange\", \"interchangeTotal\", \"transaction\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixStatement is created. ex: \"5656565656565656\"
    - `:status` [string]: current PixStatement status. ex: \"success\" or \"failed\"
    - `:transaction-count` [integer]: number of transactions that happened during the day that the PixStatement was requested. ex: 11
    - `:chunk-count` [integer]: number of chunks the statement file is split into. ex: 2
    - `:created` [string]: creation datetime for the PixStatement. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixStatement. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-content get-id get-page get-stream
                                           post-single]]))

(defn- resource []
  "pix-statement")


(defn create
  "Create a PixStatement linked to your Workspace in the Stark Infra API.

  ## Parameters (required):
    - `statement` [map]: PixStatement map (after, before, type) to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixStatement map with updated attributes"
  ([statement]
   (post-single @credentials (resource) statement {}))

  ([statement user]
   (post-single user (resource) statement {})))

(defn get
  "Retrieve the PixStatement map linked to your Workspace in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixStatement map that corresponds to the given id"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixStatement maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixStatement maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixStatement maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your statements.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixStatement maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn csv
  "Retrieve a specific PixStatement by its id in a .csv file.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - .gzip file containing a PixStatement in .csv format, as a byte array"
  ([id]
   (get-content @credentials (resource) id "csv" {}))

  ([id user]
   (get-content user (resource) id "csv" {})))
