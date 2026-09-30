(ns starkinfra.utils.bacen-id
  "Central Bank transaction id generator shared by end-to-end and return ids."
  (:import (java.time ZoneOffset ZonedDateTime)
           (java.time.format DateTimeFormatter)))

(def ^:private random-source
  (vec (concat (map char (range (int \a) (inc (int \z))))
               (map char (range (int \A) (inc (int \Z))))
               (map char (range (int \0) (inc (int \9)))))))

(def ^:private minute-formatter (DateTimeFormatter/ofPattern "yyyyMMddHHmm"))


(defn- generate
  [bank-code formatter]
  (str bank-code
       (.format (ZonedDateTime/now ZoneOffset/UTC) formatter)
       (apply str (repeatedly 11 #(rand-nth random-source)))))

(defn create
  "Generates a random Central Bank id based on your bank code (ISPB).

  ## Parameters (required):
    - `bank-code` [string]: your bank code (ISPB). ex: \"20018183\"

  ## Parameters (optional):
    - `date-format` [string, default \"yyyyMMddHHmm\"]: java.time pattern of the UTC date part. ex: \"yyyyMMdd\"

  ## Return:
    - bank code, the current UTC date in the given format and 11 random alphanumeric characters"
  ([bank-code]
   (generate bank-code minute-formatter))

  ([bank-code date-format]
   (generate bank-code (DateTimeFormatter/ofPattern date-format))))
