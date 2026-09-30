(ns starkinfra.ai-transcript
  "An AiTranscript is the text of an audio file you upload, from any speaker, cloned or not.
  When you initialize an AiTranscript, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:audio` [string]: base64-encoded audio to transcribe. Up to 10000000 characters. The format is read from the file's own header.

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiTranscript is created. ex: \"5656565656565656\"
    - `:text` [string]: transcribed text.
    - `:status` [string]: current status of the transcript. Options: \"processing\", \"success\", \"failed\"
    - `:errors` [list of strings]: reasons the transcription failed. Empty when it worked.
    - `:created` [string]: creation datetime for the AiTranscript. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the AiTranscript. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.ai-api :as ai-api]))

(def ^:private api {:path "ai-transcript" :one :transcript :many :transcripts})


(defn create
  "Send an AiTranscript map for creation at the Stark Infra API. The audio is transcribed during the call.

  ## Parameters (required):
    - `transcript` [map]: AiTranscript map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiTranscript map with updated attributes"
  ([transcript]
   (create transcript @credentials))

  ([transcript user]
   (ai-api/create-one api (ai-api/payload "audio" (:audio transcript)) nil user)))

(defn query
  "Receive a stream of AiTranscript maps previously created in the Stark Infra API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiTranscript maps with updated attributes"
  ([]
   (query @credentials))

  ([user]
   ;; this route is not paginated and takes no filters
   (ai-api/list-all api user)))
