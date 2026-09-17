(ns starkinfra.pix-internal-transaction-report
  "PixInternalTransactionReports are used to report transactions that happened
  internally, outside of the SPI, to the Central Bank so they are reflected in
  the participant's statements.
  When you initialize a PixInternalTransactionReport, the entity will not be
  automatically created in the Stark Infra API. The 'create' function sends the
  maps to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount of the reported transaction in cents. ex: 1234 (= R$ 12.34)
    - `:created` [string]: datetime when the reported transaction occurred. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:end-to-end-id` [string]: central bank's unique transaction id. ex: \"E20018183202201201213u34sav898j\"
    - `:method` [string]: type of the reported transaction. Options: \"manual\", \"dict\", \"initiator\", \"dynamicQrcode\", \"staticQrcode\", \"payerQrcode\", \"subscription\", \"contactless\", \"staticContactless\"
    - `:reference-type` [string]: type of the reported transaction. Options: \"request\", \"reversal\"
    - `:sender-account-number` [string]: sender's bank account number. ex: \"76543\"
    - `:sender-branch-code` [string]: sender's bank account branch code. ex: \"1234\"
    - `:sender-account-type` [string]: sender's bank account type. Options: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:sender-bank-code` [string]: sender's participant code (ISPB). ex: \"20018183\"
    - `:sender-tax-id` [string]: sender's tax ID (CPF/CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:receiver-account-number` [string]: receiver's bank account number. ex: \"76543\"
    - `:receiver-branch-code` [string]: receiver's bank account branch code. ex: \"1234\"
    - `:receiver-account-type` [string]: receiver's bank account type. Options: \"checking\", \"savings\", \"salary\" or \"payment\"
    - `:receiver-bank-code` [string]: receiver's participant code (ISPB). ex: \"20018183\"
    - `:receiver-tax-id` [string]: receiver's tax ID (CPF/CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"

  ## Parameters (optional):
    - `:receiver-key-id` [string, default nil]: receiver's Pix Key used in the reported transaction. ex: \"+5511989898989\"
    - `:return-id` [string, default nil]: central bank's unique reversal transaction id. Required when reference-type is \"reversal\". ex: \"D20018183202201201213u34sav898j\"

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixInternalTransactionReport is created. ex: \"5656565656565656\"
    - `:status` [string]: current PixInternalTransactionReport status. ex: \"created\", \"processing\", \"success\", \"failed\"
    - `:updated` [string]: latest update datetime for the PixInternalTransactionReport. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "pix-internal-transaction-report")


(defn create
  "Send a list of PixInternalTransactionReport maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `reports` [list of maps]: list of PixInternalTransactionReport maps to be created in the API. You can send up to 100 maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixInternalTransactionReport maps with updated attributes"
  ([reports]
   (post-multi @credentials (resource) reports {}))

  ([reports user]
   (post-multi user (resource) reports {})))

(defn get
  "Receive a single PixInternalTransactionReport map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixInternalTransactionReport map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixInternalTransactionReport maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixInternalTransactionReport maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixInternalTransactionReport maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixInternalTransactionReport maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
