(ns starkinfra.individual-identity
  "An IndividualIdentity represents an individual to be validated. Once all the information required to
  validate the individual has been provided, it can be sent to validation by patching its status to processing.
  When you initialize an IndividualIdentity, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: individual's full name. ex: \"Edward Stark\".
    - `:tax-id` [string]: individual's tax ID (CPF). ex: \"594.739.480-42\"

  ## Parameters (optional):
    - `:birth-date` [string, default nil]: individual's birth date. ex: \"2012-03-06\"
    - `:tags` [list of strings, default []]: list of strings for reference when searching for IndividualIdentities. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the IndividualIdentity is created. ex: \"5656565656565656\"
    - `:status` [string]: current status of the IndividualIdentity. ex: \"created\", \"canceled\", \"processing\", \"failed\", \"success\"
    - `:created` [string]: creation datetime for the IndividualIdentity. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "individual-identity")


(defn create
  "Send a list of IndividualIdentity maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `identities` [list of maps]: list of IndividualIdentity maps to be created in the API

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of IndividualIdentity maps with updated attributes"
  ([identities]
   (post-multi @credentials (resource) identities {}))

  ([identities user]
   (post-multi user (resource) identities {})))

(defn get
  "Receive a single IndividualIdentity map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IndividualIdentity map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IndividualIdentity maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"canceled\", \"processing\", \"failed\", \"success\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IndividualIdentity maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IndividualIdentity maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"canceled\", \"processing\", \"failed\", \"success\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IndividualIdentity maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update an IndividualIdentity by passing id.

  ## Parameters (required):
    - `id` [string]: IndividualIdentity id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:status` [string]: you may send the IndividualIdentity to validation by passing \"processing\" in the status

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target IndividualIdentity map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel an IndividualIdentity entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: IndividualIdentity unique id. ex: \"6306109539221504\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled IndividualIdentity map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
