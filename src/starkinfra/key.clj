(ns starkinfra.key
  "Used to generate API-compatible key pairs."
  (:require [core-clojure.key :as core-key]))


(defn create
  "Generates a secp256k1 ECDSA private/public key pair to be used in the API authentications.

  ## Parameters (optional):
    - `path` [string, default nil]: path to save the keys .pem files. No files will be saved if this parameter isn't provided.

  ## Return:
    - map with `:private-pem` and `:public-pem` strings"
  ([]
   (let [key (core-key/create-key)]
     {:private-pem (:privatePem key)
      :public-pem (:publicPem key)}))

  ([path]
   (let [key (core-key/create-key path)]
     {:private-pem (:privatePem key)
      :public-pem (:publicPem key)})))
