(ns starkinfra.pix-infraction
  "PixInfractions are used to report transactions that are suspected of
  fraud, to request a refund or to reverse a refund.
  When you initialize a PixInfraction, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:reference-id` [string]: end_to_end_id or return_id of the transaction being reported. ex: \"E20018183202201201450u34sDGd19lz\"
    - `:type` [string]: type of infraction report. Options: \"fraud\", \"reversal\", \"reversalChargeback\"
    - `:method` [string]: method of Pix Infraction. Options: \"scam\", \"unauthorized\", \"coercion\", \"invasion\", \"other\", \"unknown\"
    - `:operator-email` [string]: contact email of the operator responsible for the PixInfraction.
    - `:operator-phone` [string]: contact phone number of the operator responsible for the PixInfraction.

  ## Parameters (optional):
    - `:description` [string, default nil]: description for any details that can help with the infraction investigation.
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:fraud-type` [string, default nil]: type of Pix Fraud. Options: \"identity\", \"mule\", \"scam\", \"unknown\", \"other\"

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixInfraction is created. ex: \"5656565656565656\"
    - `:fraud-id` [string]: id of the Pix Fraud. ex: \"5741774970552320\"
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:credited-bank-code` [string]: bank_code of the credited Pix participant in the reported transaction. ex: \"20018183\"
    - `:debited-bank-code` [string]: bank_code of the debited Pix participant in the reported transaction. ex: \"20018183\"
    - `:flow` [string]: direction of the PixInfraction flow. Options: \"out\" if you created the PixInfraction, \"in\" if you received the PixInfraction.
    - `:analysis` [string]: analysis that led to the result.
    - `:reported-by` [string]: agent that reported the PixInfraction. Options: \"debited\", \"credited\"
    - `:result` [string]: result after the analysis of the PixInfraction by the receiving party. Options: \"agreed\", \"disagreed\"
    - `:amount` [integer]: amount in cents of the reported transaction.
    - `:dispute-id` [string]: id of the PixDispute associated with the PixInfraction.
    - `:status` [string]: current PixInfraction status. Options: \"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"
    - `:created` [string]: creation datetime for the PixInfraction. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixInfraction. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id]]))

(defn- resource []
  "pix-infraction")


(defn ^{:deprecated "0.28.0"} create
  "Deprecated since v0.28.0. Creating PixInfractions is no longer supported by
  the Stark Infra API.

  Create PixInfraction maps in the Stark Infra API.

  ## Parameters (required):
    - `infractions` [list of maps]: list of PixInfraction maps to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixInfraction maps with updated attributes"
  ([infractions]
   (create infractions nil))

  ([_infractions _user]
   (throw (ex-info "Function deprecated since v0.28.0"
                   {:errors [{:code "deprecated"
                              :message "Function deprecated since v0.28.0"}]}))))

(defn get
  "Retrieve the PixInfraction map linked to your Workspace in the Stark Infra API using its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixInfraction map that corresponds to the given id"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixInfraction maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. Options: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:type` [list of strings, default nil]: filter for the type of retrieved PixInfractions. Options: \"fraud\", \"reversal\", \"reversalChargeback\"
    - `:flow` [string, default nil]: direction of the PixInfraction flow. Options: \"out\" if you created the PixInfraction, \"in\" if you received the PixInfraction.
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixInfraction maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixInfraction maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. Options: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:type` [list of strings, default nil]: filter for the type of retrieved PixInfractions. Options: \"fraud\", \"reversal\", \"reversalChargeback\"
    - `:flow` [string, default nil]: direction of the PixInfraction flow. Options: \"out\" if you created the PixInfraction, \"in\" if you received the PixInfraction.
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixInfraction maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a PixInfraction by passing id. You must analyze and answer an
  inbound PixInfraction within 7 days of its delivery.

  ## Parameters (required):
    - `id` [string]: PixInfraction id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:result` [string]: result after the analysis of the PixInfraction. Options: \"agreed\", \"disagreed\"

  ## Options:
    - `:fraud-type` [string, default nil]: type of Pix Fraud. Required when result is \"agreed\"; optional when result is \"disagreed\". Options: \"identity\", \"mule\", \"scam\", \"other\", \"unknown\"
    - `:analysis` [string, default nil]: analysis that led to the result.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixInfraction map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel a PixInfraction entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixInfraction map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
