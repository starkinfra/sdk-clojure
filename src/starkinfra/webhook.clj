(ns starkinfra.webhook
  "A Webhook is used to subscribe to notification events on a user-selected endpoint.
  Currently, available services for subscription are credit-note, issuing-card,
  issuing-invoice, issuing-purchase, pix-request.in, pix-request.out,
  pix-reversal.in, pix-reversal.out, pix-claim, pix-key, pix-chargeback and
  pix-infraction.

  ## Parameters (required):
    - `:url` [string]: url that will be notified when an event occurs.
    - `:subscriptions` [list of strings]: list of any non-empty combination of the available services. Options: \"pix-request\", \"pix-reversal\", \"pix-pull-subscription\", \"pix-pull-request\", \"pix-internal-transaction-report\", \"pix-key\", \"pix-key-holmes\", \"pix-claim\", \"pix-infraction\", \"pix-chargeback\", \"pix-dispute\", \"issuing-card\", \"issuing-holder\", \"issuing-purchase\", \"issuing-invoice\", \"credit-note\", \"credit-holmes\"

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the webhook is created. ex: \"5656565656565656\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           post-single]]))

(defn- resource []
  "webhook")


(defn create
  "Send a single Webhook subscription for creation at the Stark Infra API.

  ## Parameters (required):
    - `params` [map]:
      - `:url` [string]: url to which notification events will be sent to. ex: \"https://webhook.site/60e9c18e-4b5c-4369-bda1-ab5fcd8e1b29\"
      - `:subscriptions` [list of strings]: list of any non-empty combination of the available services. ex: [\"credit-note\", \"pix-request.in\", \"pix-request.out\"]

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - Webhook map with updated attributes"
  ([params]
   (post-single @credentials (resource) params {}))

  ([params user]
   (post-single user (resource) params {})))

(defn get
  "Receive a single Webhook subscription map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - Webhook map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of Webhook subscription maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of Webhook maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 Webhook subscription maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of Webhook maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn delete
  "Delete a Webhook subscription entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: Webhook unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - deleted Webhook map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
