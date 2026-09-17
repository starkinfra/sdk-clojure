(ns starkinfra.issuing-billing-invoice
  "Check out our API Documentation at https://starkinfra.com/docs/api#issuing-billing-invoice

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when IssuingBillingInvoice is created. ex: \"5656565656565656\"
    - `:name` [string]: payer name. ex: \"Iron Bank S.A.\"
    - `:tax-id` [string]: payer tax ID (CPF or CNPJ). ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:fine` [float]: fine percentage applied when paid after the due date. ex: 2.0
    - `:interest` [float]: monthly interest percentage applied when paid after the due date. ex: 1.0
    - `:status` [string]: current IssuingBillingInvoice status. ex: \"created\", \"paid\", \"overdue\", \"expired\" or \"canceled\"
    - `:amount` [integer]: IssuingBillingInvoice amount in cents, including fine and interest if paid after the due date. ex: 11234 (= R$ 112.34)
    - `:nominal-amount` [integer]: IssuingBillingInvoice nominal amount in cents, without fine or interest. ex: 11234 (= R$ 112.34)
    - `:brcode` [string]: BR Code for the IssuingBillingInvoice payment. ex: \"00020101021226930014br.gov.bcb.pix2571brcode-h.development.starkinfra.com/v2/d7f6546e194d4c64a153e8f79f1c41ac5204000053039865802BR5925Stark Bank S.A. - Institu6009Sao Paulo62070503***63042109\"
    - `:link` [string]: public IssuingBillingInvoice webpage URL. ex: \"https://starkbank-card-issuer.development.starkbank.com/invoicelink/d7f6546e194d4c64a153e8f79f1c41ac\"
    - `:due` [string]: IssuingBillingInvoice due datetime in UTC ISO format. ex: \"2020-10-28T17:59:26.249976+00:00\"
    - `:start` [string]: billing period start datetime in UTC ISO format. ex: \"2020-10-01T00:00:00.249976+00:00\"
    - `:end` [string]: billing period end datetime in UTC ISO format. ex: \"2020-10-28T17:59:26.249976+00:00\"
    - `:created` [string]: creation datetime for the IssuingBillingInvoice. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the IssuingBillingInvoice. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream]]))

(defn- resource []
  "issuing-billing-invoice")


(defn get
  "Receive a single IssuingBillingInvoice map previously created in the Stark Infra API by its id.
  Check out our API Documentation at https://starkinfra.com/docs/api#issuing-billing-invoice

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingBillingInvoice map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingBillingInvoice maps previously created in the Stark Infra API.
  Check out our API Documentation at https://starkinfra.com/docs/api#issuing-billing-invoice

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"overdue\", \"expired\" or \"canceled\"
    - `:id` [string, default nil]: filter for the IssuingBillingInvoice id.
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingBillingInvoice maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingBillingInvoice maps previously created in the Stark Infra API and the cursor to the next page.
  Check out our API Documentation at https://starkinfra.com/docs/api#issuing-billing-invoice

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [string, default nil]: filter for status of retrieved maps. ex: \"created\", \"paid\", \"overdue\", \"expired\" or \"canceled\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingBillingInvoice maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))
