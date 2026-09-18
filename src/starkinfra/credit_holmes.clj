(ns starkinfra.credit-holmes
  "CreditHolmes are used to obtain debt information on your customers.
  Before you create a CreditHolmes, make sure you have your customer's express
  authorization to verify their information in the Central Bank's SCR.
  When you initialize a CreditHolmes, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:tax-id` [string]: customer's tax ID (CPF or CNPJ) for whom the credit operations will be verified. ex: \"20.018.183/0001-80\"

  ## Parameters (optional):
    - `:competence` [string, default 'two months before current date']: competence month of the operation verification, format: \"YYYY-MM\". ex: \"2021-04\"
    - `:tags` [list of strings, default []]: list of strings for reference when searching for CreditHolmes. ex: [\"credit\", \"operation\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the CreditHolmes is created. ex: \"5656565656565656\"
    - `:result` [map]: result of the investigation after the case is solved.
    - `:status` [string]: current status of the CreditHolmes. ex: \"created\", \"failed\", \"success\"
    - `:created` [string]: creation datetime for the CreditHolmes. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the CreditHolmes. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-multi]]))

(defn- resource []
  "credit-holmes")


(defn create
  "Send a list of CreditHolmes maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `holmes` [list of maps]: list of CreditHolmes maps to be created in the API. You can send up to 100 CreditHolmes maps in a single request.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of CreditHolmes maps with updated attributes"
  ([holmes]
   (post-multi @credentials (resource) holmes {}))

  ([holmes user]
   (post-multi user (resource) holmes {})))

(defn get
  "Receive a single CreditHolmes map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - CreditHolmes map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of CreditHolmes maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: \"created\", \"failed\", \"success\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of CreditHolmes maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 CreditHolmes maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: \"created\", \"failed\", \"success\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of CreditHolmes maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
