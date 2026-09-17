(ns starkinfra.business-account-request
  "You can create a business account request to request an account for a specific company, opening the
  account with identity verification by webview for each of its owners.
  When you initialize a BusinessAccountRequest, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:address` [map]: company's structured address. ex: {:street \"Av. Faria Lima\" :number \"2000\" :neighborhood \"Itaim Bibi\" :city \"Sao Paulo\" :state \"SP\" :zip-code \"04538-132\"}
      - `:street` [string]: street name. ex: \"Av. Faria Lima\"
      - `:number` [string]: street number. ex: \"2000\"
      - `:neighborhood` [string]: neighborhood / district. ex: \"Itaim Bibi\"
      - `:city` [string]: city. ex: \"Sao Paulo\"
      - `:state` [string]: state (BR 2-letter code). ex: \"SP\"
      - `:zip-code` [string]: ZIP code (BR CEP), formatted or digit-only. ex: \"04538-132\"
      - `:complement` [string, default nil]: address complement. ex: \"Sala 42\"
    - `:revenue` [integer]: company's annual revenue in cents. ex: 100000000 (= R$ 1,000,000.00)
    - `:name` [string]: company's legal name (minimum 5 characters). ex: \"Stark Bank S.A.\"
    - `:tax-id` [string]: company's tax ID (CNPJ). ex: \"20.018.183/0001-80\"
    - `:owners` [list of maps]: list of 1 to 10 company owners. ex: [{:tax-id \"012.345.678-90\" :name \"Jamie Lannister\" :role \"partner\"}]
      - `:tax-id` [string]: owner's tax ID (CPF). ex: \"012.345.678-90\"
      - `:name` [string]: owner's full name (minimum 5 characters). ex: \"Jamie Lannister\"
      - `:role` [string]: owner's role in the company. Options: \"partner\", \"representative\"
      - `:identity-id` [string, return-only]: unique id of the identity verification linked to this owner. ex: \"5709594221805568\"
      - `:validator-link` [string, return-only]: webview link to be delivered to the owner to complete biometrics and document capture.
      - `:status` [string, return-only]: current status of the owner verification. Options: \"created\", \"approved\", \"denied\"

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for BusinessAccountRequests. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the BusinessAccountRequest is created. ex: \"5656565656565656\"
    - `:account-type` [string]: type of the account. ex: \"business\"
    - `:flags` [list of maps]: flags that motivated the decision, populated when the request is denied. Each flag has a code and a message.
    - `:status` [string]: current status of the BusinessAccountRequest. Options: \"created\", \"processing\", \"approved\", \"denied\"
    - `:created` [string]: creation datetime for the BusinessAccountRequest. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the BusinessAccountRequest. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-multi]]))

(defn- resource []
  "business-account-request")


(defn create
  "Send a list of BusinessAccountRequest maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `requests` [list of maps]: list of BusinessAccountRequest maps to be created in the API

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of BusinessAccountRequest maps with updated attributes"
  ([requests]
   (post-multi @credentials (resource) requests {}))

  ([requests user]
   (post-multi user (resource) requests {})))

(defn get
  "Receive a single BusinessAccountRequest map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - BusinessAccountRequest map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of BusinessAccountRequest maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings or string, default nil]: filter for status of retrieved maps. A single value is also accepted. ex: [\"created\", \"processing\"] or \"approved\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of BusinessAccountRequest maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 BusinessAccountRequest maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings or string, default nil]: filter for status of retrieved maps. A single value is also accepted. ex: [\"created\", \"processing\"] or \"approved\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of BusinessAccountRequest maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
