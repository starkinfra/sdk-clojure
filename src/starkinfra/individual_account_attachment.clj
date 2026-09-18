(ns starkinfra.individual-account-attachment
  "You can create an IndividualAccountAttachment to attach images of documents
  to a specific IndividualAccountRequest. You must reference the desired IndividualAccountRequest by its id.
  When you initialize an IndividualAccountAttachment, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:type` [string]: type of the IndividualAccountAttachment. Options: \"drivers-license-front\", \"drivers-license-back\", \"identity-front\" or \"identity-back\"
    - `:content` [string or bytes]: raw image bytes of the picture, or an already-built Base64 data url. Raw bytes become a data url only when `:content-type` is also given.
    - `:content-type` [string]: content MIME type. This parameter is required as input only. ex: \"image/png\" or \"image/jpeg\"
    - `:account-request-id` [string]: unique id of the IndividualAccountRequest. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:tags` [list of strings, default []]: list of strings for reference when searching for IndividualAccountAttachments. ex: [\"employees\", \"monthly\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the IndividualAccountAttachment is created. ex: \"5656565656565656\"
    - `:status` [string]: current status of the IndividualAccountAttachment. ex: \"created\", \"success\", \"failed\" or \"deleted\"
    - `:created` [string]: creation datetime for the IndividualAccountAttachment. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           post-multi]])
  (:import (java.util Base64)))

(defn- resource []
  "individual-account-attachment")

(defn- encode-base64
  "Mirrors python's b64encode(content).decode('utf-8')."
  [^bytes content]
  (.encodeToString (Base64/getEncoder) content))

(defn- build-content
  "content-type is input-only: sdk-python folds it into the data url (when
  content is raw bytes) and never sends content-type as its own wire field.
  Raw bytes with no content-type are base64-encoded so the request is still
  serializable; the API is left to reject it."
  [{:keys [content content-type] :as attachment}]
  (cond
    (and content-type (bytes? content))
    (-> attachment
        (assoc :content (str "data:" content-type ";base64," (encode-base64 content)))
        (dissoc :content-type))

    content-type
    (dissoc attachment :content-type)

    (bytes? content)
    (assoc attachment :content (encode-base64 content))

    :else attachment))


(defn create
  "Send a list of IndividualAccountAttachment maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `attachments` [list of maps]: list of IndividualAccountAttachment maps to be created in the API

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of IndividualAccountAttachment maps with updated attributes"
  ([attachments]
   (post-multi @credentials (resource) (mapv build-content attachments) {}))

  ([attachments user]
   (post-multi user (resource) (mapv build-content attachments) {})))

(defn get
  "Receive a single IndividualAccountAttachment map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IndividualAccountAttachment map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IndividualAccountAttachment maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"success\", \"failed\", \"deleted\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IndividualAccountAttachment maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IndividualAccountAttachment maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"success\", \"failed\", \"deleted\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IndividualAccountAttachment maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn cancel
  "Delete an IndividualAccountAttachment entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - deleted IndividualAccountAttachment map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
