(ns starkinfra.business-attachment
  "A BusinessAttachment represents a document (articles of incorporation, bylaws, etc.) sent
  to a BusinessIdentity. You must reference the desired BusinessIdentity by its id.
  A BusinessIdentity accepts at most 2 attachments.
  When you initialize a BusinessAttachment, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:name` [string]: name of the document. Must be unique among the identity's \"created\" attachments. ex: \"articles-of-incorporation.pdf\"
    - `:content` [string or bytes]: Base64 data url of the document, or raw content to be encoded when `:content-type` is given. ex: \"data:application/pdf;base64,JVBERi0xLjQ...\"
    - `:business-identity-id` [string]: unique id of the BusinessIdentity this attachment belongs to. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:content-type` [string, default nil]: content MIME type. This parameter is required as input only. ex: \"application/pdf\", \"image/png\" or \"image/jpeg\"
    - `:tags` [list of strings, default []]: list of strings for reference when searching for BusinessAttachments. ex: [\"doc-principal\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the BusinessAttachment is created. ex: \"5656565656565656\"
    - `:attachment-id` [string]: id of the document in the external ms-attachment. ex: \"5104320788332544\"
    - `:status` [string]: current status of the BusinessAttachment. ex: \"created\", \"canceled\", \"approved\", \"denied\"
    - `:created` [string]: creation datetime for the BusinessAttachment. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the BusinessAttachment. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           post-multi]])
  (:import (java.util Base64)))

(defn- resource []
  "business-attachment")

(defn- encode-base64
  "Mirrors python's b64encode(content).decode()."
  [^bytes content]
  (.encodeToString (Base64/getEncoder) content))

(defn- blank-content?
  "python rejects any falsy content, the empty string and empty bytes
  included, not just a missing one."
  [content]
  (or (nil? content) (zero? (count content))))

(defn- build-content
  "content-type is input-only: sdk-python never keeps it as an attribute of the
  built entity, so it never reaches the wire either way."
  [{:keys [content content-type] :as attachment}]
  (when (and content-type (blank-content? content))
    (throw (IllegalArgumentException. "content is required when content-type is provided")))
  (-> (if content-type
        (assoc attachment :content (str "data:" content-type ";base64," (if (bytes? content) (encode-base64 content) content)))
        attachment)
      (dissoc :content-type)))


(defn create
  "Send a list of BusinessAttachment maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `attachments` [list of maps]: list of BusinessAttachment maps to be created in the API. Limited to 1 attachment per request. Only PDF, JPG and PNG files up to 8 MB are accepted; name must be unique among the identity's other \"created\" attachments; and the target BusinessIdentity must be in \"created\"/\"pending\" status with fewer than 2 attachments already on it.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of BusinessAttachment maps with updated attributes"
  ([attachments]
   (post-multi @credentials (resource) (mapv build-content attachments) {}))

  ([attachments user]
   (post-multi user (resource) (mapv build-content attachments) {})))

(defn get
  "Receive a single BusinessAttachment map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `params` [map]:
      - `:expand` [list of strings, default nil]: fields to expand information. ex: [\"content\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - BusinessAttachment map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id params]
   (get-id @credentials (resource) id params))

  ([id params user]
   (get-id user (resource) id params)))

(defn query
  "Receive a stream of BusinessAttachment maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"canceled\", \"approved\", \"denied\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of BusinessAttachment maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 BusinessAttachment maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"canceled\", \"approved\", \"denied\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of BusinessAttachment maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn cancel
  "Cancel a BusinessAttachment entity previously created in the Stark Infra API. Only attachments
  in the 'created' status can be canceled.

  ## Parameters (required):
    - `id` [string]: BusinessAttachment unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled BusinessAttachment map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
