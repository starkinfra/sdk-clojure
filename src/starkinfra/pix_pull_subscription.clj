(ns starkinfra.pix-pull-subscription
  "PixPullSubscriptions are recurring Pix debit authorizations. A subscription defines
  the frequency, amount, and required payer authorizations for a series of Pix debits
  to be pulled from the sender by the receiver. Each cycle of an active subscription
  is triggered by a PixPullRequest (its subscriptionId references the subscription's id).
  When you initialize a PixPullSubscription, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:bacen-id` [string]: Central Bank's unique recurrency id. Identifies the subscription in the Pix infrastructure.
    - `:external-id` [string]: safe string that must be unique among all your Pix Pull Subscriptions. Used for idempotency.
    - `:installment-start` [string]: start datetime of settlements allowed for this subscription. ISO 8601. ex: \"2026-03-10T19:32:35.418698+00:00\"
    - `:interval` [string]: cycle definition. Options: \"week\", \"month\", \"quarter\", \"semester\", \"year\"
    - `:receiver-name` [string]: receiver's full name. ex: \"Edward Stark\"
    - `:receiver-tax-id` [string]: receiver's tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:receiver-bank-code` [string]: receiver's bank institution code.
    - `:reference-code` [string]: commercial-relation identifier. May be a contract number, order id, or client code.
    - `:sender-account-number` [string]: sender's bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
    - `:sender-bank-code` [string]: sender's bank institution code in Brazil. ex: \"20018183\"
    - `:sender-branch-code` [string]: sender's bank account branch code. Use '-' in case there is a verifier digit. ex: \"1357-9\"
    - `:sender-city-code` [string]: IBGE code of the payer's city.
    - `:sender-tax-id` [string]: sender's tax ID (CPF or CNPJ). Same format rules as receiver-tax-id.

  ## Parameters (conditionally required):
    - `:amount` [integer, default nil]: amount in cents charged every cycle. Required if the subscription has a fixed value; omit for variable-amount subscriptions. At least one of `:amount` or `:amount-min-limit` MUST be provided. ex: 11234 (= R$ 112.34)
    - `:amount-min-limit` [integer, default nil]: floor value for the maximum amount the sender can set when approving. Used for variable-amount subscriptions. At least one of `:amount` or `:amount-min-limit` MUST be provided.

  ## Parameters (optional):
    - `:type` [string, default nil]: subscription journey type. Options: \"push\", \"qrcode\", \"qrcodeAndPayment\", \"paymentAndOrQrcode\"
    - `:description` [string, default nil]: additional information delivered to the sender.
    - `:due` [string, default nil]: due date for the sender's answer (approval or denial).
    - `:installment-end` [string, default nil]: end datetime of settlements allowed for this subscription.
    - `:pull-retry-limit` [integer, default nil]: max number of retries the receiver may issue for a single failed pull cycle.
    - `:sender-final-name` [string, default nil]: final sender name when the sender differs from the originating institution.
    - `:sender-final-tax-id` [string, default nil]: final sender tax ID. Same format rules as sender-tax-id.
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for PixPullSubscriptions. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the PixPullSubscription is created. ex: \"5656565656565656\"
    - `:status` [string]: current lifecycle state. Options: \"active\", \"approved\", \"canceled\", \"created\", \"denied\", \"expired\", \"failed\", \"pending\"
    - `:flow` [string]: direction of money flow. Options: \"in\", \"out\"
    - `:created` [string]: creation datetime for the PixPullSubscription. ex: \"2026-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the PixPullSubscription. ex: \"2026-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.parse :refer [parse-and-verify]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "pix-pull-subscription")


(defn create
  "Send a list of PixPullSubscription maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `subscriptions` [list of maps]: list of 1 to 100 PixPullSubscription maps to be created in the API. Only one automatic debit settles per interval cycle: weekly cycles anchor on the weekday of the first installment; month/quarter/semester/year cycles anchor on its calendar day (rolling to the next available date, but keeping the original day as the reference, when a given month lacks it). If pull-retry-limit allows retries, they may occur starting one day before the expected settlement date, up to 3 times within 7 days of the original date, always for the same amount, and never inside a new cycle window.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of PixPullSubscription maps with updated attributes"
  ([subscriptions]
   (post-multi @credentials (resource) subscriptions {}))

  ([subscriptions user]
   (post-multi user (resource) subscriptions {})))

(defn get
  "Receive a single PixPullSubscription map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixPullSubscription map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of PixPullSubscription maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"active\", \"canceled\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:flows` [list of strings, default nil]: direction of money flow to filter retrieved maps. Options: \"in\", \"out\".
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixPullSubscription maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 PixPullSubscription maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"active\", \"canceled\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:flows` [list of strings, default nil]: direction of money flow to filter retrieved maps. Options: \"in\", \"out\".
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of PixPullSubscription maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page of PixPullSubscription maps"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a PixPullSubscription's mutable parameters by passing its id.
  When patching `:status` to \"confirmed\", `:sender-city-code` MUST be present in the patch.

  ## Parameters (required):
    - `id` [string]: PixPullSubscription unique id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:status` [string]: new status to set. ex: \"confirmed\". When set to \"confirmed\", `:sender-city-code` is required.

  ## Parameters (conditionally required):
    - `:sender-city-code` [string, default nil]: IBGE code of the payer's city. Required when `:status` is being set to \"confirmed\".

  ## Options:
    - `:reason` [string, default nil]: reason for the patch. Options: \"accountClosed\", \"accountBlocked\", \"invalidBranchCode\", \"notRecognizedBySender\", \"userRejected\", \"notOffered\"
    - `:amount` [integer, default nil]: new amount in cents.
    - `:amount-min-limit` [integer, default nil]: new amount minimum limit.
    - `:due` [string, default nil]: new due date for the sender's answer.
    - `:pull-retry-limit` [integer, default nil]: new max number of retries.
    - `:tags` [list of strings, default nil]: new list of tags.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixPullSubscription with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel a PixPullSubscription entity previously created in the Stark Infra API.
  `:reason` is sent as a query parameter on the DELETE request.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"
    - `params` [map]:
      - `:reason` [string]: reason why the PixPullSubscription is being cancelled. As receiver: \"accountClosed\", \"receiverOrganizationClosed\", \"receiverInternalError\", \"fraud\", \"receiverUserRequested\", \"paymentNotFound\". As sender: \"accountClosed\", \"senderDeceased\", \"fraud\", \"senderUserRequested\", \"paymentNotFound\".

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled PixPullSubscription map"
  ([id params]
   (delete-id @credentials (resource) id params))

  ([id params user]
   (delete-id user (resource) id params)))

(defn parse
  "Create a single verified PixPullSubscription map from a content string received from a
  handler listening at the subscription url. If the provided digital signature does
  not check out with the Stark Infra public key, an ex-info carrying
  `:code \"invalidSignature\"` is thrown.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - Parsed PixPullSubscription map"
  ([content signature]
   (parse-and-verify content signature @credentials nil))

  ([content signature user]
   (parse-and-verify content signature user nil)))
