(ns starkinfra.pix-domain
  "The PixDomain object displays the domain name and the QR Code domain certificate of Pix participants.
  All certificates must be registered with the Central Bank.

  ## Attributes (return-only):
    - `:certificates` [list of maps]: certificate information of the Pix participant.
      - `:content` [string]: certificate of the Pix participant in PEM format.
    - `:name` [string]: current active domain (URL) of the Pix participant."
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-stream]]))

(defn- resource []
  "pix-domain")


(defn query
  "Receive a stream of PixDomain maps.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of PixDomain maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([user]
   (get-stream user (resource) {})))
