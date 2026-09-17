(ns starkinfra.request
  "Sends HTTP requests to arbitrary Stark Infra routes, for features the SDK has
  not mapped yet. Argument order follows sdk-python: path, body, query, user."
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :as rest]))

;; sdk-python tags these calls with the `Joker` User-Agent prefix so the API can
;; tell a mapped resource call from a raw one.
(def ^:private prefix "Joker")

;; sdk-python passes raiseException=False on every raw verb, so an API error
;; comes back as the {:status :content} map instead of an exception.
(def ^:private throw-error false)


(defn get
  "Retrieve any Stark Infra resource.
  Receive a json of resources previously created in the Stark Infra API.

  ## Parameters (required):
    - `path` [string]: Stark Infra resource's route. ex: \"pix-request\"

  ## Parameters (optional):
    - `query` [map, default nil]: query parameters. ex: {:limit 1 :status \"success\"}
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:status` and `:content`"
  ([path]
   (rest/get-raw @credentials path {} prefix throw-error))

  ([path query]
   (rest/get-raw @credentials path query prefix throw-error))

  ([path query user]
   (rest/get-raw user path query prefix throw-error)))

(defn post
  "Create any Stark Infra resource.
  Send a json of resources and create any Stark Infra resource objects.

  ## Parameters (required):
    - `path` [string]: Stark Infra resource's route. ex: \"pix-request\"
    - `body` [map]: request parameters. ex: {:requests [{:amount 100 :external-id \"141234121\"}]}

  ## Parameters (optional):
    - `query` [map, default nil]: query parameters. ex: {:limit 1 :status \"success\"}
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:status` and `:content`"
  ([path body]
   (rest/post-raw @credentials path body {} prefix throw-error))

  ([path body query]
   (rest/post-raw @credentials path body query prefix throw-error))

  ([path body query user]
   (rest/post-raw user path body query prefix throw-error)))

(defn patch
  "Update any Stark Infra resource.
  Send a json with the parameters of a single Stark Infra resource object and update it.

  ## Parameters (required):
    - `path` [string]: Stark Infra resource's route. ex: \"issuing-holder/5155165527080960\"
    - `body` [map]: request parameters. ex: {:tags [\"Arya\" \"Stark\"]}

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:status` and `:content`"
  ([path body]
   (rest/patch-raw @credentials path body {} prefix throw-error))

  ([path body user]
   (rest/patch-raw user path body {} prefix throw-error)))

(defn put
  "Put any Stark Infra resource.
  Send a json with the parameters of a single Stark Infra resource object and create it.
  If the resource already exists, you will update it.

  ## Parameters (required):
    - `path` [string]: Stark Infra resource's route. ex: \"pix-request\"
    - `body` [map]: request parameters. ex: {:amount 100}

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:status` and `:content`"
  ([path body]
   (rest/put-raw @credentials path body {} prefix throw-error))

  ([path body user]
   (rest/put-raw user path body {} prefix throw-error)))

(defn delete
  "Delete any Stark Infra resource.
  Send a json with the parameters of a single Stark Infra resource object and delete it.

  ## Parameters (required):
    - `path` [string]: Stark Infra resource's route. ex: \"issuing-holder/5155165527080960\"

  ## Parameters (optional):
    - `body` [map, default nil]: request parameters. ex: {:amount 100}
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:status` and `:content`"
  ([path]
   (rest/delete-raw @credentials path {} {} prefix throw-error))

  ([path body]
   (rest/delete-raw @credentials path body {} prefix throw-error))

  ([path body user]
   (rest/delete-raw user path body {} prefix throw-error)))
