(ns starkinfra.utils.bacen-id
  "Central Bank transaction id generator shared by end-to-end and return ids."
  (:import (java.time ZoneOffset ZonedDateTime)
           (java.time.format DateTimeFormatter)))

(def ^:private random-source
  (vec (concat (map char (range (int \a) (inc (int \z))))
               (map char (range (int \A) (inc (int \Z))))
               (map char (range (int \0) (inc (int \9)))))))

(def ^:private minute-formatter (DateTimeFormatter/ofPattern "yyyyMMddHHmm"))


(defn create
  "Generates a random Central Bank id based on your bank code (ISPB).

  ## Parameters (required):
    - `bank-code` [string]: your bank code (ISPB). ex: \"20018183\"

  ## Return:
    - bank code, the current UTC minute and 11 random alphanumeric characters"
  [bank-code]
  (str bank-code
       (.format (ZonedDateTime/now ZoneOffset/UTC) minute-formatter)
       (apply str (repeatedly 11 #(rand-nth random-source)))))
