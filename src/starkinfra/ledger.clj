(ns starkinfra.ledger
  "Ledgers are used to track the balance of a given amount by inserting LedgerTransactions to them.
  They can represent a bank account, a digital wallet, an inventory product, etc.

  ## Parameters (required):
    - `:external-id` [string]: string that must be unique among all your Ledgers. ex: \"my-internal-id-123456\"

  ## Parameters (optional):
    - `:rules` [list of maps, default []]: list of Rule maps linked to the Ledger. Rules are used to limit the balance of the Ledger. ex: [{:key \"minimumBalance\" :value 0}]
      - `:key` [string]: rule to be customized, describes what Ledger behavior will be altered. ex: \"minimumBalance\", \"maximumBalance\"
      - `:value` [integer]: value of the rule. ex: 1000
    - `:tags` [list of strings, default []]: list of strings for reference when searching for Ledgers. ex: [\"account/123\", \"savings\"]
    - `:metadata` [map, default {}]: map used to store additional information about the Ledger. ex: {:account-id \"123\" :account-type \"savings\"}

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the Ledger is created. ex: \"5656565656565656\"
    - `:created` [string]: creation datetime for the Ledger. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the Ledger. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream patch-id
                                           post-multi]]))

(defn- resource []
  "ledger")


(defn create
  "Send a list of Ledger maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `ledgers` [list of maps]: list of Ledger maps to be created in the Stark Infra API. You can send up to 100 Ledger maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of Ledger maps with updated attributes"
  ([ledgers]
   (post-multi @credentials (resource) ledgers {}))

  ([ledgers user]
   (post-multi user (resource) ledgers {})))

(defn get
  "Receive a single Ledger map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - Ledger map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of Ledger maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:ids` [list of strings, default nil]: list of Ledger ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:external-ids` [list of strings, default nil]: list of Ledger external ids to filter retrieved maps. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"account/123\", \"savings\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of Ledger maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 Ledger maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:ids` [list of strings, default nil]: list of Ledger ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:external-ids` [list of strings, default nil]: list of Ledger external ids to filter retrieved maps. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"account/123\", \"savings\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of Ledger maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a Ledger by passing id.

  ## Parameters (required):
    - `id` [string]: Ledger id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:rules` [list of maps, default nil]: list of Rule maps linked to the Ledger. Rules are used to limit the balance of the Ledger. ex: [{:key \"minimumBalance\" :value 0}]
      - `:tags` [list of strings, default nil]: list of strings for reference when searching for Ledgers. ex: [\"account/123\", \"savings\"]
      - `:metadata` [map, default nil]: map used to store additional information about the Ledger. ex: {:account-id \"123\" :account-type \"savings\"}

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target Ledger map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))
