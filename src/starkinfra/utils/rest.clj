(ns starkinfra.utils.rest
  "Thin wrappers binding the Stark Infra settings to the core REST verbs."
  (:require [core-clojure.utils.api :refer [endpoint last-name]]
            [core-clojure.utils.case :refer [cast-keys-to-kebab json-to-map
                                             kebab-to-camel]]
            [core-clojure.utils.request :refer [fetch]]
            [core-clojure.utils.rest :as rest]
            [starkinfra.settings :refer [api-version error-lang host sdk-version
                                         timeout]]))


(defn- cast-query
  "core-clojure camelCases query keys but leaves the values of `expand` alone,
  and the API only understands camelCased expand fields (core-python does this
  in `url.py`). Kebab in, camel on the wire."
  [query]
  (if (seq (:expand query))
    (update query :expand #(mapv (fn [field] (name (kebab-to-camel field))) %))
    query))

(defn- drop-nils
  "python's `api_json` drops nil members before serializing a write payload;
  core-clojure's writer keeps them, so an optional parameter left nil would
  reach the API as an explicit null. The raw verbs deliberately skip this:
  sdk-python sends their bodies through untouched."
  [payload]
  (cond
    (map? payload) (reduce (fn [acc [k v]]
                             (if (nil? v)
                               acc
                               (assoc acc k (drop-nils v))))
                           {}
                           payload)
    (sequential? payload) (mapv drop-nils payload)
    :else payload))

(defn- raw-response [response]
  {:status (:status response)
   :content (cast-keys-to-kebab (json-to-map (:content response)))})

(defn get-page [user path query]
  (rest/get-page host sdk-version user path (cast-query query) api-version @error-lang timeout))

(defn get-stream [user path query]
  (rest/get-stream host sdk-version user path (cast-query query) api-version @error-lang timeout))

(defn get-id [user path id query]
  (rest/get-id host sdk-version user path id (cast-query query) api-version @error-lang timeout))

(defn get-content [user path id sub-resource query]
  (rest/get-content host sdk-version user path id sub-resource (cast-query query) api-version @error-lang timeout))

(defn get-sub-resource [user path id sub-resource query]
  (rest/get-sub-resource host sdk-version user path id sub-resource (cast-query query) api-version @error-lang timeout))

(defn get-public-key [user]
  (rest/get-public-key host sdk-version user api-version @error-lang timeout))

(defn post-multi [user path payload query]
  (rest/post-multi host sdk-version user path (drop-nils payload) (cast-query query) api-version @error-lang timeout))

(defn post-single [user path payload query]
  (rest/post-single host sdk-version user path (drop-nils payload) (cast-query query) api-version @error-lang timeout))

(defn patch-id [user path payload id]
  (rest/patch-id host sdk-version user path (drop-nils payload) id api-version @error-lang timeout))

(defn delete-id
  "DELETE {endpoint}/{id} carrying an optional query map, the shape python's
  `delete_id(**query)` has. Core's own delete-id drops the query and appends a
  trailing slash, so this one goes straight to core's `fetch`."
  [user path id query]
  (let [response (fetch host
                        sdk-version
                        user
                        :delete
                        (str (endpoint path) "/" id)
                        ""
                        (cast-query query)
                        api-version
                        @error-lang
                        timeout
                        ""
                        true)]
    (get (cast-keys-to-kebab (json-to-map (:content response)))
         (keyword (last-name path)))))

(defn get-raw [user path query prefix throw-error]
  (rest/get-raw host sdk-version user path (cast-query query) api-version @error-lang timeout prefix throw-error))

(defn post-raw [user path payload query prefix throw-error]
  (rest/post-raw host sdk-version user path payload (cast-query query) api-version @error-lang timeout prefix throw-error))

(defn patch-raw [user path payload query prefix throw-error]
  (rest/patch-raw host sdk-version user path payload (cast-query query) api-version @error-lang timeout prefix throw-error))

(defn put-raw
  "Core's `put-raw`, not `patch-raw`: the Stark Bank template wires this one to
  the wrong verb."
  [user path payload query prefix throw-error]
  (rest/put-raw host sdk-version user path payload (cast-query query) api-version @error-lang timeout prefix throw-error))

(defn delete-raw
  "Core's `delete-raw` hardcodes an empty payload and query; python's
  `delete_raw` forwards both, so this one calls `fetch` directly."
  [user path payload query prefix throw-error]
  (raw-response (fetch host
                       sdk-version
                       user
                       :delete
                       path
                       payload
                       (cast-query query)
                       api-version
                       @error-lang
                       timeout
                       prefix
                       throw-error)))
