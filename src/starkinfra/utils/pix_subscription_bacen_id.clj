(ns starkinfra.utils.pix-subscription-bacen-id
  "Used to generate bacen ids for Pix subscriptions."
  (:require [starkinfra.utils.bacen-id :as bacen-id]))


(defn create
  "Generates a random Pix subscription bacen id based on your bank code (ISPB).

  ## Parameters (required):
    - `bank-code` [string]: your bank code (ISPB). ex: \"20018183\"
    - `prefix` [string]: subscription prefix. ex: \"RR\"

  ## Return:
    - random bacen id based on your bank code. ex: \"RR2001818320220117u34srod19le\""
  [bank-code prefix]
  (str prefix (bacen-id/create bank-code "yyyyMMdd")))
