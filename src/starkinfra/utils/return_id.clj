(ns starkinfra.utils.return-id
  "Used to generate return ids for Pix reversals."
  (:require [starkinfra.utils.bacen-id :as bacen-id]))


(defn create
  "Generates a random return id based on your bank code (ISPB).

  ## Parameters (required):
    - `bank-code` [string]: your bank code (ISPB). ex: \"20018183\"

  ## Return:
    - random return id based on your bank code. ex: \"D20018183202201172211u34srod19le\""
  [bank-code]
  (str "D" (bacen-id/create bank-code)))
