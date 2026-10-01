(ns starkinfra.ai-chat
  "An AiChat is one conversation thread with an AiAgent and holds the history. Each turn is an AiMessage.
  When you initialize an AiChat, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:agent-id` [string]: id of the AiAgent that will answer in this chat. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `:title` [string, default nil]: title of the conversation. Up to 100 characters. When omitted, the first message posted to the chat generates one.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiChat is created. ex: \"5656565656565656\"
    - `:agent-name` [string]: name of the agent. Only present when requested with expand [:agent-name].
    - `:updated` [string]: latest update datetime for the AiChat. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [delete get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.ai-api :as ai-api]))

(def ^:private api {:path "ai-chat" :one :chat :many :chats})


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
   (ai-api/create-one api
                      (ai-api/payload "agentId" (:agent-id chat)
                                      "title" (:title chat))
                      nil
                      user)))

(defn get
  "Receive a single AiChat map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:fields` [list of keywords, default nil]: attributes to keep in the response. ex: [:id :title]
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :agent-name. When fields is also given, the expanded attribute must be listed there too.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiChat map that corresponds to the given id"
  ([id]
   (get id {} @credentials))

  ([id params]
   (get id params @credentials))

  ([id params user]
   (ai-api/get-one api id params user)))

(defn query
  "Receive a stream of AiChat maps previously created in the Stark Infra API.

  ## Options:
    - `:fields` [list of keywords, default nil]: attributes to keep in the response. ex: [:id :title]
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :agent-name. When fields is also given, the expanded attribute must be listed there too.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiChat maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   ;; this route is not paginated and rejects limit, cursor and every filter (invalidQueryString)
   (ai-api/list-all api params user)))

(defn update
  "Update an AiChat's parameters by passing its id. Only the parameters you give are changed.

  ## Parameters (required):
    - `id` [string]: AiChat id. ex: \"5656565656565656\"

  ## Options:
    - `:title` [string, default nil]: new title for the conversation. Up to 100 characters.
    - `:agent-id` [string, default nil]: id of the AiAgent that should answer from now on.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiChat map with updated attributes"
  ([id params]
   (update id params @credentials))

  ([id params user]
   (ai-api/patch-one api
                     id
                     (ai-api/payload "title" (:title params)
                                     "agentId" (:agent-id params))
                     user)))

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
   (ai-api/delete-many api ids user)))
