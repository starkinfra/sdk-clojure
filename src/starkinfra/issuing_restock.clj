(ns starkinfra.issuing-restock
  "The IssuingRestock map displays the information of the restock orders
  created in your Workspace. This resource places a restock order for a
  specific IssuingStock map.

  ## Parameters (required):
    - `:count` [integer]: number of restocks to be restocked. ex: 100
    - `:stock-id` [string]: IssuingStock unique id ex: \"5136459887542272\"

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"card\", \"corporate\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingRestock is created. ex: \"5656565656565656\"
    - `:status` [string]: current IssuingRestock status. ex: \"created\", \"processing\", \"confirmed\"
    - `:updated` [string]: latest update datetime for the IssuingRestock. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingRestock. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-multi]]))

(defn- resource []
  "issuing-restock")


(defn create
  "Send a list of IssuingRestock maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `restocks` [list of maps]: list of IssuingRestock maps to be created in the API. You can send up to 100 maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of IssuingRestock maps with updated attributes"
  ([restocks]
   (post-multi @credentials (resource) restocks {}))

  ([restocks user]
   (post-multi user (resource) restocks {})))

(defn query
  "Receive a stream of IssuingRestock maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"confirmed\"]
    - `:stock-ids` [list of strings, default nil]: list of stock_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"card\", \"corporate\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingRestock maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IssuingRestock maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call.
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"confirmed\"]
    - `:stock-ids` [list of strings, default nil]: list of stock_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"card\", \"corporate\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingRestock maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn get
  "Receive a single IssuingRestock map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingRestock map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))
