(ns starkinfra.pix-request
  "PixRequests are used to receive or send instant payments to accounts
  hosted in any Pix participant.
  When you initialize a PixRequest, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount in cents to be transferred. ex: 11234 (= R$ 112.34)
    - `:external-id` [string]: string that must be unique among all your PixRequests. Duplicated external IDs will cause failures. By default, this parameter will block any PixRequests that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
    - `:sender-name` [string]: sender's full name. ex: \"Edward Stark\"
    - `:sender-tax-id` [string]: sender's tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:sender-branch-code` [string]: sender's bank account branch code. Use '-' in case there is a verifier digit. ex: \"1357-9\"
    - `:sender-account-number` [string]: sender's bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
    - `:sender-account-type` [string]: sender's bank account type. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:receiver-name` [string]: receiver's full name. ex: \"Edward Stark\"
    - `:receiver-tax-id` [string]: receiver's tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:receiver-bank-code` [string]: receiver's bank institution code in Brazil. ex: \"20018183\"
    - `:receiver-account-number` [string]: receiver's bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
    - `:receiver-branch-code` [string]: receiver's bank account branch code. Use '-' in case there is a verifier digit. ex: \"1357-9\"
    - `:receiver-account-type` [string]: receiver's bank account type. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:end-to-end-id` [string]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"

  ## Parameters (conditionally-required):
    - `:cashier-type` [string]: cashier's type. Required if the `:cash-amount` is different from 0. Options: \"merchant\", \"participant\" and \"other\"
    - `:cashier-bank-code` [string]: cashier's bank code. Required if the `:cash-amount` is different from 0. ex: \"20018183\"

  ## Parameters (optional):
    - `:cash-amount` [integer]: amount to be withdrawn from the cashier in cents. Must be less than or equal to amount. ex: 1000 (= R$ 10.00)
    - `:priority` [string, default nil]: Pix request processing priority. Options: \"high\", \"low\"
    - `:receiver-key-id` [string, default nil]: receiver's dict key. ex: \"20.018.183/0001-80\"
    - `:description` [string, default nil]: optional description to override default description to be shown in the bank statement. ex: \"Payment for service #1234\"
    - `:reconciliation-id` [string, default nil]: reconciliation ID linked to this payment. ex: \"b77f5236-7ab9-4487-9f95-66ee6eaf1781\"
    - `:initiator-tax-id` [string, default nil]: payment initiator's tax id (CPF/CNPJ). ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:tags` [list of strings, default []]: list of strings for reference when searching for PixRequests. ex: [\"employees\", \"monthly\"]
    - `:method` [string, default nil]: execution method for the creation of the Pix. Options: \"manual\", \"dict\", \"initiator\", \"dynamicQrcode\", \"staticQrcode\", \"payerQrcode\", \"subscription\", \"contactless\", \"staticContactless\"
    - `:reason` [string, default \"customerRequest\"]: underlying reason for the payment transaction. ex: \"customerRequest\", \"fraud\", \"subscriptionFlaw\". When reason is \"fraud\" (e.g. returning funds from a PixChargeback), `:sender-tax-id` must be your institution's organization tax ID (CNPJ).

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixRequest is created. ex: \"5656565656565656\"
    - `:fee` [integer]: fee charged when PixRequest is paid. ex: 200 (= R$ 2.00)
    - `:status` [string]: current PixRequest status. ex: \"created\", \"processing\", \"success\", \"failed\"
    - `:flow` [string]: direction of money flow. ex: \"in\" or \"out\"
    - `:sender-bank-code` [string]: sender's bank institution code in Brazil. ex: \"20018183\"
    - `:created` [string]: creation datetime for the PixRequest. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixRequest. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.parse :refer [parse-and-verify]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "pix-request")


(defn create
  "Send a list of PixRequest maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `requests` [list of maps]: list of PixRequest maps to be created in the API. You can send up to 100 PixRequest maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixRequest maps with updated attributes"
  ([requests]
   (post-multi @credentials (resource) requests {}))

  ([requests user]
   (post-multi user (resource) requests {})))

(defn get
  "Receive a single PixRequest map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixRequest map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixRequest maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:end-to-end-ids` [list of strings, default nil]: central bank's unique transaction IDs. ex: [\"E79457883202101262140HHX553UPqeq\", \"E79457883202101262140HHX553UPxzx\"]
    - `:external-ids` [list of strings, default nil]: url safe strings that must be unique among all your PixRequests. Duplicated external IDs will cause failures. By default, this parameter will block any PixRequests that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixRequest maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixRequest maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:end-to-end-ids` [list of strings, default nil]: central bank's unique transaction IDs. ex: [\"E79457883202101262140HHX553UPqeq\", \"E79457883202101262140HHX553UPxzx\"]
    - `:external-ids` [list of strings, default nil]: url safe strings that must be unique among all your PixRequests. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixRequest maps with updated attributes
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
  [request]
  (-> request
      (update :fee #(or % 0))
      (update :tags #(or % []))
      (update :external-id #(or % ""))
      (update :description #(or % ""))))

(defn parse
  "Create a single verified PixRequest map from a content string received from a
  handler listening at the request url. If the provided digital signature does
  not check out with the Stark Infra public key, an ex-info carrying
  `:code \"invalidSignature\"` is thrown.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - parsed PixRequest map"
  ([content signature]
   (fill-defaults (parse-and-verify content signature @credentials nil)))

  ([content signature user]
   (fill-defaults (parse-and-verify content signature user nil))))

(defn response
  "Helps you respond to a PixRequest authorization.
  Authorization requests will be posted at your registered endpoint whenever
  inbound PixRequests are received. Note that the receiving endpoint
  (pixRequestUrl) must answer this synchronous authorization webhook within 1
  second. If you do not respond in time, or if no pixRequestUrl is registered,
  Stark Infra denies the inbound PixRequest by default.

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
