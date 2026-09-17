(ns starkinfra.utils.page
  "Walks a resource's `page` function for a fixed number of iterations and
  returns every id it saw, failing if the API ever repeats one.")

(declare collect)

(defn- repeated? [ids]
  (boolean (seq (for [[_ frequency] (frequencies ids) :when (> frequency 1)] frequency))))

(defn- remaining [iterations cursor]
  (if (nil? cursor) 0 (dec iterations)))

(defn- fetch [page-fn iterations ids options]
  (let [page (page-fn options)
        cursor (:cursor page)
        entities (get (first (dissoc page :cursor)) 1)]
    (collect page-fn
             (remaining iterations cursor)
             (concat ids (map :id entities))
             (assoc options :cursor cursor))))

(defn- collect [page-fn iterations ids options]
  (cond
    (> iterations 0) (fetch page-fn iterations ids options)
    (repeated? ids) (throw (ex-info "Repeated IDs" {:ids ids}))
    :else ids))

(defn get-ids
  "Calls `page-fn` up to `iterations` times, following the cursor, and returns
  the ids of every entity retrieved.

  ## Parameters (required):
    - `page-fn` [function]: one-argument function taking the options map, ex: #(starkinfra.pix-request/page %)
    - `iterations` [integer]: how many pages to walk. ex: 2
    - `options` [map]: options forwarded to `page-fn`. ex: {:limit 2}"
  [page-fn iterations options]
  (collect page-fn iterations [] options))
