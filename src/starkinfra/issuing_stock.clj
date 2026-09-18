(ns starkinfra.issuing-stock
  "The IssuingStock map represents the current stock of a certain
  IssuingDesign linked to an Embosser available to your workspace.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingStock is created. ex: \"5656565656565656\"
    - `:balance` [integer]: [EXPANDABLE] current stock balance. ex: 1000
    - `:design-id` [string]: IssuingDesign unique id. ex: \"5656565656565656\"
    - `:embosser-id` [string]: Embosser unique id. ex: \"5656565656565656\"
    - `:embosser-name` [string]: Name of the embosser that holds this stock
    - `:updated` [string]: latest update datetime for the IssuingStock. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingStock. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream]]))

(defn- resource []
  "issuing-stock")


(defn query
  "Receive a stream of IssuingStock maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:design-ids` [list of strings, default nil]: IssuingDesign unique ids. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:embosser-ids` [list of strings, default nil]: Embosser unique ids. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"balance\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingStock maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IssuingStock maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:design-ids` [list of strings, default nil]: IssuingDesign unique ids. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:embosser-ids` [list of strings, default nil]: Embosser unique ids. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"balance\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingStock maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn get
  "Receive a single IssuingStock map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `params` [map]:
      - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"balance\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingStock map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id params]
   (get-id @credentials (resource) id params))

  ([id params user]
   (get-id user (resource) id params)))
