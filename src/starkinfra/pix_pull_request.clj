(ns starkinfra.pix-pull-request
  "A Pix Pull Request is a command sent to the payer's bank to trigger the automatic
  debit linked to an active PixPullSubscription. It confirms the receiver's intent
  to collect the agreed amount within the current billing cycle and initiates the
  settlement process through the Pix infrastructure. Each pull request references a
  parent PixPullSubscription via `:subscription-id`.
  When you initialize a PixPullRequest, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:amount` [integer]: amount to be charged in cents. ex: 11234 (= R$ 112.34)
    - `:due` [string]: due date for answering with an approval or denial. ISO 8601.
    - `:end-to-end-id` [string]: Central Bank's unique transaction id. ex: \"E00002649202201172211u34srod19le\"
    - `:receiver-account-number` [string]: receiver's bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
    - `:receiver-account-type` [string]: receiver's account type. Options: \"checking\", \"savings\", \"salary\", \"payment\"
    - `:receiver-bank-code` [string]: receiver's bank code.
    - `:reconciliation-id` [string]: id used for conciliation of the resulting Pix transaction. Up to 25 alphanumeric chars. ex: \"123456\"
    - `:subscription-id` [string]: unique id of the parent PixPullSubscription.

  ## Parameters (optional):
    - `:attempt-type` [string, default nil]: defines the type of attempt. Options: \"default\", \"instantRetry\", \"scheduledRetry\".
    - `:description` [string, default nil]: additional information to be delivered to the sender.
    - `:receiver-branch-code` [string, default nil]: receiver's branch code.
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for PixPullRequests. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixPullRequest is created. ex: \"5656565656565656\"
    - `:status` [string]: current PixPullRequest status. Options: \"created\", \"processing\", \"scheduled\", \"denied\", \"success\", \"canceled\", \"expired\"
    - `:flow` [string]: direction of money flow. Options: \"in\", \"out\"
    - `:receiver-name` [string]: receiver's full name (filled in by the Pix infrastructure during settlement).
    - `:receiver-tax-id` [string]: receiver's tax ID (CPF or CNPJ).
    - `:sender-bank-code` [string]: sender's bank institution code in Brazil.
    - `:sender-final-name` [string]: sender's final name when the sender differs from the originating institution.
    - `:sender-tax-id` [string]: sender's tax ID (CPF or CNPJ).
    - `:subscription-bacen-id` [string]: bacenId of the parent subscription, denormalized for convenience.
    - `:created` [string]: creation datetime for the PixPullRequest.
    - `:updated` [string]: latest update datetime for the PixPullRequest."
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "pix-pull-request")


(defn create
  "Send a list of PixPullRequest maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `requests` [list of maps]: list of PixPullRequest maps to be created in the API. Min = 1, Max = 100 per call. A request must be sent 2 to 10 days before the intended settlement date, and Stark Infra validates that the parent subscription is approved, the amount is within its authorized limit, the due date matches the subscription's charge cycle, payer/receiver details match the contract, and no other request is already scheduled for the same cycle. As the payer's bank, you must attempt settlement in two windows (00h00-08h00 and 18h00-21h00); attempts are no longer accepted after 21:00.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixPullRequest maps with updated attributes"
  ([requests]
   (post-multi @credentials (resource) requests {}))

  ([requests user]
   (post-multi user (resource) requests {})))

(defn get
  "Receive a single PixPullRequest map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixPullRequest map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixPullRequest maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date.
    - `:before` [string, default nil]: date filter for maps created before a specified date.
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"scheduled\", \"denied\", \"success\", \"canceled\", \"expired\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"employees\", \"monthly\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:subscription-ids` [list of strings, default nil]: filter by parent PixPullSubscription ids. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:flows` [list of strings, default nil]: direction of money flow to filter retrieved maps. Options: \"in\", \"out\".
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixPullRequest maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixPullRequest maps previously created and a cursor for the next page.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page call.
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created after a specified date.
    - `:before` [string, default nil]: date filter for maps created before a specified date.
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"processing\", \"scheduled\", \"denied\", \"success\", \"canceled\", \"expired\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"employees\", \"monthly\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:subscription-ids` [list of strings, default nil]: filter by parent PixPullSubscription ids. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:flows` [list of strings, default nil]: direction of money flow to filter retrieved maps. Options: \"in\", \"out\".
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixPullRequest maps with updated attributes
      - `:cursor`: cursor to retrieve the next page"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a PixPullRequest to change its status to \"scheduled\" or \"denied\".

  ## Parameters (required):
    - `id` [string]: PixPullRequest unique id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:status` [string]: new status to set. Options: \"scheduled\", \"denied\". Only the payer may update a pull request.

  ## Parameters (conditionally required):
    - `:reason` [string, default nil]: required when `:status` is \"denied\". Options: \"senderAccountClosed\", \"senderAccountBlocked\", \"amountNotAllowed\".

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixPullRequest with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel a PixPullRequest entity previously created in the Stark Infra API.
  `:reason` is sent as a query parameter on the DELETE request.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:reason` [string, default nil]: cancellation reason. As sender: \"accountClosed\", \"accountBlocked\", \"pixRequestFailed\", \"other\", \"senderUserRequested\". As receiver: \"accountClosed\", \"accountBlocked\", \"other\", \"receiverUserRequested\".
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixPullRequest map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id params]
   (delete-id @credentials (resource) id params))

  ([id params user]
   (delete-id user (resource) id params)))
