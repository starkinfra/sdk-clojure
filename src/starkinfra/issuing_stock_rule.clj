(ns starkinfra.issuing-stock-rule
  "The IssuingStockRule map displays the notification rules of a specific
  IssuingStock. When the stock balance reaches the minimum balance, the
  recipients informed in the rule are notified.

  ## Parameters (required):
    - `:minimum-balance` [integer]: stock balance threshold that triggers a notification. ex: 10000
    - `:stock-id` [string]: IssuingStock unique id to which the rule is linked. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"card\", \"corporate\"]
    - `:emails` [list of strings, default nil]: list of up to 10 emails to notify when the stock reaches minimum_balance. At least one of emails or phones is required. ex: [\"john.doe@enterprise.com\"]
    - `:phones` [list of strings, default nil]: list of up to 10 phone numbers to notify when the stock reaches minimum_balance. At least one of emails or phones is required. ex: [\"+55 (11) 91234 5678\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingStockRule is created. ex: \"5656565656565656\"
    - `:status` [string]: current IssuingStockRule status. ex: \"active\", \"canceled\"
    - `:updated` [string]: latest update datetime for the IssuingStockRule. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingStockRule. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "issuing-stock-rule")


(defn create
  "Send a list of IssuingStockRule maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `rules` [list of maps]: list of IssuingStockRule maps to be created in the API

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of IssuingStockRule maps with updated attributes"
  ([rules]
   (post-multi @credentials (resource) rules {}))

  ([rules user]
   (post-multi user (resource) rules {})))

(defn get
  "Receive a single IssuingStockRule map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingStockRule map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingStockRule maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"active\", \"canceled\"]
    - `:stock-ids` [list of strings, default nil]: list of stock_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"card\", \"corporate\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingStockRule maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IssuingStockRule maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call.
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"active\", \"canceled\"]
    - `:stock-ids` [list of strings, default nil]: list of stock_ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"card\", \"corporate\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingStockRule maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update an IssuingStockRule by passing its id.

  ## Parameters (required):
    - `id` [string]: IssuingStockRule id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `params` [map]:
      - `:minimum-balance` [integer, default nil]: stock balance threshold that triggers a notification. ex: 10000
      - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"card\", \"corporate\"]
      - `:emails` [list of strings, default nil]: list of emails to be notified when the stock reaches the minimum balance. ex: [\"john.doe@enterprise.com\"]
      - `:phones` [list of strings, default nil]: list of phones to be notified when the stock reaches the minimum balance. ex: [\"+55 (11) 91234 5678\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target IssuingStockRule with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel an IssuingStockRule entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: IssuingStockRule unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled IssuingStockRule map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
