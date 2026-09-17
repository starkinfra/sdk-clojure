(ns starkinfra.utils.json
  "JSON writing that reproduces CPython's `json.dumps` byte for byte.

  Two callers need that fidelity for different reasons. Signature verification
  retries over `json.dumps(json.loads(content), sort_keys=True)`, so a single
  byte of disagreement would reject a legitimate Stark Infra signature. The
  `response` builders hand their output straight back to the API, and matching
  sdk-python's bytes keeps the two SDKs comparable on the wire."
  (:require [clojure.string :as string]
            [core-clojure.utils.case :refer [kebab-to-camel]])
  (:import (java.io StringWriter)))

(def ^:private escapes
  {\" "\\\"" \\ "\\\\" \backspace "\\b" \formfeed "\\f"
   \newline "\\n" \return "\\r" \tab "\\t"})


(defn- write-char [^StringWriter out c]
  (let [code (int c)
        escape (escapes c)]
    (cond
      escape (.write out ^String escape)
      (<= 0x20 code 0x7e) (.write out code)
      :else (.write out (format "\\u%04x" code)))))

(defn- write-string [^StringWriter out ^String s]
  (.write out "\"")
  (doseq [c s]
    (write-char out c))
  (.write out "\""))

(defn- exponent-suffix [exponent]
  (let [magnitude (Math/abs (long exponent))]
    (str "e"
         (if (neg? (long exponent)) "-" "+")
         (if (< magnitude 10) (str "0" magnitude) (str magnitude)))))

(defn- render-digits
  "Renders the shortest round-trip decimal `digits` of a double whose value is
  0.<digits> * 10^`point`, choosing between fixed and exponential notation the
  way CPython's float repr does."
  [^String digits point]
  (let [point (long point)
        width (count digits)]
    (cond
      (or (<= point -4) (> point 16))
      (str (subs digits 0 1)
           (when (> width 1) (str "." (subs digits 1)))
           (exponent-suffix (dec point)))

      (<= point 0)
      (str "0." (string/join (repeat (- point) \0)) digits)

      (>= point width)
      (str digits (string/join (repeat (- point width) \0)) ".0")

      :else
      (str (subs digits 0 point) "." (subs digits point)))))

(defn- float-repr
  "CPython's `repr` of a double. Java's `Double/toString` has produced the
  shortest round-trip decimal since JDK 19, so only the formatting differs."
  [^double value]
  (cond
    (Double/isNaN value) "NaN"
    (and (Double/isInfinite value) (pos? value)) "Infinity"
    (Double/isInfinite value) "-Infinity"
    :else
    (let [text (Double/toString value)
          negative? (string/starts-with? text "-")
          text (if negative? (subs text 1) text)
          [mantissa exponent] (string/split text #"E")
          exponent (if exponent (Long/parseLong exponent) 0)
          raw (string/replace mantissa "." "")
          leading (count (take-while #(= \0 %) raw))
          digits (string/replace (subs raw leading) #"0+$" "")
          point (- (+ (string/index-of mantissa ".") exponent) leading)]
      (cond
        (and (empty? digits) negative?) "-0.0"
        (empty? digits) "0.0"
        negative? (str "-" (render-digits digits point))
        :else (render-digits digits point)))))

(declare write-value)

(defn- write-entries [^StringWriter out entries sort-keys?]
  (let [keyed (map (fn [[k v]] [(if (keyword? k) (name k) (str k)) v]) entries)
        ordered (if sort-keys? (sort-by first keyed) keyed)]
    (.write out "{")
    (doseq [[index [k v]] (map-indexed vector ordered)]
      (when (pos? (long index))
        (.write out ", "))
      (write-string out k)
      (.write out ": ")
      (write-value out v sort-keys?))
    (.write out "}")))

(defn- write-array [^StringWriter out values sort-keys?]
  (.write out "[")
  (doseq [[index value] (map-indexed vector values)]
    (when (pos? (long index))
      (.write out ", "))
    (write-value out value sort-keys?))
  (.write out "]"))

(defn- write-value [^StringWriter out value sort-keys?]
  (cond
    (nil? value) (.write out "null")
    (true? value) (.write out "true")
    (false? value) (.write out "false")
    (string? value) (write-string out value)
    (keyword? value) (write-string out (name value))
    (integer? value) (.write out (str value))
    (decimal? value) (.write out ^String (float-repr (double value)))
    (float? value) (.write out ^String (float-repr (double value)))
    (map? value) (write-entries out (seq value) sort-keys?)
    (sequential? value) (write-array out value sort-keys?)
    :else (throw (ex-info "Value is not JSON serializable" {:value value}))))

(defn dumps
  "Serializes `value` exactly as CPython's `json.dumps` would: separators
  \", \" and \": \", non-ASCII escaped as \\uXXXX, floats as python's repr.
  Keys are emitted in seq order unless `sort-keys?` is true."
  ([value]
   (dumps value false))
  ([value sort-keys?]
   (let [out (StringWriter.)]
     (write-value out value sort-keys?)
     (.toString out))))

(defn api-json
  "Casts a kebab-keyed Clojure value to the API's wire shape, as core-python's
  `api_json` does: nil members dropped, map keys camelCased. Nils inside lists
  are kept, which is also what python does."
  [value]
  (cond
    (map? value) (reduce (fn [acc [k v]]
                           (if (nil? v)
                             acc
                             (assoc acc (name (kebab-to-camel k)) (api-json v))))
                         (array-map)
                         value)
    (sequential? value) (mapv api-json value)
    :else value))
