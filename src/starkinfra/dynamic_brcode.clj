(ns starkinfra.dynamic-brcode
  "BR codes store information represented by Pix QR Codes, which are used to
  send or receive Pix transactions in a convenient way.
  DynamicBrcodes represent charges with information that can change at any time,
  since all data needed for the payment is requested dynamically to an URL stored
  in the BR Code. Stark Infra will receive the GET request and forward it to your
  registered endpoint with a GET request containing the UUID of the BR code for
  identification.
  When you initialize a DynamicBrcode, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: receiver's name. ex: \"Tony Stark\"
    - `:city` [string]: receiver's city name. ex: \"Rio de Janeiro\"
    - `:external-id` [string]: string that must be unique among all your DynamicBrcodes. Duplicated external ids will cause failures. ex: \"my-internal-id-123456\"

  ## Parameters (optional):
    - `:type` [string, default \"instant\"]: type of the DynamicBrcode. Options: \"instant\", \"due\", \"subscription\", \"subscriptionAndInstant\", \"dueAndOrSubscription\"
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string]: id returned on creation, this is the BR code. ex: \"00020126360014br.gov.bcb.pix0114+552840092118152040000530398654040.095802BR5915Jamie Lannister6009Sao Paulo620705038566304FC6C\"
    - `:uuid` [string]: unique uuid returned when the DynamicBrcode is created. ex: \"4e2eab725ddd495f9c98ffd97440702d\"
    - `:url` [string]: url link to the BR code image. ex: \"https://brcode-h.development.starkinfra.com/dynamic-qrcode/901e71f2447c43c886f58366a5432c4b.png\"
    - `:updated` [string]: latest update datetime for the DynamicBrcode. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the DynamicBrcode. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.parse :as parse]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "dynamic-brcode")


(defn create
  "Send a list of DynamicBrcode maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `brcodes` [list of maps]: list of DynamicBrcode maps to be created in the API. You can send up to 100 DynamicBrcode maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of DynamicBrcode maps with updated attributes"
  ([brcodes]
   (post-multi @credentials (resource) brcodes {}))

  ([brcodes user]
   (post-multi user (resource) brcodes {})))

(defn get
  "Receive a single DynamicBrcode map previously created in the Stark Infra API by its uuid.

  ## Parameters (required):
    - `uuid` [string]: map's unique uuid. ex: \"901e71f2447c43c886f58366a5432c4b\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - DynamicBrcode map with updated attributes"
  ([uuid]
   (get-id @credentials (resource) uuid {}))

  ([uuid user]
   (get-id user (resource) uuid {})))

(defn query
  "Receive a stream of DynamicBrcode maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:external-id` [string, default nil]: list of external_ids to filter retrieved maps. ex: \"my_external_id1\"
    - `:uuids` [list of strings, default nil]: list of uuids to filter retrieved maps. ex: [\"901e71f2447c43c886f58366a5432c4b\", \"4e2eab725ddd495f9c98ffd97440702d\"]
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of DynamicBrcode maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of DynamicBrcode maps previously created in the Stark Infra API and the cursor to the next page.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:external-id` [string, default nil]: list of external_ids to filter retrieved maps. ex: \"my_external_id1\"
    - `:uuids` [list of strings, default nil]: list of uuids to filter retrieved maps. ex: [\"901e71f2447c43c886f58366a5432c4b\", \"4e2eab725ddd495f9c98ffd97440702d\"]
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of DynamicBrcode maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn response-due
  "Helps you respond to a due DynamicBrcode Read.
  When a Due DynamicBrcode is read by your user, a GET request containing the Brcode's
  UUID will be made to your registered URL to retrieve additional information needed
  to complete the transaction.
  The GET request must be answered in the following format, within 5 seconds, and with
  an HTTP status code 200.

  ## Parameters (required):
    - `params` [map]:
      - `:version` [integer]: integer that represents how many times the BR code was updated.
      - `:created` [string]: creation datetime in ISO format of the DynamicBrcode. ex: \"2020-03-10T10:30:00.000000+00:00\"
      - `:due` [string]: requested payment due datetime in ISO format. ex: \"2020-03-10T10:30:00.000000+00:00\"
      - `:key-id` [string]: receiver's PixKey id. Can be a tax_id (CPF/CNPJ), a phone number, an email or an alphanumeric sequence (EVP). ex: \"+5511989898989\"
      - `:status` [string]: BR code status. Options: \"created\", \"overdue\", \"paid\", \"canceled\" or \"expired\"
      - `:reconciliation-id` [string]: id to be used for conciliation of the resulting Pix transaction. This id must have from to 26 to 35 alphanumeric characters ex: \"cd65c78aeb6543eaaa0170f68bd741ee\"
      - `:nominal-amount` [integer]: positive integer that represents the amount in cents of the resulting Pix transaction. ex: 1234 (= R$ 12.34)
      - `:sender-name` [string]: sender's full name. ex: \"Anthony Edward Stark\"
      - `:sender-tax-id` [string]: sender's CPF (11 digits formatted or unformatted) or CNPJ (14 digits formatted or unformatted). ex: \"01.001.001/0001-01\"
      - `:receiver-name` [string]: receiver's full name. ex: \"Jamie Lannister\"
      - `:receiver-tax-id` [string]: receiver's CPF (11 digits formatted or unformatted) or CNPJ (14 digits formatted or unformatted). ex: \"012.345.678-90\"
      - `:receiver-street-line` [string]: receiver's main address. ex: \"Av. Paulista, 200\"
      - `:receiver-city` [string]: receiver's address city name. ex: \"Sao Paulo\"
      - `:receiver-state-code` [string]: receiver's address state code. ex: \"SP\"
      - `:receiver-zip-code` [string]: receiver's address zip code. ex: \"01234-567\"
      - `:data` [list of maps, default nil]: aditional info to the br code, example: [{:key \"Anticipation discount\" :value \"3.80\"}]
      - `:expiration` [integer, default 86400 (1 day)]: time in seconds counted from the creation datetime until the DynamicBrcode expires. After expiration, the BR code cannot be paid anymore.
      - `:fine` [float, default 2.0]: Percentage charged if the sender pays after the due datetime.
      - `:interest` [float, default 1.0]: Interest percentage charged if the sender pays after the due datetime.
      - `:discounts` [list of maps, default nil]: list of maps with \"percentage\" [float] and \"due\" [string] pairs.
      - `:description` [string, default nil]: additional information to be shown to the sender at the moment of payment.

  ## Return:
    - Dumped JSON string that must be returned to us"
  [params]
  (json/dumps (json/api-json params)))

(defn response-instant
  "Helps you respond to an instant DynamicBrcode Read.
  When an instant DynamicBrcode is read by your user, a GET request containing the BR code's UUID will be made
  to your registered URL to retrieve additional information needed to complete the transaction.
  The GET request must be answered in the following format within 5 seconds and with an HTTP status code 200.

  ## Parameters (required):
    - `params` [map]:
      - `:version` [integer]: integer that represents how many times the BR code was updated.
      - `:created` [string]: creation datetime of the DynamicBrcode. ex: \"2022-05-17\"
      - `:key-id` [string]: receiver's PixKey id. Can be a tax_id (CPF/CNPJ), a phone number, an email or an alphanumeric sequence (EVP). ex: \"+5511989898989\"
      - `:status` [string]: BR code status. Options: \"created\", \"overdue\", \"paid\", \"canceled\" or \"expired\"
      - `:reconciliation-id` [string]: id to be used for conciliation of the resulting Pix transaction. ex: \"cd65c78aeb6543eaaa0170f68bd741ee\"
      - `:amount` [integer]: positive integer that represents the amount in cents of the resulting Pix transaction. ex: 1234 (= R$ 12.34)

  ## Parameters (conditionally-required):
    - `:cashier-type` [string, default nil]: cashier's type. Required if the cashAmount is different from 0. Options: \"merchant\", \"participant\" and \"other\"
    - `:cashier-bank-code` [string, default nil]: cashier's bank code. Required if the cashAmount is different from 0. ex: \"20018183\"

  ## Parameters (optional):
    - `:data` [list of maps, default nil]: aditional info to the br code, example: [{:key \"Anticipation discount\" :value \"3.80\"}]
    - `:cash-amount` [integer, default 0]: amount to be withdrawn from the cashier in cents. ex: 1000 (= R$ 10.00)
    - `:expiration` [integer, default 86400 (1 day)]: time in seconds counted from the creation datetime until the DynamicBrcode expires. After expiration, the BR code cannot be paid anymore. Default value: 86400 (1 day)
    - `:sender-name` [string, default nil]: sender's full name. ex: \"Anthony Edward Stark\"
    - `:sender-tax-id` [string, default nil]: sender's CPF (11 digits formatted or unformatted) or CNPJ (14 digits formatted or unformatted). ex: \"01.001.001/0001-01\"
    - `:amount-type` [string, default \"fixed\"]: amount type of the Brcode. If the amount type is \"custom\" the Brcode's amount can be changed by the sender at the moment of payment. Options: \"fixed\"or \"custom\"
    - `:description` [string, default nil]: additional information to be shown to the sender at the moment of payment.

  ## Return:
    - Dumped JSON string that must be returned to us"
  [params]
  (json/dumps (json/api-json params)))

(defn verify
  "Verify a DynamicBrcode Read.
  When a DynamicBrcode is read by your user, a GET request will be made to your registered URL to
  retrieve additional information needed to complete the transaction.
  Use this method to verify the authenticity of a GET request received at your registered endpoint.
  If the provided digital signature does not check out with the Stark Infra public key, an ex-info
  carrying `:code \"invalidSignature\"` is thrown.

  ## Parameters (required):
    - `uuid` [string]: unique uuid returned when a DynamicBrcode is created. ex: \"4e2eab725ddd495f9c98ffd97440702d\"
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - verified Brcode's uuid."
  ([uuid signature]
   (parse/verify uuid signature @credentials))

  ([uuid signature user]
   (parse/verify uuid signature user)))
