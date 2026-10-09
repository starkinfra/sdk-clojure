(ns starkinfra.ai-chat
  "An AiChat is one conversation thread with an AiAgent and holds the history. Each turn is an AiMessage.
  When you initialize an AiChat, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:agent-id` [string]: id of the AiAgent that will answer in this chat. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:title` [string, default nil]: title of the conversation. Up to 100 characters. When omitted, the first message posted to the chat generates one.
    - `:tags` [list of strings, default nil]: list of strings for reference when searching for AiChats. ex: [\"support\" \"vip\"]
    - `:context` [map, default nil]: free-form data about the conversation, available to the agent. The keys are yours and are sent exactly as written. ex: {:order_id \"123\"}

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiChat is created. ex: \"5656565656565656\"
    - `:agent-name` [string]: name of the agent. Only present when requested with expand [:agent-name].
    - `:updated` [string]: latest update datetime for the AiChat. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [delete get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.request :refer [fetch]]
            [starkinfra.utils.rest :refer [get-keyed-page get-keyed-stream]]))

(defn- resource []
  "ai-chat")

(def ^:private preserved #{:context})

(defn- given [payload]
  (into {} (remove (comp nil? val)) payload))


(defn create
  "Send an AiChat map for creation at the Stark Infra API.

  ## Parameters (required):
    - `chat` [map]: AiChat map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiChat map with updated attributes"
  ([chat]
   (create chat @credentials))

  ([chat user]
   (-> (fetch user
              :post
              (resource)
              {:payload (given {"agentId" (:agent-id chat)
                                "title" (:title chat)
                                "tags" (:tags chat)
                                "context" (:context chat)})
               :preserve preserved})
       :content
       :chat)))

(defn get
  "Receive a single AiChat map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :agent-name
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiChat map that corresponds to the given id"
  ([id]
   (get id {} @credentials))

  ([id params]
   (get id params @credentials))

  ([id params user]
   (-> (fetch user :get (str (resource) "/" id) {:query (select-keys params [:expand]) :preserve preserved})
       :content
       :chat)))

(defn query
  "Receive a stream of AiChat maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:tags` [list of strings, default nil]: list of strings to filter retrieved maps. ex: [\"support\" \"vip\"]
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :agent-name
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiChat maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   (get-keyed-stream user (resource) (select-keys params [:limit :tags :expand]) :chats preserved)))

(defn page
  "Receive a list of up to 100 AiChat maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:tags` [list of strings, default nil]: list of strings to filter retrieved maps. ex: [\"support\" \"vip\"]
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :agent-name
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of AiChat maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, nil when there is none"
  ([]
   (page {} @credentials))

  ([params]
   (page params @credentials))

  ([params user]
   (get-keyed-page user (resource) (select-keys params [:cursor :limit :tags :expand]) :chats preserved)))

(defn update
  "Update an AiChat's parameters by passing its id. The API keeps what you do not send; clear a field by
  sending an empty value: \"\" for the title, [] for the tags, {} for the context.

  ## Parameters (required):
    - `id` [string]: AiChat id. ex: \"5656565656565656\"

  ## Options:
    - `:title` [string, default nil]: new title for the conversation. Up to 100 characters.
    - `:agent-id` [string, default nil]: id of the AiAgent that should answer from now on.
    - `:tags` [list of strings, default nil]: new list of tags. Replaces the current list.
    - `:context` [map, default nil]: new context of the conversation. The keys are yours and are sent exactly as written.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiChat map with updated attributes"
  ([id params]
   (update id params @credentials))

  ([id params user]
   (-> (fetch user
              :patch
              (str (resource) "/" id)
              {:payload {"title" (:title params)
                         "agentId" (:agent-id params)
                         "tags" (:tags params)
                         "context" (:context params)}
               :preserve preserved})
       :content
       :chat)))

(defn delete
  "Delete up to 100 AiChats at once, with their messages.

  ## Parameters (required):
    - `ids` [list of strings]: ids of the AiChats to be deleted. Up to 100 ids. ex: [\"5656565656565656\" \"4545454545454545\"]

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of deleted AiChat maps"
  ([ids]
   (delete ids @credentials))

  ([ids user]
   (-> (fetch user :delete (resource) {:query {:ids ids} :preserve preserved})
       :content
       :chats)))
