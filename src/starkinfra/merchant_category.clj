(ns starkinfra.merchant-category
  "MerchantCategory's codes and types are used to define categories filters
  in IssuingRules. A MerchantCategory filter must define exactly one
  parameter between code and type. A type, such as \"food\", \"services\",
  etc., defines an entire group of merchant codes, whereas a code only
  specifies a specific MCC.

  ## Parameters (conditionally required):
    - `:code` [string, default nil]: category's code. ex: \"veterinaryServices\", \"fastFoodRestaurants\"
    - `:type` [string, default nil]: category's type. ex: \"pets\", \"food\"

  ## Attributes (return-only):
    - `:name` [string]: category's name. ex: \"Veterinary services\", \"Fast food restaurants\"
    - `:number` [string]: category's number. ex: \"742\", \"5814\"
    - `:group` [string]: category's group. ex: \"pets\", \"food\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-stream]]))

(defn- resource []
  "merchant-category")


(defn query
  "Receive a stream of MerchantCategory maps previously created in the Stark Infra API.

  ## Options:
    - `:search` [string, default nil]: keyword to search for code, type, name or number
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of MerchantCategory maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))
