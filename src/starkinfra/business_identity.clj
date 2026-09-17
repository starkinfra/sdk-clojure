(ns starkinfra.business-identity
  "A BusinessIdentity represents the identity verification of a company (PJ), identified by
  its tax ID (CNPJ). It holds the company's registration data, the list of representatives,
  the attached documents, the extracted signature rules and the final verification status.
  When you initialize a BusinessIdentity, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:tax-id` [string]: company's tax ID (CNPJ). Must be a valid CNPJ, active in the official bureau, and returning at least one representative (sócio). ex: \"20.018.183/0001-80\"

  ## Parameters (optional):
    - `:tags` [list of strings, default []]: list of strings for reference when searching for BusinessIdentities. ex: [\"onboarding-123\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the BusinessIdentity is created. ex: \"5656565656565656\"
    - `:name` [string]: company's legal name, filled from the bureau. ex: \"STARK BANK S.A.\"
    - `:tax-id-status` [string]: status of the CNPJ at the bureau. ex: \"active\", \"blocked\", \"pending\"
    - `:insight-tax-id` [string]: tax ID extracted from the document by the insight. ex: \"20.018.183/0001-80\"
    - `:insight-document-type` [string]: document type detected by the insight. ex: \"articles-of-incorporation\"
    - `:num-pages` [integer]: number of pages of the document. ex: 5
    - `:representatives` [string]: JSON string of the company's representatives. ex: \"[{\\\"name\\\": \\\"Edward Stark\\\", \\\"qualification\\\": \\\"Diretor\\\"}]\"
    - `:attachments` [list of strings]: list of attached documents references. ex: [\"attachment/5656565656565656\"]
    - `:rules` [string]: JSON string of the complemented signature rules.
    - `:status` [string]: current status of the BusinessIdentity. ex: \"created\", \"pending\", \"canceled\", \"processing\", \"success\", \"failed\"
    - `:created` [string]: creation datetime for the BusinessIdentity. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the BusinessIdentity. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id post-multi]]))

(defn- resource []
  "business-identity")


(defn create
  "Send a list of BusinessIdentity maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `identities` [list of maps]: list of BusinessIdentity maps to be created in the API

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of BusinessIdentity maps with updated attributes"
  ([identities]
   (post-multi @credentials (resource) identities {}))

  ([identities user]
   (post-multi user (resource) identities {})))

(defn get
  "Receive a single BusinessIdentity map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - BusinessIdentity map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of BusinessIdentity maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"pending\", \"canceled\", \"processing\", \"success\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tax-ids` [list of strings, default nil]: list of company tax IDs (CNPJ) to filter retrieved maps. ex: [\"20.018.183/0001-80\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of BusinessIdentity maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 BusinessIdentity maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"pending\", \"canceled\", \"processing\", \"success\", \"failed\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tax-ids` [list of strings, default nil]: list of company tax IDs (CNPJ) to filter retrieved maps. ex: [\"20.018.183/0001-80\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of BusinessIdentity maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update a BusinessIdentity by passing id.

  ## Parameters (required):
    - `id` [string]: BusinessIdentity id. ex: \"5656565656565656\"

  ## Options:
    - `:status` [string, default nil]: only \"processing\" is accepted, to trigger the AI Model analysis. The identity must currently be in \"created\"/\"pending\" status and must already have at least one BusinessAttachment associated with it.
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for BusinessIdentities. ex: [\"onboarding-123\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target BusinessIdentity map with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel a BusinessIdentity entity previously created in the Stark Infra API. Only identities
  in the 'created' or 'pending' status can be canceled.

  ## Parameters (required):
    - `id` [string]: BusinessIdentity unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled BusinessIdentity map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))
