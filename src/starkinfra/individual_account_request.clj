(ns starkinfra.individual-account-request
  "An IndividualAccountRequest represents an individual account request. It can be created to request
  the opening of an account for a specific individual by providing their required information.
  When you initialize an IndividualAccountRequest, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: individual's full name. ex: \"Edward Stark\".
    - `:tax-id` [string]: individual's tax ID (CPF). ex: \"012.345.678-90\"
    - `:address` [map]: individual's structured residential address. ex: {:street \"Rua do Estilo Barroco\" :number \"648\" :neighborhood \"Santo Amaro\" :city \"Sao Paulo\" :state \"SP\" :zip-code \"05724005\"}
      - `:street` [string]: street name. ex: \"Rua do Estilo Barroco\"
      - `:number` [string]: street number. ex: \"648\"
      - `:neighborhood` [string]: neighborhood / district. ex: \"Santo Amaro\"
      - `:city` [string]: city. ex: \"Sao Paulo\"
      - `:state` [string]: state (BR 2-letter code). ex: \"SP\"
      - `:zip-code` [string]: ZIP code (BR CEP), formatted or digit-only. ex: \"05724005\"
      - `:complement` [string, default nil]: address complement. ex: \"Apto. 123\"
    - `:income` [integer]: individual's income in cents. ex: 1000000 (= R$ 10,000.00)

  ## Parameters (optional):
    - `:birth-date` [string, default nil]: individual's birth date. ex: \"2012-03-06\"
    - `:tags` [list of strings, default []]: list of strings for reference when searching for IndividualAccountRequests. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the IndividualAccountRequest is created. ex: \"5656565656565656\"
    - `:account-type` [string]: type of account requested. ex: \"individual\"
    - `:flags` [list of maps]: flags that motivated the decision, populated when the request is denied. Each flag has a code and a message.
    - `:status` [string]: current status of the IndividualAccountRequest. Options: \"created\", \"processing\", \"approved\", \"denied\"
    - `:created` [string]: creation datetime for the IndividualAccountRequest. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the IndividualAccountRequest. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:validator-link` [string]: webview link to be delivered to the taker to complete biometrics and document capture."
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "individual-account-request")


(defn create
  "Send a list of IndividualAccountRequest maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `requests` [list of maps]: list of IndividualAccountRequest maps to be created in the API

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of IndividualAccountRequest maps with updated attributes"
  ([requests]
   (post-multi @credentials (resource) requests {}))

  ([requests user]
   (post-multi user (resource) requests {})))

(defn get
  "Receive a single IndividualAccountRequest map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IndividualAccountRequest map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IndividualAccountRequest maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings or string, default nil]: filter for status of retrieved maps. A single value is also accepted. ex: [\"created\", \"processing\"] or \"approved\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IndividualAccountRequest maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IndividualAccountRequest maps previously created in the Stark Infra API and the cursor to the next page.
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
      - `:content`: list of IndividualAccountRequest maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update an IndividualAccountRequest by passing id.

  ## Parameters (required):
    - `id` [string]: IndividualAccountRequest id. ex: \"5656565656565656\"

  ## Options:
    - `:status` [string, default nil]: you may send IndividualAccountRequests to validation by passing \"processing\" in the status
    - `:name` [string, default nil]: individual's full name. ex: \"Edward Stark\"
    - `:tax-id` [string, default nil]: individual's tax ID (CPF). ex: \"012.345.678-90\"
    - `:address` [map, default nil]: individual's structured residential address. Replaces the address map as a whole. ex: {:street \"Avenida Paulista\" :number \"1000\" :neighborhood \"Bela Vista\" :city \"Sao Paulo\" :state \"SP\" :zip-code \"01310100\"}
    - `:income` [integer, default nil]: individual's income in cents. ex: 1000000 (= R$ 10,000.00)
    - `:birth-date` [string, default nil]: individual's birth date. ex: \"2012-03-06\"
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for IndividualAccountRequests. ex: [\"employees\", \"monthly\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target IndividualAccountRequest map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))
