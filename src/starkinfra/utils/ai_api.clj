(ns starkinfra.utils.ai-api
  "The plumbing the AI resources share, mirroring sdk-python `utils/aiapi.py`.

  starkinfra.utils.rest reads the response key from the last word of the path, which cannot
  work here: the API answers \"speeches\", and creating an AiMessage answers a list. And `fetch`
  kebab-cases every key it parses, which would rewrite names the caller owns (the keys of an
  agent's metadata schema, of a message's metadata). So these functions read the body raw and
  rename only the fixed attribute names of each resource.

  An `api` map describes one resource:
    - `:path` [string]: route, with no leading slash. ex: \"ai-voice\"
    - `:one` [keyword]: key the API answers a single object under. ex: :voice
    - `:many` [keyword]: key the API answers a list under. ex: :voices
    - `:preserved` [set of keywords, default #{}]: kebab-cased attributes whose nested keys belong to the caller"
  (:require [cheshire.core :as cheshire]
            [starkinfra.utils.case :as casing]
            [starkinfra.utils.request :refer [fetch]]))

(defn payload
  "Builds a request body from alternating wire names and values, dropping the nil ones. The names are
  written out by each resource, because a payload built by case conversion would also rewrite the
  caller's own keys; false and empty lists are kept."
  [& names-and-values]
  (into (array-map)
        (comp (partition-all 2)
              (remove (comp nil? second)))
        names-and-values))

(defn camel-names [names]
  (when names
    (mapv casing/kebab-to-camel names)))

(defn query-params
  "The only two parameters the AI routes that are not paginated accept."
  [{:keys [fields expand]}]
  {:fields (camel-names fields) :expand expand})

(defn read-body
  "Sends the request and returns the answer parsed with its wire keys untouched, as keywords."
  [user method path options]
  (let [content (:content (fetch user method path (assoc options :as :byte-array)))]
    (cheshire/parse-string (String. ^bytes content "UTF-8") true)))

(defn entity
  "Renames the attributes of `wire` to kebab keywords, recursing into all of them except `preserved`."
  [wire preserved]
  (into {}
        (map (fn [[wire-name value]]
               (let [attribute (keyword (casing/camel-to-kebab wire-name))]
                 [attribute (if (contains? preserved attribute) value (casing/cast-keys-to-kebab value))])))
        wire))

(defn- entities [api wire-list]
  (mapv #(entity % (:preserved api)) wire-list))

(defn create-one [api body query user]
  (entity ((:one api) (read-body user :post (:path api) {:payload body :query query}))
          (:preserved api)))

(defn get-one [api id params user]
  (entity ((:one api) (read-body user :get (str (:path api) "/" id) {:query (query-params params)}))
          (:preserved api)))

(defn patch-one
  "An update with nothing to change still has to be a JSON object: the API answers 400 to a missing body."
  [api id body user]
  (entity ((:one api) (read-body user :patch (str (:path api) "/" id) {:payload body :empty-object true}))
          (:preserved api)))

(defn list-all
  ([api user]
   (list-all api {} user))

  ([api params user]
   (entities api ((:many api) (read-body user :get (:path api) {:query (query-params params)})))))

(defn delete-many [api ids user]
  (entities api ((:many api) (read-body user :delete (:path api) {:query {:ids ids}}))))
