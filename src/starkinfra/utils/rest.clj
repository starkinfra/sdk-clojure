(ns starkinfra.utils.rest
  "The REST verbs every resource namespace calls, one per core-python
  `utils/rest.py` function. Endpoints and envelope keys come from
  `core-clojure.utils.api`, which agrees with core-python; everything below the
  verb goes through `starkinfra.utils.request/fetch` and not through
  `core-clojure.utils.rest`, whose defects that namespace documents."
  (:require [core-clojure.utils.api :refer [endpoint last-name last-name-plural]]
            [starkinfra.utils.case :as casing]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.request :refer [fetch]]))


(defn- fetch-json [user method path options]
  (:content (fetch user method path options)))

(defn get-page [user path query]
  (let [plural (last-name-plural path)
        json (fetch-json user :get (endpoint path) {:query query})]
    {:cursor (:cursor json)
     :content (plural json)}))

(defn- page-limit
  "python's `min(limit, 100) if limit else limit`: nil and 0 go through
  untouched, anything else is capped at one page."
  [limit]
  (if (or (nil? limit) (zero? limit))
    limit
    (min limit 100)))

(defn- exhausted? [cursor]
  (or (nil? cursor) (= "" cursor)))

(defn- stream
  "core-python's `get_stream` loop, request for request: one page per iteration,
  the remaining limit shrinking by 100 whatever the page returned, and a stop on
  an empty cursor even while a limit remains - core-clojure keeps paging there
  and re-fetches page one, because its encoder drops the nil cursor."
  [user path query limit]
  (lazy-seq
   (let [{:keys [content cursor]} (get-page user path (assoc query :limit (page-limit limit)))
         remaining (when limit (- limit 100))]
     (if (or (exhausted? cursor) (and remaining (<= remaining 0)))
       content
       (concat content (stream user path (assoc query :cursor cursor) remaining))))))

(defn get-stream [user path query]
  (stream user path (dissoc query :limit) (:limit query)))

(defn get-id [user path id query]
  (let [json (fetch-json user :get (str (endpoint path) "/" id) {:query query})]
    (get json (keyword (last-name path)))))

(defn get-content
  "Raw bytes, not a String: a pdf, a csv or a gzip must survive the trip."
  [user path id sub-resource query]
  (:content (fetch user :get (str (endpoint path) "/" id "/" sub-resource)
                   {:query query :as :byte-array})))

(defn get-sub-resource [user path id sub-resource query]
  (fetch-json user :get (str (endpoint path) "/" id "/" (endpoint sub-resource))
              {:query query}))

(defn get-public-key [user]
  (-> (fetch-json user :get "public-key" {:query {:limit 1}})
      :public-keys
      first
      :content))

(defn post-multi [user path payload query]
  (let [plural (last-name-plural path)
        json (fetch-json user :post (endpoint path)
                         {:payload {(name plural) (mapv json/api-json payload)}
                          :query query})]
    (plural json)))

(defn post-single [user path payload query]
  (let [json (fetch-json user :post (endpoint path)
                         {:payload (json/api-json payload) :query query})]
    (get json (keyword (last-name path)))))

(defn patch-id
  "python's `patch_id(resource, id, payload)`; the payload comes before the id
  here because that is the order every resource namespace already calls it in."
  [user path payload id]
  (let [json (fetch-json user :patch (str (endpoint path) "/" id)
                         {:payload (json/api-json payload)})]
    (get json (keyword (last-name path)))))

(defn delete-id
  "DELETE {endpoint}/{id} carrying an optional query map, the shape python's
  `delete_id(**query)` has."
  [user path id query]
  (let [json (fetch-json user :delete (str (endpoint path) "/" id) {:query query})]
    (get json (keyword (last-name path)))))

(defn- raw
  "The `starkinfra.request` verbs: the path is taken verbatim, the body keeps
  its nil members (python hands a raw payload straight to `json.dumps`) and no
  status ever throws, because sdk-python passes `raiseException=False`."
  [user method path payload query prefix throw-error]
  (fetch user method path {:payload (casing/cast-keys-to-camel payload)
                           :query query
                           :prefix prefix
                           :throw-error throw-error}))

(defn get-raw [user path query prefix throw-error]
  (raw user :get path nil query prefix throw-error))

(defn post-raw [user path payload query prefix throw-error]
  (raw user :post path payload query prefix throw-error))

(defn patch-raw [user path payload query prefix throw-error]
  (raw user :patch path payload query prefix throw-error))

(defn put-raw [user path payload query prefix throw-error]
  (raw user :put path payload query prefix throw-error))

(defn delete-raw [user path payload query prefix throw-error]
  (raw user :delete path payload query prefix throw-error))
