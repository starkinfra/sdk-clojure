(ns starkinfra.issuing-embossing-request
  "The IssuingEmbossingRequest map displays the information of embossing
  requests in your Workspace.

  ## Parameters (required):
    - `:card-id` [string]: id of the IssuingCard to be embossed. ex \"5656565656565656\"
    - `:kit-id` [string]: card embossing kit id. ex \"5656565656565656\"
    - `:display-name-1` [string]: card displayed name. ex: \"ANTHONY STARK\"
    - `:shipping-city` [string]: shipping city. ex: \"NEW YORK\"
    - `:shipping-country-code` [string]: shipping country code. ex: \"US\"
    - `:shipping-district` [string]: shipping district. ex: \"NY\"
    - `:shipping-state-code` [string]: shipping state code. ex: \"NY\"
    - `:shipping-street-line-1` [string]: shipping main address. ex: \"AVENUE OF THE AMERICAS\"
    - `:shipping-street-line-2` [string]: shipping address complement. ex: \"Apt. 6\"
    - `:shipping-service` [string]: shipping service. ex: \"loggi\"
    - `:shipping-tracking-number` [string]: shipping tracking number. ex: \"5656565656565656\"
    - `:shipping-zip-code` [string]: shipping zip code. ex: \"12345-678\"

  ## Parameters (optional):
    - `:embosser-id` [string]: id of the card embosser. ex: \"5656565656565656\"
    - `:display-name-2` [string]: card displayed name. ex: \"IT Services\"
    - `:display-name-3` [string]: card displayed name. ex: \"StarkBank S.A.\"
    - `:shipping-phone` [string]: shipping phone. ex: \"+5511999999999\"
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"card\", \"corporate\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingEmbossingRequest is created. ex: \"5656565656565656\"
    - `:fee` [integer]: fee charged when IssuingEmbossingRequest is created. ex: 1000
    - `:status` [string]: status of the IssuingEmbossingRequest. ex: \"created\", \"processing\", \"success\", \"failed\"
    - `:updated` [string]: latest update datetime for the IssuingEmbossingRequest. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingEmbossingRequest. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-multi]]))

(defn- resource []
  "issuing-embossing-request")


(defn create
  "Send a list of IssuingEmbossingRequest maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `requests` [list of maps]: list of IssuingEmbossingRequest maps to be created in the API. You can send up to 100 maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of IssuingEmbossingRequest maps with updated attributes"
  ([requests]
   (post-multi @credentials (resource) requests {}))

  ([requests user]
   (post-multi user (resource) requests {})))

(defn query
  "Receive a stream of IssuingEmbossingRequest maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:card-ids` [list of strings, default nil]: list of card_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingEmbossingRequest maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IssuingEmbossingRequest maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:card-ids` [list of strings, default nil]: list of card_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingEmbossingRequest maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn get
  "Receive a single IssuingEmbossingRequest map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingEmbossingRequest map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))
