(ns starkinfra.ledger-transaction
  "LedgerTransactions are used to track the balance of a given amount by inserting LedgerTransactions to them.
  They can represent a bank account, a digital wallet, an inventory product, etc.

  ## Parameters (required):
    - `:amount` [integer]: amount of the transaction. ex: 11234
    - `:ledger-id` [string]: id of the Ledger containing the transaction. ex: \"5656565656565656\"
    - `:external-id` [string]: string that must be unique among all your LedgerTransactions in a single Ledger. ex: \"my-internal-id-123456\"
    - `:source` [string]: source of the LedgerTransaction. ex: \"bank-transfer/123\"

  ## Parameters (optional):
    - `:fee` [integer]: fee applied to the LedgerTransaction. ex: 100
    - `:rules` [list of maps, default []]: list of Rule maps linked to the LedgerTransaction. Rules are used to overwrite the Ledger's rules for this transaction. ex: [{:key \"minimumBalance\" :value 0}]
      - `:key` [string]: rule to be customized, describes what Ledger behavior will be altered. ex: \"minimumBalance\", \"maximumBalance\"
      - `:value` [integer]: value of the rule. ex: 1000
    - `:metadata` [map, default {}]: map used to store additional information about the LedgerTransaction. ex: {:order-id \"123\" :order-type \"purchase\"}
    - `:tags` [list of strings, default []]: list of strings for reference when searching for LedgerTransactions. ex: [\"transfer/123\", \"savings\"]
    - `:created` [string, default nil]: datetime to backdate the transaction, used to import existing transaction history. Cannot be in the future; when creating multiple transactions in one request, their created values must be in chronological order. Defaults to the current datetime when omitted.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the LedgerTransaction is created. ex: \"5656565656565656\"
    - `:balance` [integer]: Ledger's balance after the transaction. ex: 11234"
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-multi]]))

(defn- resource []
  "ledger-transaction")


(defn create
  "Send a list of LedgerTransaction maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `transactions` [list of maps]: list of LedgerTransaction maps to be created in the Stark Infra API. You can send up to 500 maps in a single request, targeting different ledgers if needed; each is applied to its Ledger in the order sent, and the resulting balance is returned for each one.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of LedgerTransaction maps with updated attributes"
  ([transactions]
   (post-multi @credentials (resource) transactions {}))

  ([transactions user]
   (post-multi user (resource) transactions {})))

(defn get
  "Receive a single LedgerTransaction map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - LedgerTransaction map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of LedgerTransaction maps previously created in the Stark Infra API.

  ## Options:
    - `:ledger-id` [string, default nil]: id of the Ledger containing the transaction. Either `:ledger-id` or `:ids` must be provided. If both are sent, the query will be filtered by both. ex: \"5656565656565656\"
    - `:ids` [list of strings, default nil]: list of LedgerTransaction ids to filter retrieved maps. Either `:ledger-id` or `:ids` must be provided. If both are sent, the query will be filtered by both. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:flow` [string, default nil]: direction of the transaction. ex: \"in\" or \"out\"
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"transfer/123\", \"savings\"]
    - `:external-ids` [list of strings, default nil]: list of LedgerTransaction external ids to filter retrieved maps. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:limit` [integer, default 100, maximum 1000]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of LedgerTransaction maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of LedgerTransaction maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:ledger-id` [string, default nil]: id of the Ledger containing the transaction. Either `:ledger-id` or `:ids` must be provided. If both are sent, the query will be filtered by both. ex: \"5656565656565656\"
    - `:ids` [list of strings, default nil]: list of LedgerTransaction ids to filter retrieved maps. Either `:ledger-id` or `:ids` must be provided. If both are sent, the query will be filtered by both. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:flow` [string, default nil]: direction of the transaction. ex: \"in\" or \"out\"
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"transfer/123\", \"savings\"]
    - `:external-ids` [list of strings, default nil]: list of LedgerTransaction external ids to filter retrieved maps. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:limit` [integer, default 100, maximum 1000]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of LedgerTransaction maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
