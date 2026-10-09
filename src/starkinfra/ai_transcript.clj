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
            [starkinfra.utils.rest :refer [get-page get-stream post-single]]))

(defn- resource []
  "ai-transcript")


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
   (post-single user (resource) (select-keys transcript [:audio]) {})))

(defn query
  "Receive a stream of AiTranscript maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiTranscript maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 AiTranscript maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of AiTranscript maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, nil when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
