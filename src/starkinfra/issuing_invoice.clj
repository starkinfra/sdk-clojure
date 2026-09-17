(ns starkinfra.issuing-invoice
  "The IssuingInvoice maps created in your Workspace load your Issuing balance
  when paid. When you initialize an IssuingInvoice, the entity will not be
  automatically created in the Stark Infra API. The 'create' function sends
  the map to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:amount` [integer]: IssuingInvoice value in cents. Minimum = 0 (R$0,00). ex: 1234 (= R$ 12.34)

  ## Parameters (optional):
    - `:tax-id` [string, default sub-issuer tax ID]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:name` [string, default sub-issuer name]: payer name. ex: \"Iron Bank S.A.\"
    - `:tags` [list of strings, default []]: list of strings for tagging. ex: [\"travel\", \"food\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingInvoice is created. ex: \"5656565656565656\"
    - `:brcode` [string]: BR Code for the Invoice payment. ex: \"00020101021226930014br.gov.bcb.pix2571brcode-h.development.starkinfra.com/v2/d7f6546e194d4c64a153e8f79f1c41ac5204000053039865802BR5925Stark Bank S.A. - Institu6009Sao Paulo62070503***63042109\"
    - `:due` [string]: Invoice due and expiration date in UTC ISO format. ex: \"2020-10-28T17:59:26.249976+00:00\"
    - `:link` [string]: public Invoice webpage URL. ex: \"https://starkbank-card-issuer.development.starkbank.com/invoicelink/d7f6546e194d4c64a153e8f79f1c41ac\"
    - `:status` [string]: current IssuingInvoice status. ex: \"created\", \"expired\", \"overdue\", \"paid\"
    - `:issuing-transaction-id` [string]: ledger transaction ids linked to this IssuingInvoice. ex: \"issuing-invoice/5656565656565656\"
    - `:updated` [string]: latest update datetime for the IssuingInvoice. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingInvoice. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream post-single]]))

(defn- resource []
  "issuing-invoice")


(defn create
  "Send an IssuingInvoice map for creation at the Stark Infra API.

  ## Parameters (required):
    - `invoice` [map]: IssuingInvoice map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingInvoice map with updated attributes"
  ([invoice]
   (post-single @credentials (resource) invoice {}))

  ([invoice user]
   (post-single user (resource) invoice {})))

(defn get
  "Receive a single IssuingInvoice map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingInvoice map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingInvoice maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"expired\", \"overdue\", \"paid\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingInvoice maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingInvoice maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"created\", \"expired\", \"overdue\", \"paid\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingInvoice maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
