(ns starkinfra.ai-agent
  "An AiAgent is the configuration of an assistant: the model, the instructions, the knowledge it may consult and
  the voice it speaks with. The agent never changes during a conversation; the conversation lives in an AiChat
  and each turn is an AiMessage.
  When you initialize an AiAgent, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:name` [string]: name of the agent. Between 1 and 100 characters. ex: \"Support assistant\"
    - `:model` [string]: AI model the agent runs on. Options: \"bender-1.0\" for everyday conversations, \"prime-1.0\" for harder reasoning.

  ## Parameters (optional):
    - `:system-prompt` [string, default nil]: instructions that define the agent's persona, tone and domain behavior. Up to 100000 characters. The API falls back to its default assistant prompt when omitted.
    - `:voice-id` [string, default nil]: id of the AiVoice the agent speaks with. When set, every reply also carries a speech string ready to be sent to AiSpeech. The API does not check that the voice exists.
    - `:knowledge-base-ids` [list of strings, default nil]: ids of up to 100 AiKnowledgeBases the agent retrieves from before answering. The API does not check that they exist.
    - `:metadata-schema` [map, default nil]: flat map whose keys are the fields the agent must extract on every reply. Each field takes a \"type\" (string, integer, number, boolean or array), an optional \"description\" of up to 2000 characters, an optional \"enum\" of up to 20 strings for string fields. The keys are yours and are sent exactly as written. ex: {:order_id {:type \"string\" :description \"Order the customer mentions\"}}

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiAgent is created. ex: \"5656565656565656\"
    - `:knowledge-bases` [list of AiKnowledgeBase maps]: the knowledge bases themselves. Only present when requested with expand [:knowledge-bases].
    - `:created` [string]: creation datetime for the AiAgent. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the AiAgent. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [delete get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.request :refer [fetch]]
            [starkinfra.utils.rest :refer [get-keyed-page get-keyed-stream]]))

(defn- resource []
  "ai-agent")

(def ^:private preserved #{:metadata-schema})

(defn- body [{:keys [name model system-prompt voice-id knowledge-base-ids metadata-schema]}]
  {"name" name
   "model" model
   "systemPrompt" system-prompt
   "voiceId" voice-id
   "knowledgeBaseIds" knowledge-base-ids
   "metadataSchema" metadata-schema})

(defn- given [payload]
  (into {} (remove (comp nil? val)) payload))


(defn create
  "Send an AiAgent map for creation at the Stark Infra API.

  ## Parameters (required):
    - `agent` [map]: AiAgent map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiAgent map with updated attributes"
  ([agent]
   (create agent @credentials))

  ([agent user]
   (-> (fetch user :post (resource) {:payload (given (body agent)) :preserve preserved})
       :content
       :agent)))

(defn get
  "Receive a single AiAgent map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :knowledge-bases
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiAgent map that corresponds to the given id"
  ([id]
   (get id {} @credentials))

  ([id params]
   (get id params @credentials))

  ([id params user]
   (-> (fetch user :get (str (resource) "/" id) {:query (select-keys params [:expand]) :preserve preserved})
       :content
       :agent)))

(defn query
  "Receive a stream of AiAgent maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :knowledge-bases
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiAgent maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   (get-keyed-stream user (resource) (select-keys params [:limit :expand]) :agents preserved)))

(defn page
  "Receive a list of up to 100 AiAgent maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :knowledge-bases
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of AiAgent maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, nil when there is none"
  ([]
   (page {} @credentials))

  ([params]
   (page params @credentials))

  ([params user]
   (get-keyed-page user (resource) (select-keys params [:cursor :limit :expand]) :agents preserved)))

(defn update
  "Update an AiAgent's parameters by passing its id. The API keeps what you do not send; clear a field by
  sending an empty value: \"\" for a string, [] for a list, {} for a map.

  ## Parameters (required):
    - `id` [string]: AiAgent id. ex: \"5656565656565656\"

  ## Options:
    - `:name` [string, default nil]: new name for the agent. Between 1 and 100 characters.
    - `:model` [string, default nil]: new AI model. Options: \"bender-1.0\", \"prime-1.0\"
    - `:system-prompt` [string, default nil]: new instructions for the agent. Up to 100000 characters.
    - `:voice-id` [string, default nil]: new AiVoice id.
    - `:knowledge-base-ids` [list of strings, default nil]: the AiKnowledgeBase ids the agent should end up with. Replaces the current list.
    - `:metadata-schema` [map, default nil]: new schema of the structured data the agent must extract. The keys are yours and are sent exactly as written.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiAgent map with updated attributes"
  ([id params]
   (update id params @credentials))

  ([id params user]
   (-> (fetch user :patch (str (resource) "/" id) {:payload (body params) :preserve preserved})
       :content
       :agent)))

(defn delete
  "Delete up to 100 AiAgents at once.

  ## Parameters (required):
    - `ids` [list of strings]: ids of the AiAgents to be deleted. Up to 100 ids. ex: [\"5656565656565656\" \"4545454545454545\"]

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of deleted AiAgent maps"
  ([ids]
   (delete ids @credentials))

  ([ids user]
   (-> (fetch user :delete (resource) {:query {:ids ids} :preserve preserved})
       :content
       :agents)))
