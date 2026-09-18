(ns starkinfra.pix-user
  "Pix Users are used to get fraud statistics of a user.

  ## Parameters (required):
    - `:id` [string]: user tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"

  ## Attributes (return-only):
    - `:statistics` [list of maps, default []]: list of PixUser.Statistics maps. Each map has:
      - `:value` [integer]: aggregated value of the statistic. ex: 3
      - `:type` [string]: type of the statistic. ex: \"infractions\"
      - `:source` [string]: source of the statistic. ex: \"keyManagement\"
      - `:after` [string]: start datetime considered for the statistic aggregation. ex: \"2020-04-23T23:00:00.000000+00:00\"
      - `:updated` [string]: latest update datetime for the statistic. ex: \"2020-04-23T23:00:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id]]))

(defn- resource []
  "pix-user")


(defn get
  "Receive a single PixUser map information by passing its taxId.

  ## Parameters (required):
    - `id` [string]: user tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"

  ## Options:
    - `:key-id` [string, default nil]: marked PixKey id. ex: \"+5511989898989\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixUser map that corresponds to the given id"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id params]
   (get-id @credentials (resource) id params))

  ([id params user]
   (get-id user (resource) id params)))
