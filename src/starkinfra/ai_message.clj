(ns starkinfra.ai-message
  "An AiMessage is a single turn of an AiChat. You post what the user said and the same call returns the user's
  message and the agent's answer.
  When you initialize an AiMessage, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the user's message and the agent's answer.

  ## Parameters (required):
    - `:chat-id` [string]: id of the AiChat to post to. ex: \"5656565656565656\"
    - `:text` [string]: content of the user's message. Between 1 and 50000 characters. ex: \"What is the status of my order?\"

  ## Parameters (optional):
    - `:model` [string, default nil]: AI model to use for this turn only. Options: \"bender-1.0\", \"prime-1.0\". The API defaults to the agent's own model.

  ## Attributes (return-only):
    - `:id` [string]: unique id of the AiMessage. ex: \"5656565656565656\"
    - `:sender` [string]: who wrote the message. Options: \"user\", \"system\". The agent's answers are sent by \"system\".
    - `:speech` [string]: version of the text written to be heard rather than read, ready to be sent to AiSpeech. Only filled when the agent has a voice.
    - `:metadata` [map]: structured data the agent extracted, shaped by the agent's metadata schema. The keys are the agent's, exactly as it declared them.
    - `:chat-name` [string]: title of the chat. Only present when create is called with expand [:chat-name].
    - `:created` [string]: creation datetime for the AiMessage. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.request :refer [fetch]]
            [starkinfra.utils.rest :refer [get-keyed-page get-keyed-stream]]))

(defn- resource []
  "ai-message")

(def ^:private preserved #{:metadata})


(defn create
  "Post the user's message to an AiChat. The call waits for the agent, which takes a few seconds, and returns both messages.
  The answer is a list under :messages with the chat name beside it, so each message gets :chat-name from there.

  ## Parameters (required):
    - `message` [map]: AiMessage map with :chat-id and :text, to be created in the API.

  ## Options:
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :chat-name, which returns the chat title on every message, useful on the first turn, when the title is generated.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list with the user's AiMessage map and the agent's AiMessage map"
  ([message]
   (create message {} @credentials))

  ([message params]
   (create message params @credentials))

  ([message params user]
   (let [answer (:content (fetch user
                                 :post
                                 (resource)
                                 {:payload (into {}
                                                 (remove (comp nil? val))
                                                 {"chatId" (:chat-id message)
                                                  "text" (:text message)
                                                  "model" (:model message)})
                                  :query (select-keys params [:expand])
                                  :preserve preserved}))]
     (mapv #(assoc % :chat-name (:chat-name answer))
           (:messages answer)))))

(defn query
  "Receive a stream of AiMessage maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:chat-id` [string, default nil]: id of the AiChat whose messages you want. Every message of the workspace when omitted. ex: \"5656565656565656\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiMessage maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   (get-keyed-stream user (resource) (select-keys params [:limit :chat-id]) :messages preserved)))

(defn page
  "Receive a list of up to 100 AiMessage maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:chat-id` [string, default nil]: id of the AiChat whose messages you want. Every message of the workspace when omitted. ex: \"5656565656565656\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of AiMessage maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, nil when there is none"
  ([]
   (page {} @credentials))

  ([params]
   (page params @credentials))

  ([params user]
   (get-keyed-page user (resource) (select-keys params [:cursor :limit :chat-id]) :messages preserved)))
