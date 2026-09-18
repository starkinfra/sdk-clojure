(ns starkinfra.event
  "An Event is the notification received from the subscription to the Webhook.
  Events cannot be created, but may be retrieved from the Stark Infra API to
  list all generated updates on entities.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the Event is created. ex: \"5656565656565656\"
    - `:log` [map]: a Log map from one of the subscribed services (PixRequestLog, PixReversalLog)
    - `:created` [string]: creation datetime for the notification Event. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:is-delivered` [bool]: true if the Event has been successfully delivered to the user url. ex: false
    - `:subscription` [string]: service that triggered this Event. Options: \"pix-request.in\", \"pix-request.out\", \"pix-reversal.in\", \"pix-reversal.out\", \"pix-key\", \"pix-claim\", \"pix-infraction\", \"pix-chargeback\", \"pix-dispute\", \"pix-pull-subscription\", \"pix-pull-request\", \"issuing-card\", \"issuing-invoice\", \"issuing-purchase\", \"credit-note\", \"business-identity\"
    - `:workspace-id` [string]: ID of the Workspace that generated this Event. Mostly used when multiple Workspaces have Webhooks registered to the same endpoint. ex: \"4545454545454545\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.parse :refer [parse-and-verify]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id]]))

(defn- resource []
  "event")


(defn get
  "Receive a single notification Event map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - Event map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of notification Event maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:is-delivered` [bool, default nil]: bool to filter successfully delivered events. ex: true or false
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of Event maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 Event maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:is-delivered` [bool, default nil]: bool to filter successfully delivered events. ex: true or false
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of Event maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn delete
  "Delete a notification Event entity previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: Event unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - deleted Event map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))

(defn update
  "Update a notification Event by passing its id.
  If `:is-delivered` is true, the event will no longer be returned on queries with `:is-delivered` false.

  ## Parameters (required):
    - `id` [string]: Event unique id. ex: \"5656565656565656\"
    - `:is-delivered` [bool]: if true and the event hasn't been delivered already, the event will be set as delivered. ex: true

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target Event with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn parse
  "Create a single notification Event map received from Event listening at your
  subscribed endpoint. If the provided digital signature does not check out with
  the Stark Infra public key, an ex-info carrying `:code \"invalidSignature\"`
  is thrown.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - parsed Event map"
  ([content signature]
   (parse-and-verify content signature @credentials "event"))

  ([content signature user]
   (parse-and-verify content signature user "event")))
