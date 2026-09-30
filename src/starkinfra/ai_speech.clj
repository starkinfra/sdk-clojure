(ns starkinfra.ai-speech
  "An AiSpeech is one text read out loud by an AiVoice. The speech is synthesized when it is created and comes
  back as a base64 MP3 in the audio attribute.
  When you initialize an AiSpeech, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:voice-id` [string]: id of the AiVoice that should read the text. Only a voice in \"success\" can speak. ex: \"5656565656565656\"
    - `:text` [string]: text to read out loud. Between 1 and 100000 characters. ex: \"Hello, how can I help you?\"

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiSpeech is created. ex: \"5656565656565656\"
    - `:status` [string]: current status of the speech. Options: \"processing\", \"success\", \"failed\"
    - `:audio` [string]: base64-encoded MP3 of the speech. Left out of query results; get returns it unless fields is given without it.
    - `:voice-name` [string]: name of the voice. Only present when requested with expand [:voice-name].
    - `:errors` [list of strings]: reasons the synthesis failed. Empty when it worked.
    - `:created` [string]: creation datetime for the AiSpeech. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the AiSpeech. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.ai-api :as ai-api]))

;; the list key is \"speeches\", which starkinfra.utils.rest would derive as \"speechs\"
(def ^:private api {:path "ai-speech" :one :speech :many :speeches})


(defn create
  "Send an AiSpeech map for creation at the Stark Infra API. The audio is synthesized during the call.

  ## Parameters (required):
    - `speech` [map]: AiSpeech map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiSpeech map with updated attributes"
  ([speech]
   (create speech @credentials))

  ([speech user]
   (ai-api/create-one api
                      (ai-api/payload "voiceId" (:voice-id speech)
                                      "text" (:text speech))
                      nil
                      user)))

(defn get
  "Receive a single AiSpeech map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:fields` [list of keywords, default nil]: attributes to keep in the response. The audio is only attached when fields is omitted or lists :audio. ex: [:id :status :audio]
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :voice-name. When fields is also given, the expanded attribute must be listed there too.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiSpeech map that corresponds to the given id"
  ([id]
   (get id {} @credentials))

  ([id params]
   (get id params @credentials))

  ([id params user]
   (ai-api/get-one api id params user)))

(defn query
  "Receive a stream of AiSpeech maps previously created in the Stark Infra API. The audio is left out of the results.

  ## Options:
    - `:fields` [list of keywords, default nil]: attributes to keep in the response. ex: [:id :status]
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :voice-name. When fields is also given, the expanded attribute must be listed there too.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiSpeech maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   ;; this route is not paginated and rejects limit, cursor and every filter (invalidQueryString)
   (ai-api/list-all api params user)))
