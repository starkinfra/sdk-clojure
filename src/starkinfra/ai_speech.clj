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
    - `:audio` [string]: base64-encoded MP3 of the speech. Left out of query and page results; get returns it.
    - `:voice-name` [string]: name of the voice. Only present when requested with expand [:voice-name].
    - `:errors` [list of strings]: reasons the synthesis failed. Empty when it worked.
    - `:created` [string]: creation datetime for the AiSpeech. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the AiSpeech. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-keyed-page get-keyed-stream post-single]]))

(defn- resource []
  "ai-speech")


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
   (post-single user (resource) (select-keys speech [:voice-id :text]) {})))

(defn get
  "Receive a single AiSpeech map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Options:
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :voice-name
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiSpeech map that corresponds to the given id"
  ([id]
   (get id {} @credentials))

  ([id params]
   (get id params @credentials))

  ([id params user]
   (get-id user (resource) id (select-keys params [:expand]))))

(defn query
  "Receive a stream of AiSpeech maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :voice-name
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiSpeech maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   (get-keyed-stream user (resource) (select-keys params [:limit :expand]) :speeches nil)))

(defn page
  "Receive a list of up to 100 AiSpeech maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:expand` [list of keywords, default nil]: extra attributes to compute. Options: :voice-name
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of AiSpeech maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, nil when there is none"
  ([]
   (page {} @credentials))

  ([params]
   (page params @credentials))

  ([params user]
   (get-keyed-page user (resource) (select-keys params [:cursor :limit :expand]) :speeches nil)))
