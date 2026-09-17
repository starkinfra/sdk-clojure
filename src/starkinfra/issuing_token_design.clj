(ns starkinfra.issuing-token-design
  "The IssuingTokenDesign map displays the information of the token designs
  created in your Workspace. This resource represents the existent designs
  for the cards which will be tokenized.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingTokenDesign is created. ex: \"5656565656565656\"
    - `:name` [string]: design name. ex: \"Stark Bank - White Metal\"
    - `:created` [string]: creation datetime for the IssuingTokenDesign. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the IssuingTokenDesign. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-content get-id get-page get-stream]]))

(defn- resource []
  "issuing-token-design")


(defn get
  "Receive a single IssuingTokenDesign map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingTokenDesign map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingTokenDesign maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingTokenDesign maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingTokenDesign maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingTokenDesign maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn pdf
  "Receive a single IssuingTokenDesign pdf file generated in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingTokenDesign pdf file content"
  ([id]
   (get-content @credentials (resource) id "pdf" {}))

  ([id user]
   (get-content user (resource) id "pdf" {})))
