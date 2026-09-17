(ns starkinfra.pix-dispute
  "Pix disputes can be created when a fraud is detected creating a chain of transactions
  in order to reverse the funds to the origin. When you initialize a PixDispute,
  the entity will not be automatically created in the Stark Infra API. The 'create'
  function sends the maps to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:reference-id` [string]: endToEndId of the transaction being reported. ex: \"E20018183202201201450u34sDGd19lz\"
    - `:method` [string]: method used to perform the fraudulent action. Options: \"scam\", \"unauthorized\", \"coercion\", \"invasion\", \"other\"
    - `:operator-email` [string]: contact email of the operator responsible for the dispute.
    - `:operator-phone` [string]: contact phone number of the operator responsible for the dispute.

  ## Parameters (conditionally required):
    - `:description` [string, default nil]: description including any details that can help with the dispute investigation. The description parameter is required when method is \"other\".

  ## Parameters (optional):
    - `:tags` [list of strings]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:min-transaction-amount` [integer]: minimum transaction amount to be considered for the graph creation.
    - `:max-transaction-count` [integer]: maximum number of transactions to be considered for the graph creation.
    - `:max-hop-interval` [integer]: maximum interval in seconds between hops to be considered for the graph creation.
    - `:max-hop-count` [integer]: depth to be considered for the graph creation.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixDispute is created. ex: \"5656565656565656\"
    - `:bacen-id` [string]: Central Bank's unique dispute id. ex: \"817fc523-9e9d-40ab-9e53-dacb71454a05\"
    - `:flow` [string]: indicates the flow of the Pix Dispute. Options: \"in\" if you received the PixDispute, \"out\" if you created the PixDispute.
    - `:status` [string]: current PixDispute status. Options: \"created\", \"delivered\", \"analysed\", \"processing\", \"closed\", \"failed\", \"canceled\"
    - `:transactions` [list of maps]: list of PixDispute.Transaction maps related to the dispute. Each map has:
      - `:end-to-end-id` [string]: Central Bank's unique transaction id. ex: \"E79457883202101262140HHX553UPqeq\"
      - `:amount` [integer]: refundable amount. ex: 11234 (= R$ 112.34)
      - `:nominal-amount` [integer]: transaction amount. ex: 11234 (= R$ 112.34)
      - `:receiver-type` [string]: receiver person type. Options: \"individual\", \"business\"
      - `:receiver-tax-id-created` [string]: receiver's taxId creation date. For business type only.
      - `:receiver-account-created` [string]: receiver's account creation date.
      - `:receiver-bank-code` [string]: receiver's bank code. ex: \"20018183\"
      - `:receiver-id` [string]: identifier of accountholder in the graph.
      - `:sender-type` [string]: sender person type. Options: \"individual\", \"business\"
      - `:sender-tax-id-created` [string]: sender's taxId creation date. For business type only.
      - `:sender-account-created` [string]: sender's account creation date.
      - `:sender-bank-code` [string]: sender's bank code. ex: \"20018183\"
      - `:sender-id` [string]: identifier of accountholder in the graph.
      - `:settled` [string]: settled datetime of the transaction. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the PixDispute. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixDispute. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           post-multi]]))

(defn- resource []
  "pix-dispute")


(defn create
  "Send a list of PixDispute maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `requests` [list of maps]: list of PixDispute maps to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixDispute maps with updated attributes"
  ([requests]
   (post-multi @credentials (resource) requests {}))

  ([requests user]
   (post-multi user (resource) requests {})))

(defn get
  "Receive a single PixDispute map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixDispute map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixDispute maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: Central Bank's unique dispute id to filter retrieved maps. ex: \"817fc523-9e9d-40ab-9e53-dacb71454a05\"
    - `:reference-ids` [list of strings, default nil]: list of end_to_end_ids of the reported transactions to filter retrieved maps. ex: [\"E20018183202201201450u34sDjD7334\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixDispute maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixDispute maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"success\", \"failed\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: Central Bank's unique dispute id to filter retrieved maps. ex: \"817fc523-9e9d-40ab-9e53-dacb71454a05\"
    - `:reference-ids` [list of strings, default nil]: list of end_to_end_ids of the reported transactions to filter retrieved maps. ex: [\"E20018183202201201450u34sDjD7334\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixDispute maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn cancel
  "Cancel a PixDispute entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: PixDispute unique id. ex: \"6306109539221504\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixDispute map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
