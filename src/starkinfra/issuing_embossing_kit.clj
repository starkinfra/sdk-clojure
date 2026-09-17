(ns starkinfra.issuing-embossing-kit
  "The IssuingEmbossingKit map displays information on the embossing kits
  available to your Workspace.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingEmbossingKit is created. ex: \"5656565656565656\"
    - `:name` [string]: embossing kit name. ex: \"stark-plastic-dark-001\"
    - `:designs` [list of maps]: list of IssuingDesign maps. ex: [{...}, {...}]
    - `:updated` [string]: latest update datetime for the IssuingEmbossingKit. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingEmbossingKit. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream]]))

(defn- resource []
  "issuing-embossing-kit")


(defn query
  "Receive a stream of IssuingEmbossingKit maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:design-ids` [list of strings, default nil]: list of design_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingEmbossingKit maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IssuingEmbossingKit maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:design-ids` [list of strings, default nil]: list of design_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingEmbossingKit maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn get
  "Receive a single IssuingEmbossingKit map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingEmbossingKit map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))
