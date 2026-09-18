(ns starkinfra.pix-key-holmes
  "PixKeyHolmes are used to investigate the registration status of a Pix Key
  in the Central Bank's DICT.
  When you initialize a PixKeyHolmes, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:key-id` [string]: Pix Key to be investigated. ex: \"+5511989898989\", \"11.222.333/0001-00\", \"valid@sandbox.com\"

  ## Parameters (optional):
    - `:tags` [list of strings, default []]: list of strings for reference when searching for PixKeyHolmes. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixKeyHolmes is created. ex: \"5656565656565656\"
    - `:result` [string]: result of the investigation. Options: \"registered\", \"unregistered\". Empty/nil until status is \"solved\".
    - `:status` [string]: current PixKeyHolmes status. ex: \"created\", \"solving\", \"solved\", \"failed\"
    - `:created` [string]: creation datetime for the PixKeyHolmes. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixKeyHolmes. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-page get-stream post-multi]]))

(defn- resource []
  "pix-key-holmes")


(defn create
  "Send a list of PixKeyHolmes maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `holmes` [list of maps]: list of PixKeyHolmes maps to be created in the API. You can send up to 100 PixKeyHolmes maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixKeyHolmes maps with updated attributes"
  ([holmes]
   (post-multi @credentials (resource) holmes {}))

  ([holmes user]
   (post-multi user (resource) holmes {})))

(defn query
  "Receive a stream of PixKeyHolmes maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"solving\", \"solved\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixKeyHolmes maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixKeyHolmes maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"solving\", \"solved\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixKeyHolmes maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
