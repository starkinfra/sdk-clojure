(ns starkinfra.utils.date
  "Dates in the near future, for resources that refuse dates in the past."
  (:import (java.time LocalDate ZoneOffset ZonedDateTime)
           (java.time.format DateTimeFormatter)))

(def ^:private date-formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd"))
(def ^:private datetime-formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd'T'HH:mm:ss.SSS"))


(defn future-date
  ([]
   (future-date (inc (rand-int 6))))

  ([days]
   (.format (.plusDays (LocalDate/now ZoneOffset/UTC) days) date-formatter)))

(defn future-datetime
  ([]
   (future-datetime (inc (rand-int 6))))

  ([days]
   (str (.format (.plusDays (ZonedDateTime/now ZoneOffset/UTC) days) datetime-formatter) "+00:00")))
