(ns starkinfra.issuing-product
  "The IssuingProduct map displays information of registered card products to
  your Workspace. They represent a group of cards that begin with the same
  numbers (id) and offer the same product to end customers.

  ## Attributes (return-only):
    - `:id` [string]: unique card product number (BIN) registered within the card network. ex: \"53810200\"
    - `:network` [string]: card network flag. ex: \"mastercard\"
    - `:funding-type` [string]: type of funding used for payment. ex: \"credit\", \"debit\"
    - `:holder-type` [string]: holder type. ex: \"business\", \"individual\"
    - `:code` [string]: internal code from card flag informing the product. ex: \"MRW\", \"MCO\", \"MWB\", \"MCS\"
    - `:customer-type` [string]: Same as holderType. Kept for backward compatibility
    - `:created` [string]: creation datetime for the IssuingProduct. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-page get-stream]]))

(defn- resource []
  "issuing-product")


(defn query
  "Receive a stream of IssuingProduct maps previously registered in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingProduct maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 IssuingProduct maps previously registered in the Stark Infra API and the cursor to the next page.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingProduct maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
