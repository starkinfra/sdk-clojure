(ns starkinfra.utils.case
  "Key casing between this SDK's kebab keywords and the API's camelCase.

  WHY this is not `core-clojure.utils.case` (com.starkinfra/starkcore 0.2.0):
  that namespace's `camel-to-kebab` splits only on a lowercase-to-uppercase
  boundary, so the API's `displayName1` arrives as `:displayname1` while every
  docstring in this SDK promises `:display-name-1`, and its `kebab-to-camel`
  returns a keyword where the wire wants a string. core-python's rule, mirrored
  here, puts a boundary before every non-initial uppercase letter *or digit*.
  Drop this namespace once core-clojure adopts it."
  (:require [clojure.string :as string]))

;; core-python's `case.py` pattern is `(?<!^)(?=[A-Z0-9])`; `(?<=.)` is the same
;; "not at the start of the string" guard in a form Java accepts without
;; look-behind length warnings.
(def ^:private boundary #"(?<=.)(?=[A-Z0-9])")


(defn camel-to-kebab
  "`endToEndId` -> `end-to-end-id`, `streetLine1` -> `street-line-1`,
  `boletoV2` -> `boleto-v-2`, exactly as core-python's `camel_to_kebab`."
  [text]
  (string/lower-case (string/replace (name text) boundary "-")))

(defn kebab-to-camel
  "`end-to-end-id` -> `endToEndId`, `street-line-1` -> `streetLine1`. The
  inverse of `camel-to-kebab` for every key this API uses."
  [text]
  (let [[head & tail] (string/split (name text) #"-")]
    (apply str head (map string/capitalize tail))))

(defn- kebab-key [k]
  (if (or (keyword? k) (string? k))
    (keyword (camel-to-kebab k))
    k))

(defn- camel-key [k]
  (if (or (keyword? k) (string? k))
    (kebab-to-camel k)
    k))

(defn cast-keys-to-kebab
  "Recursively rewrites every map key of a parsed API response as a kebab
  keyword. Values, nils included, are untouched."
  [value]
  (cond
    (map? value) (reduce-kv (fn [acc k v] (assoc acc (kebab-key k) (cast-keys-to-kebab v)))
                            {}
                            value)
    (sequential? value) (mapv cast-keys-to-kebab value)
    :else value))

(defn cast-keys-to-camel
  "Recursively rewrites every map key as a camelCase string, keeping nil
  members. `starkinfra.request`'s raw bodies go through this instead of
  `starkinfra.utils.json/api-json`, because sdk-python hands a raw payload to
  `json.dumps` untouched."
  [value]
  (cond
    ;; array-map built in one shot, not assoc'd into: assoc past eight entries
    ;; silently switches to a hash map and the body loses the caller's order.
    (map? value) (apply array-map
                        (mapcat (fn [[k v]] [(camel-key k) (cast-keys-to-camel v)]) value))
    (sequential? value) (mapv cast-keys-to-camel value)
    :else value))
