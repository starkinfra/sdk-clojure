(ns starkinfra.event.attempt
  "When an Event delivery fails, an event attempt will be registered.
  It carries information meant to help you debug event reception issues.

  ## Attributes (return-only):
    - `:id` [string]: unique id that identifies the delivery attempt. ex: \"5656565656565656\"
    - `:code` [string]: delivery error code. ex: badHttpStatus, badConnection, timeout
    - `:message` [string]: delivery error full description. ex: \"HTTP POST request returned status 404\"
    - `:event-id` [string]: ID of the Event whose delivery failed. ex: \"4848484848484848\"
    - `:webhook-id` [string]: ID of the Webhook that triggered this event. ex: \"5656565656565656\"
    - `:created` [string]: datetime representing the moment when the attempt was made. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream]]))

(defn- resource []
  "event-attempt")


(defn get
  "Receive a single event.Attempt map previously created by the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - event.Attempt map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of event.Attempt maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:event-ids` [list of strings, default nil]: list of Event ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:webhook-ids` [list of strings, default nil]: list of Webhook ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of event.Attempt maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 event.Attempt maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:event-ids` [list of strings, default nil]: list of Event ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:webhook-ids` [list of strings, default nil]: list of Webhook ids to filter attempts. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of event.Attempt maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
