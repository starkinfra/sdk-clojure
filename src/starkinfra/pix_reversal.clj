(ns starkinfra.pix-reversal
  "PixReversals are instant payments used to revert PixRequests. You can only
  revert inbound PixRequests.
  When you initialize a PixReversal, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount in cents to be reversed from the PixRequest. ex: 1234 (= R$ 12.34)
    - `:external-id` [string]: string that must be unique among all your PixReversals. Duplicated external IDs will cause failures. By default, this parameter will block any PixReversal that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
    - `:end-to-end-id` [string]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
    - `:reason` [string]: reason why the PixReversal is being reversed. Options are \"bankError\", \"fraud\", \"cashierError\", \"customerRequest\"

  ## Parameters (optional):
    - `:tags` [list of strings, default []]: list of strings for reference when searching for PixReversals. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixReversal is created. ex: \"5656565656565656\"
    - `:return-id` [string]: central bank's unique reversal transaction ID. ex: \"D20018183202202030109X3OoBHG74wo\"
    - `:fee` [integer]: fee charged by this PixReversal, in cents. ex: 200 (= R$ 2.00)
    - `:status` [string]: current PixReversal status. ex: \"created\", \"processing\", \"success\", \"failed\"
    - `:flow` [string]: direction of money flow. ex: \"in\" or \"out\"
    - `:created` [string]: creation datetime for the PixReversal. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixReversal. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.parse :refer [parse-and-verify]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "pix-reversal")


(defn create
  "Send a list of PixReversal maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `reversals` [list of maps]: list of PixReversal maps to be created in the API. You can send up to 100 PixReversal maps in a single request. A PixReversal can only be created for an inbound PixRequest whose status is \"success\"; reference it via end-to-end-id.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixReversal maps with updated attributes"
  ([reversals]
   (post-multi @credentials (resource) reversals {}))

  ([reversals user]
   (post-multi user (resource) reversals {})))

(defn get
  "Receive a single PixReversal map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixReversal map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixReversal maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:return-ids` [list of strings, default nil]: central bank's unique reversal transaction IDs. ex: [\"D20018183202202030109X3OoBHG74wo\", \"D20018183202202030109X3OoBHG72rd\"]
    - `:external-ids` [list of strings, default nil]: url safe strings that must be unique among all your PixReversals. Duplicated external IDs will cause failures. By default, this parameter will block any PixReversal that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixReversal maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixReversal maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your reversals.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:return-ids` [list of strings, default nil]: central bank's unique reversal transaction ID. ex: [\"D20018183202202030109X3OoBHG74wo\", \"D20018183202202030109X3OoBHG72rd\"]
    - `:external-ids` [list of strings, default nil]: url safe string that must be unique among all your PixReversals. Duplicated external IDs will cause failures. By default, this parameter will block any PixReversal that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixReversal maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn- fill-defaults
  "sdk-python fills these four in after parsing, so an inbound authorization
  always has a fee, a tag list, an external id and a description to read."
  [reversal]
  (-> reversal
      (update :fee #(or % 0))
      (update :tags #(or % []))
      (update :external-id #(or % ""))
      (update :description #(or % ""))))

(defn parse
  "Create a single verified PixReversal map from a content string received from
  a handler listening at the reversal url. If the provided digital signature
  does not check out with the Stark Infra public key, an ex-info carrying
  `:code \"invalidSignature\"` is thrown.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - parsed PixReversal map"
  ([content signature]
   (fill-defaults (parse-and-verify content signature @credentials nil)))

  ([content signature user]
   (fill-defaults (parse-and-verify content signature user nil))))

(defn response
  "Helps you respond to a PixReversal authorization.
  You must answer this synchronous authorization webhook within 1 second.
  Unlike PixRequest, an inbound PixReversal is ACCEPTED by default if you do
  not respond in time or have no pixReversalUrl registered.

  ## Parameters (required):
    - `status` [string]: response to the authorization. ex: \"approved\" or \"denied\"

  ## Parameters (conditionally required):
    - `reason` [string, default nil]: denial reason. Required if the status is \"denied\". Options: \"invalidAccountNumber\", \"blockedAccount\", \"accountClosed\", \"invalidAccountType\", \"invalidTransactionType\", \"taxIdMismatch\", \"invalidTaxId\", \"orderRejected\", \"reversalTimeExpired\", \"settlementFailed\"

  ## Return:
    - JSON string that must be returned to us"
  ([status]
   (response status nil))

  ([status reason]
   (json/dumps (json/api-json {:authorization (array-map :status status :reason reason)}))))
