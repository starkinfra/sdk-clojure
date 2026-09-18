(ns starkinfra.issuing-withdrawal
  "The IssuingWithdrawal maps created in your Workspace return cash from your
  Issuing balance to your Banking balance.

  ## Parameters (required):
    - `:amount` [integer]: IssuingWithdrawal value in cents. Minimum = 0 (any value will be accepted). ex: 1234 (= R$ 12.34)
    - `:external-id` [string]: unique identifier for this withdrawal, used to prevent duplicate withdrawals on retry. ex: \"withdrawal-2024-001\"
    - `:description` [string]: IssuingWithdrawal description. ex: \"sending money back\"

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"tony\", \"stark\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingWithdrawal is created. ex: \"5656565656565656\"
    - `:transaction-id` [string]: Stark Bank ledger transaction ids linked to this IssuingWithdrawal
    - `:issuing-transaction-id` [string]: issuing ledger transaction ids linked to this IssuingWithdrawal
    - `:updated` [string]: latest update datetime for the IssuingWithdrawal. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingWithdrawal. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-single]]))

(defn- resource []
  "issuing-withdrawal")


(defn create
  "Send a single IssuingWithdrawal map for creation at the Stark Infra API.

  ## Parameters (required):
    - `withdrawal` [map]: IssuingWithdrawal map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingWithdrawal map with updated attributes"
  ([withdrawal]
   (post-single @credentials (resource) withdrawal {}))

  ([withdrawal user]
   (post-single user (resource) withdrawal {})))

(defn get
  "Receive a single IssuingWithdrawal map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingWithdrawal map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingWithdrawal maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingWithdrawal maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingWithdrawal maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingWithdrawal maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
