(ns starkinfra.utils.request
  "The SDK's own HTTP layer, mirroring core-python's `utils/request.py`,
  `utils/url.py` and `utils/api.py` request for request.

  WHY this is not `core-clojure.utils.request/fetch`
  (com.starkinfra/starkcore 0.2.0), which every verb used to go through:

    - its query encoder drops any *falsy* member, so `{:is-delivered false}`
      never reaches the URL and a nil cursor silently re-fetches page one;
    - it prefixes the query string with `/?` instead of `?`;
    - its status `case` has no default branch (the trailing `throw Exception`
      reads as a key/value pair), so a 403, 404 or 429 surfaces as
      `IllegalArgumentException: No matching clause` with no error payload, and
      a connection failure dies inside `(.getData e)` before that;
    - it signs `<type>/<id>`, so an Organization scoped to a Workspace never
      reaches that Workspace;
    - it returns the response body as a String, which corrupts every pdf, csv
      and gzip the API serves.

  Delete this namespace and go back to core-clojure once those are fixed and
  published upstream."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [core-clojure.user.user :refer [validate]]
            [starkinfra.settings :refer [api-version error-lang host sdk-version
                                         timeout]]
            [starkinfra.utils.case :as casing]
            [starkinfra.utils.json :as json])
  (:import (com.starkbank.ellipticcurve Ecdsa PrivateKey)
           (java.time Instant)))

(def ^:private services
  {"infra" "starkinfra" "bank" "starkbank" "sign" "starksign"})

(def ^:private base-urls
  {"production" "https://api." "sandbox" "https://sandbox.api."})

;; python's `quote_plus` leaves alphanumerics and `_.-~` alone and writes a
;; space as `+`; java.net.URLEncoder disagrees on both `~` and `*`.
(def ^:private unreserved
  (set "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_.-~"))


(defn access-id
  "python's `User.access_id()`: a Project signs as `project/{id}`, an
  Organization as `organization/{id}`, or as
  `organization/{id}/workspace/{workspace-id}` while it is scoped to one."
  [{:keys [type id workspace-id]}]
  (if (and (= "organization" type) workspace-id)
    (str "organization/" id "/workspace/" workspace-id)
    (str type "/" id)))

(defn- percent-encode [text]
  (let [out (StringBuilder.)]
    (doseq [octet (.getBytes ^String text "UTF-8")]
      (let [code (bit-and (long octet) 0xff)
            character (char code)]
        (cond
          (unreserved character) (.append out character)
          (= \space character) (.append out \+)
          :else (.append out (format "%%%02X" code)))))
    (.toString out)))

(defn- value-string
  "python's `url.valueToString`: lists are comma-joined, booleans are `True`
  and `False`, everything else is `str(value)`."
  [value]
  (cond
    (nil? value) "None"
    (true? value) "True"
    (false? value) "False"
    (or (sequential? value) (set? value)) (string/join "," (map value-string value))
    (keyword? value) (name value)
    :else (str value)))

(defn- encode-query
  "python's `url.urlencode`: nil members dropped, keys camelCased, `expand`
  values camelCased, values stringified, then percent-encoded."
  [query]
  (let [params (json/api-json query)
        params (if (contains? params "expand")
                 (assoc params "expand" (mapv casing/kebab-to-camel (get params "expand")))
                 params)]
    (if (empty? params)
      ""
      (str "?" (string/join "&" (map (fn [[k v]]
                                       (str (percent-encode k) "=" (percent-encode (value-string v))))
                                     params))))))

(defn- request-url [user path query]
  (str (base-urls (:environment user))
       (services host)
       ".com/"
       api-version
       "/"
       path
       (encode-query query)))

(defn- user-agent [prefix]
  ;; core-clojure's spelling of the Clojure version, trailing dot included, so
  ;; the two SDKs stay recognizable to the API as the same client.
  (str (if (seq prefix) (str prefix "-") "")
       "Clojure-"
       (:major *clojure-version*) "." (:minor *clojure-version*) "." (:incremental *clojure-version*) "."
       "-SDK-" host "-" sdk-version))

(defn- ^PrivateKey private-key
  "core-clojure stores the pem string for a Project but the parsed key for an
  Organization; both shapes have to sign."
  [user]
  (let [key (:private-key user)]
    (if (string? key)
      (PrivateKey/fromPem ^String key)
      key)))

(defn- authentication-headers [user body]
  (let [id (access-id user)
        access-time (str (.getEpochSecond (Instant/now)))
        message (str id ":" access-time ":" body)]
    {"Access-Id" id
     "Access-Time" access-time
     "Access-Signature" (.toBase64 (Ecdsa/sign ^String message (private-key user)))}))

(defn- request-body [payload]
  (if (or (nil? payload) (and (coll? payload) (empty? payload)))
    ""
    (json/dumps payload)))

(defn- body-string [body]
  (cond
    (nil? body) ""
    (bytes? body) (String. ^bytes body "UTF-8")
    :else (str body)))

(defn- parse-json [text]
  (try
    (let [parsed (cheshire/parse-string text true)]
      (when (coll? parsed)
        (casing/cast-keys-to-kebab parsed)))
    (catch Exception _
      nil)))

(defn- decoded-content [response raw-bytes?]
  (if raw-bytes?
    (:body response)
    (let [text (body-string (:body response))]
      (or (parse-json text) text))))

(defn- fail
  "core-python's error mapping: 500 is always the same message, 400 carries the
  API's own error list, and anything else is unknown."
  [status text]
  (when (= 500 status)
    (throw (ex-info "Houston, we have a problem."
                    {:status 500
                     :errors [{:code "internalServerError"
                               :message "Houston, we have a problem."}]})))
  (when (= 400 status)
    (throw (ex-info text
                    (assoc (or (parse-json text)
                               {:errors [{:code "unknownError" :message text}]})
                           :status 400))))
  (throw (ex-info (str "Unknown exception encountered: " text)
                  {:status status
                   :errors [{:code "unknownError" :message text}]})))

(defn fetch
  "Signs and sends one request to the Stark Infra API.

  ## Parameters (required):
    - `user` [map]: Project or Organization map
    - `method` [keyword]: `:get`, `:post`, `:patch`, `:put` or `:delete`
    - `path` [string]: route with no leading slash. ex: \"pix-request/123\"

  ## Options:
    - `:payload` [any, default nil]: request body, already cast to the wire shape
    - `:query` [map, default nil]: kebab-keyed query members
    - `:prefix` [string, default nil]: User-Agent prefix. ex: \"Joker\"
    - `:throw-error` [boolean, default true]: false returns every status as data
    - `:as` [keyword, default nil]: `:byte-array` for raw content routes

  ## Return:
    - map with `:status` and `:content`, `:content` being the parsed kebab-keyed
      body, the raw string when the body is not JSON, or the `byte[]` when
      `:as :byte-array` was asked for"
  [user method path options]
  (validate (:private-key user) (:environment user))
  (let [{:keys [payload query prefix as]} options
        throw-error (get options :throw-error true)
        body (request-body payload)
        timeout-ms (* 1000 timeout)
        response (try
                   (client/request (cond-> {:method method
                                            :url (request-url user path query)
                                            :body body
                                            :headers (merge {"User-Agent" (user-agent prefix)
                                                             "Accept-Language" @error-lang
                                                             "Content-Type" "application/json"}
                                                            (authentication-headers user body))
                                            :socket-timeout timeout-ms
                                            :connection-timeout timeout-ms
                                            ;; `credit-note/pdf` passes the
                                            ;; sub-resource "/pdf", so the path
                                            ;; carries a double slash that
                                            ;; python's requests sends verbatim.
                                            ;; HttpClient collapses it unless
                                            ;; normalization is off.
                                            :normalize-uri false
                                            :throw-exceptions false}
                                     as (assoc :as as)))
                   (catch Exception exception
                     ;; python reports a transport failure as status 0 carrying
                     ;; the exception's class and message instead of raising it.
                     {:status 0
                      :body (str (.getName (class exception)) ": " (.getMessage exception))}))
        status (:status response)]
    (when (and throw-error (not= 200 status))
      (fail status (body-string (:body response))))
    {:status status :content (decoded-content response (= :byte-array as))}))
