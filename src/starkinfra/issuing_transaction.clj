(ns starkinfra.issuing-transaction
  "The IssuingTransaction map created in your Workspace to represent each
  balance shift.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingTransaction is created. ex: \"5656565656565656\"
    - `:amount` [integer]: IssuingTransaction value in cents. ex: 1234 (= R$ 12.34)
    - `:balance` [integer]: balance amount of the Workspace at the instant of the Transaction in cents. ex: 200 (= R$ 2.00)
    - `:description` [string]: IssuingTransaction description. ex: \"Buying food\"
    - `:source` [string]: source of the transaction. ex: \"issuing-purchase/5656565656565656\"
    - `:tags` [list of strings]: list of strings inherited from the source resource. ex: [\"tony\", \"stark\"]
    - `:created` [string]: creation datetime for the IssuingTransaction. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream]]))

(defn- resource []
  "issuing-transaction")


(defn get
  "Receive a single IssuingTransaction map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingTransaction map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingTransaction maps previously created in the Stark Infra API.

  ## Options:
    - `:source` [string, default nil]: source of the transaction to filter retrieved maps. ex: \"issuing-purchase/5656565656565656\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:ids` [list of strings, default nil]: purchase IDs
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingTransaction maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingTransaction maps previously created in the Stark Infra API and the cursor to the next page.

  ## Options:
    - `:source` [string, default nil]: source of the transaction to filter retrieved maps. ex: \"issuing-purchase/5656565656565656\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:ids` [list of strings, default nil]: purchase IDs
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingTransaction maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
