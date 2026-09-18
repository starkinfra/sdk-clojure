(ns starkinfra.static-brcode
  "A StaticBrcode stores account information in the form of a PixKey and can be used to create
  Pix transactions easily.
  When you initialize a StaticBrcode, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: receiver's name. ex: \"Tony Stark\"
    - `:key-id` [string]: receiver's Pixkey id. ex: \"+5541999999999\"
    - `:city` [string]: receiver's city name. ex: \"Rio de Janeiro\"

  ## Parameters (optional):
    - `:amount` [integer, default 0]: positive integer that represents the amount in cents of the resulting Pix transaction. ex: 1234 (= R$ 12.34)
    - `:cashier-bank-code` [string, default nil]: Cashier's bank code. ex: \"20018183\".
    - `:reconciliation-id` [string, default nil]: id to be used for conciliation of the resulting Pix transaction. This id must have up to 25 alphanumeric characters ex: \"ah27s53agj6493hjds6836v49\"
    - `:description` [string, default nil]: optional description to override default description to be shown in the bank statement. ex: \"Payment for service #1234\"
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:type` [string, default \"instant\"]: type of the StaticBrcode. Options: \"instant\", \"instantAndOrSubscription\"

  ## Attributes (return-only):
    - `:id` [string]: id returned on creation, this is the BR code. ex: \"00020126360014br.gov.bcb.pix0114+552840092118152040000530398654040.095802BR5915Jamie Lannister6009Sao Paulo620705038566304FC6C\"
    - `:uuid` [string]: unique uuid returned when a StaticBrcode is created. ex: \"97756273400d42ce9086404fe10ea0d6\"
    - `:url` [string]: url link to the BR code image. ex: \"https://brcode-h.development.starkinfra.com/static-qrcode/97756273400d42ce9086404fe10ea0d6.png\"
    - `:updated` [string]: latest update datetime for the StaticBrcode. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the StaticBrcode. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "static-brcode")


(defn create
  "Send a list of StaticBrcode maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `brcodes` [list of maps]: list of StaticBrcode maps to be created in the API. You can send up to 100 StaticBrcode maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of StaticBrcode maps with updated attributes"
  ([brcodes]
   (post-multi @credentials (resource) brcodes {}))

  ([brcodes user]
   (post-multi user (resource) brcodes {})))

(defn get
  "Receive a single StaticBrcode map previously created in the Stark Infra API by its uuid.

  ## Parameters (required):
    - `uuid` [string]: map's unique uuid. ex: \"97756273400d42ce9086404fe10ea0d6\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - StaticBrcode map with updated attributes"
  ([uuid]
   (get-id @credentials (resource) uuid {}))

  ([uuid user]
   (get-id user (resource) uuid {})))

(defn query
  "Receive a stream of StaticBrcode maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:uuids` [list of strings, default nil]: list of uuids to filter retrieved maps. ex: [\"97756273400d42ce9086404fe10ea0d6\", \"e3da0b6d56fa4045b9b295b2be82436e\"]
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of StaticBrcode maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 StaticBrcode maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:uuids` [list of strings, default nil]: list of uuids to filter retrieved maps. ex: [\"97756273400d42ce9086404fe10ea0d6\", \"e3da0b6d56fa4045b9b295b2be82436e\"]
    - `:tags` [list of strings, default nil]: list of tags to filter retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of StaticBrcode maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
