(ns starkinfra.ai-voice
  "An AiVoice is a voice cloned from a recording you upload. Once cloned, it can read any text out loud
  through an AiSpeech, and it can be attached to an AiAgent so every reply carries a speech ready to be synthesized.
  Cloning is asynchronous: the voice is created in \"processing\" status and moves to \"success\" when it is ready
  to speak, or to \"failed\" when the recording could not be cloned.
  When you initialize an AiVoice, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:audio` [string]: base64-encoded recording of the speaker. MP3, WAV, OGG, FLAC and WebM are accepted. Up to 10000000 characters.

  ## Parameters (optional):
    - `:name` [string, default nil]: name of the voice. Up to 100 characters. Defaults to the voice's own id. ex: \"Helena\"
    - `:description` [string, default nil]: free-text description of the voice. Up to 1000 characters.
    - `:language` [string, default nil]: language the voice speaks. Options: \"portuguese\", \"english\". The API defaults to \"portuguese\".
    - `:gender` [string, default nil]: gender of the voice. Options: \"male\", \"female\", \"neutral\"

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiVoice is created. This is the voice id you send to other AI resources. ex: \"5656565656565656\"
    - `:status` [string]: current status of the voice. Options: \"processing\", \"success\", \"failed\". Only a voice in \"success\" can speak.
    - `:errors` [list of strings]: reasons the cloning failed. Empty while the voice is healthy.
    - `:created` [string]: creation datetime for the AiVoice. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the AiVoice. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [delete])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.request :refer [fetch]]
            [starkinfra.utils.rest :refer [get-page get-stream post-single]]))

(defn- resource []
  "ai-voice")


(defn create
  "Send an AiVoice map for creation at the Stark Infra API and start cloning it.
  The call returns immediately with the voice in \"processing\" status.

  ## Parameters (required):
    - `voice` [map]: AiVoice map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiVoice map with updated attributes"
  ([voice]
   (create voice @credentials))

  ([voice user]
   (post-single user (resource) (select-keys voice [:audio :name :description :language :gender]) {})))

(defn query
  "Receive a stream of AiVoice maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiVoice maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 AiVoice maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of AiVoice maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, nil when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn delete
  "Delete up to 100 AiVoices at once.

  ## Parameters (required):
    - `ids` [list of strings]: ids of the AiVoices to be deleted. Up to 100 ids. ex: [\"5656565656565656\" \"4545454545454545\"]

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of deleted AiVoice maps"
  ([ids]
   (delete ids @credentials))

  ([ids user]
   (:voices (:content (fetch user :delete (resource) {:query {:ids ids}})))))
