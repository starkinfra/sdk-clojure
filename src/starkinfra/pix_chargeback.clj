(ns starkinfra.pix-chargeback
  "A Pix chargeback can be created when fraud is detected on a transaction or a system malfunction
  results in an erroneous transaction.
  It notifies another participant of your request to reverse the payment they have received.
  A PixChargeback should only be created after a corresponding PixInfraction has been completed, or after a system malfunction causes an erroneous transaction.
  When you initialize a PixChargeback, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:amount` [integer]: amount in cents to be reversed. ex: 11234 (= R$ 112.34)
    - `:reference-id` [string]: end_to_end_id or return_id of the transaction to be reversed. ex: \"E20018183202201201450u34sDGd19lz\"
    - `:reason` [string]: reason why the reversal was requested. Options: \"flaw\", \"fraud\", \"subscriptionFlaw\" (the API also assigns \"reversalChargeback\" automatically when a chargeback stems from a closed Pix Infraction, but it cannot be passed on creation).

  ## Parameters (conditionally required):
    - `:description` [string, default nil]: description for the PixChargeback. Required if reason is \"flaw\".

  ## Parameters (optional):
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixChargeback is created. ex: \"5656565656565656\"
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:analysis` [string]: analysis that led to the result.
    - `:sender-bank-code` [string]: bank_code of the Pix participant that created the PixChargeback. ex: \"20018183\"
    - `:receiver-bank-code` [string]: bank_code of the Pix participant that received the PixChargeback. ex: \"20018183\"
    - `:rejection-reason` [string]: reason for the rejection of the Pix chargeback. Options: \"noBalance\", \"accountClosed\", \"invalidRequest\", \"unableToReverse\"
    - `:reversal-reference-id` [string]: return_id or end_to_end_id of the reversal transaction. ex: \"D20018183202202030109X3OoBHG74wo\"
    - `:result` [string]: result after the analysis of the PixChargeback by the receiving party. Options: \"rejected\", \"accepted\", \"partiallyAccepted\"
    - `:flow` [string]: direction of the Pix Chargeback. Options: \"in\" for received chargebacks, \"out\" for chargebacks you requested
    - `:dispute-id` [string]: id of the dispute associated with the PixChargeback.
    - `:is-monitoring-required` [boolean]: indicates if monitoring is required for this chargeback.
    - `:reversal-account-number` [string]: account number for the reversal transaction.
    - `:reversal-account-type` [string]: account type for the reversal transaction.
    - `:reversal-bank-code` [string]: bank code for the reversal transaction.
    - `:reversal-branch-code` [string]: branch code for the reversal transaction.
    - `:reversal-tax-id` [string]: tax ID for the reversal transaction.
    - `:status` [string]: current PixChargeback status. Options: \"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"
    - `:created` [string]: creation datetime for the PixChargeback. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixChargeback. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "pix-chargeback")


(defn create
  "Create PixChargeback maps in the Stark Infra API.

  ## Parameters (required):
    - `chargebacks` [list of maps]: list of PixChargeback maps to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixChargeback maps with updated attributes"
  ([chargebacks]
   (post-multi @credentials (resource) chargebacks {}))

  ([chargebacks user]
   (post-multi user (resource) chargebacks {})))

(defn get
  "Retrieve the PixChargeback map linked to your Workspace in the Stark Infra API using its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixChargeback map that corresponds to the given id"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixChargeback maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:reference-ids` [list of strings, default nil]: list of end_to_end_ids or return_ids of the reversed transactions to filter retrieved maps. Max = 30. ex: [\"E20018183202201201450u34sDjD7334\"]
    - `:flow` [string, default nil]: direction of the Pix Chargeback. Options: \"in\" for received chargebacks, \"out\" for chargebacks you requested
    - `:tags` [list of strings, default nil]: filter for tags of retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixChargeback maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixChargeback maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:bacen-id` [string, default nil]: unique transaction id returned from Central Bank. ex: \"ccf9bd9c-e99d-999e-bab9-b999ca999f99\"
    - `:reference-ids` [list of strings, default nil]: list of end_to_end_ids or return_ids of the reversed transactions to filter retrieved maps. Max = 30. ex: [\"E20018183202201201450u34sDjD7334\"]
    - `:flow` [string, default nil]: direction of the Pix Chargeback. Options: \"in\" for received chargebacks, \"out\" for chargebacks you requested
    - `:tags` [list of strings, default nil]: filter for tags of retrieved maps. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixChargeback maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Respond to a received PixChargeback. You must analyze and answer an
  inbound PixChargeback within 24 hours of its creation.

  ## Parameters (required):
    - `id` [string]: PixChargeback id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:result` [string]: result after the analysis of the PixChargeback. Options: \"rejected\", \"accepted\", \"partiallyAccepted\"

  ## Options:
    - `:rejection-reason` [string, default nil]: if the PixChargeback's result is \"rejected\", a reason is required. Options: \"other\", \"noBalance\", \"accountClosed\", \"invalidRequest\" (\"unableToReverse\" is not a valid value).
    - `:analysis` [string, default nil]: description of the analysis that led to the result. Required if rejection-reason is \"invalidRequest\".
    - `:reversal-reference-id` [string, default nil]: return_id of the reversal transaction. ex: \"D20018183202201201450u34sDGd19lz\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixChargeback map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel a PixChargeback entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixChargeback map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
