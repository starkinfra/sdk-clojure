(ns starkinfra.utils.end-to-end-id
  "Used to generate end-to-end ids for Pix transactions."
  (:require [starkinfra.utils.bacen-id :as bacen-id]))


(defn create
  "Generates a random end-to-end id based on your bank code (ISPB).

  ## Parameters (required):
    - `bank-code` [string]: your bank code (ISPB). ex: \"20018183\"

  ## Return:
    - random end-to-end id based on your bank code. ex: \"E20018183202201172211u34srod19le\""
  [bank-code]
  (str "E" (bacen-id/create bank-code)))
