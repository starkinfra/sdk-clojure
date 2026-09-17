(ns starkinfra.utils.parse
  "Signature verification and hydration of content posted to your endpoints.

  Mirrors core-python's `starkcore/utils/parse.py`: the signature is checked
  against the raw content first and against python's canonical rendering of the
  same JSON second, because the API signs one or the other depending on the
  route. Both checks are repeated once with a freshly fetched public key, so a
  key rotation costs one extra request instead of a rejected webhook."
  (:require [cheshire.core :as cheshire]
            [core-clojure.utils.case :refer [cast-keys-to-kebab]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.rest :as rest])
  (:import (com.starkbank.ellipticcurve Ecdsa PublicKey Signature)
           (com.starkbank.ellipticcurve.utils ByteString)))

;; Keyed by environment so a process that talks to both sandbox and production
;; never verifies a sandbox payload against the production key.
(def ^:private public-keys (atom {}))


(defn- invalid-signature [message]
  (ex-info message {:code "invalidSignature" :message message}))

(defn- decode-signature [signature]
  (try
    (Signature/fromBase64 (ByteString. (.getBytes ^String signature "UTF-8")))
    (catch Exception _
      (throw (invalid-signature "The provided signature is not valid")))))

(defn- fetch-public-key [user]
  (PublicKey/fromPem (rest/get-public-key user)))

(defn- cached-public-key [user]
  (let [environment (:environment user)
        cached (get @public-keys environment)]
    (or cached
        (let [fetched (fetch-public-key user)]
          (swap! public-keys assoc environment fetched)
          fetched))))

(defn- refreshed-public-key [user]
  (let [fetched (fetch-public-key user)]
    (swap! public-keys assoc (:environment user) fetched)
    fetched))

(defn- canonical
  "`json.dumps(json.loads(content), sort_keys=True)`, the form the API signs on
  the routes that do not sign the raw body. nil when the content is not JSON."
  [content]
  (try
    (json/dumps (cheshire/parse-string content) true)
    (catch Exception _
      nil)))

(defn- signature-valid? [content signature public-key]
  (if (Ecdsa/verify ^String content signature public-key)
    true
    (let [normalized (canonical content)]
      (boolean (and normalized (Ecdsa/verify ^String normalized signature public-key))))))

(defn verify
  "Checks that `content` was signed by Stark Infra and returns it unchanged.
  Throws an ex-info carrying `:code \"invalidSignature\"` otherwise.

  ## Parameters (required):
    - `content` [string]: response content from the request received at your endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"
    - `user` [map]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization

  ## Return:
    - the verified content string"
  [content signature user]
  (let [decoded (decode-signature signature)]
    (cond
      (signature-valid? content decoded (cached-public-key user)) content
      (signature-valid? content decoded (refreshed-public-key user)) content
      :else (throw (invalid-signature
                    "The provided signature and content do not match the Stark Infra public key")))))

(defn parse-and-verify
  "Verifies `content` and hydrates it as a kebab-keyed map.

  ## Parameters (required):
    - `content` [string]: response content from the request received at your endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"
    - `user` [map]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization
    - `key` [string or nil]: envelope key to unwrap after parsing. `starkinfra.event/parse`
      passes \"event\"; every other resource's parse passes nil, because the API posts
      those payloads unwrapped.

  ## Return:
    - map with kebab-case keys"
  [content signature user key]
  (let [verified (verify content signature user)
        parsed (cheshire/parse-string verified true)]
    (cast-keys-to-kebab (if key (get parsed (keyword key)) parsed))))
