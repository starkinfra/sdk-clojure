(ns starkinfra.credit-note
  "CreditNotes are used to generate CCB contracts between you and your customers.
  When you initialize a CreditNote, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the list of created maps.

  ## Parameters (required):
    - `:template-id` [string]: ID of the contract template on which the CreditNote will be based. ex: \"0123456789101112\"
    - `:name` [string]: credit receiver's full name. ex: \"Edward Stark\"
    - `:tax-id` [string]: credit receiver's tax ID (CPF or CNPJ). ex: \"20.018.183/0001-80\"
    - `:scheduled` [string]: date of transfer execution. ex: \"2020-03-10\"
    - `:invoices` [list of maps]: list of Invoice maps to be created and sent to the credit receiver. ex: [{:amount 100}]
      - `:amount` [integer]: Invoice value in cents. Minimum = 1 (any value will be accepted). ex: 1234 (= R$ 12.34)
      - `:due` [string, default now + 2 days]: Invoice due date in UTC ISO format. ex: \"2020-10-28T17:59:26.249976+00:00\" for immediate invoices and \"2020-10-28\" for scheduled invoices
      - `:expiration` [integer, default 5097600 (59 days)]: time interval in seconds between due date and expiration date. ex 123456789
      - `:tags` [list of strings, default []]: list of strings for tagging
      - `:descriptions` [list of maps, default []]: list of Description maps
        - `:key` [string]: description for the value. ex: \"Taxes\"
        - `:value` [string, default \"\"]: amount related to the described key. ex: \"R$100,00\"
      - `:fine` [float, default 2.0]: Invoice fine for overdue payment in %, shared by every invoice in the CreditNote. ex: 2.5
      - `:interest` [float, default 1.0]: Invoice monthly interest for overdue payment in %, shared by every invoice in the CreditNote. ex: 1.5
      - `:id` [string]: unique id returned when Invoice is created. ex: \"5656565656565656\"
      - `:name` [string]: payer name. ex: \"Iron Bank S.A.\"
      - `:tax-id` [string]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:pdf` [string]: public Invoice PDF URL. ex: \"https://invoice.starkbank.com/pdf/d454fa4e524441c1b0c1a729457ed9d8\"
      - `:link` [string]: public Invoice webpage URL. ex: \"https://my-workspace.sandbox.starkbank.com/invoicelink/d454fa4e524441c1b0c1a729457ed9d8\"
      - `:nominal-amount` [integer]: Invoice emission value in cents (will change if invoice is updated, but not if it's paid). ex: 400000
      - `:fine-amount` [integer]: Invoice fine value calculated over nominal-amount. ex: 20000
      - `:interest-amount` [integer]: Invoice interest value calculated over nominal-amount. ex: 10000
      - `:discount-amount` [integer]: Invoice discount value calculated over nominal-amount. ex: 3000
      - `:discounts` [list of maps]: list of Discount maps. ex: [{:percentage 10 :due \"2020-03-10\"}]
        - `:percentage` [float]: percentage of discount applied until specified due date
        - `:due` [string]: due datetime for the discount
      - `:brcode` [string]: BR Code for the Invoice payment. ex: \"00020101021226800014br.gov.bcb.pix2558invoice.starkbank.com/f5333103-3279-4db2-8389-5efe335ba93d5204000053039865802BR5913Arya Stark6009Sao Paulo6220051656565656565656566304A9A0\"
      - `:status` [string]: current Invoice status. ex: \"registered\" or \"paid\"
      - `:fee` [integer]: fee charged by this Invoice. ex: 200 (= R$ 2.00)
      - `:transaction-ids` [list of strings]: ledger transaction ids linked to this Invoice (if there are more than one, all but the first are reversals or failed reversal chargebacks). ex: [\"19827356981273\"]
      - `:created` [string]: creation datetime for the Invoice. ex: \"2020-03-10T10:30:00.000000+00:00\"
      - `:updated` [string]: latest update datetime for the Invoice. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:payment` [map]: payment entity to be created and sent to the credit receiver. ex: {:bank-code \"00000000\" :branch-code \"1234\" :account-number \"129340-1\" :name \"Jamie Lannister\" :tax-id \"012.345.678-90\"}
      - `:name` [string]: receiver full name. ex: \"Anthony Edward Stark\"
      - `:tax-id` [string]: receiver tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
      - `:bank-code` [string]: code of the receiver bank institution in Brazil. ex: \"20018183\" or \"341\"
      - `:branch-code` [string]: receiver bank account branch. Use '-' in case there is a verifier digit. ex: \"1357-9\"
      - `:account-number` [string]: receiver bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
      - `:account-type` [string, default \"checking\"]: receiver bank account type. This parameter only has effect on Pix Transfers. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
      - `:tags` [list of strings, default []]: list of strings for reference when searching for transfers. ex: [\"employees\", \"monthly\"]
      - `:id` [string]: unique id returned when the transfer is created. ex: \"5656565656565656\"
      - `:amount` [integer]: amount in cents to be transferred. ex: 1234 (= R$ 12.34)
      - `:external-id` [string]: url safe string that must be unique among all your transfers. Duplicated external ids will cause failures. By default, this parameter will block any transfer that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
      - `:scheduled` [string]: date or datetime when the transfer will be processed. May be pushed to next business day if necessary. ex: \"2020-03-10T10:30:00.000000+00:00\"
      - `:description` [string]: optional description to override default description to be shown in the bank statement. ex: \"Payment for service #1234\"
      - `:fee` [integer]: fee charged when the Transfer is processed. ex: 200 (= R$ 2.00)
      - `:status` [string]: current transfer status. ex: \"success\" or \"failed\"
      - `:transaction-ids` [list of strings]: ledger Transaction IDs linked to this Transfer (if there are two, the second is the chargeback). ex: [\"19827356981273\"]
      - `:created` [string]: creation datetime for the transfer. ex: \"2020-03-10T10:30:00.000000+00:00\"
      - `:updated` [string]: latest update datetime for the transfer. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:signers` [list of maps]: signer's name, contact and delivery method for the signature request. ex: [{:name \"Tony Stark\" :contact \"tony@starkindustries.com\" :method \"link\"}]
      - `:name` [string]: signer's name. ex: \"Tony Stark\"
      - `:contact` [string]: signer's contact information. ex: \"tony@starkindustries.com\"
      - `:method` [string]: delivery method for the contract. Options: \"link\" (signing link sent to the contact), \"token\" (signing token sent to the contact), \"server\" and \"organization\" (automatic signatures, no contact delivery).
      - `:id` [string]: unique id returned when the CreditSigner is created. ex: \"5656565656565656\"
    - `:external-id` [string]: a string that must be unique among all your CreditNotes, used to avoid resource duplication. ex: \"my-internal-id-123456\"
    - `:street-line-1` [string]: credit receiver main address. ex: \"Av. Paulista, 200\"
    - `:street-line-2` [string]: credit receiver address complement. ex: \"Apto. 123\"
    - `:district` [string]: credit receiver address district / neighbourhood. ex: \"Bela Vista\"
    - `:city` [string]: credit receiver address city. ex: \"Rio de Janeiro\"
    - `:state-code` [string]: credit receiver address state. ex: \"GO\"
    - `:zip-code` [string]: credit receiver address zip code. ex: \"01311-200\"

  ## Parameters (conditionally-required):
    - `:payment-type` [string]: payment type, inferred from the payment parameter if it is not a map. ex: \"transfer\"
    - `:nominal-amount` [integer]: CreditNote value in cents, before taxes. Required when amount is not sent; when provided instead of amount, the disbursed amount, tax-amount (IOF) and interest rates are computed from the invoice schedule. ex: 1234 (= R$ 12.34)
    - `:amount` [integer]: net amount in cents to be disbursed to the credit receiver. Required when nominal-amount is not sent; when provided instead of nominal-amount, the nominal amount, tax-amount and interest rates are computed from the invoice schedule.

  ## Parameters (optional):
    - `:rebate-amount` [integer, default 0]: credit analysis fee deducted from lent amount. ex: 11234 (= R$ 112.34)
    - `:tags` [list of strings, default []]: list of strings for reference when searching for CreditNotes. ex: [\"employees\", \"monthly\"]
    - `:expiration` [integer, default 604800 (7 days)]: time interval in seconds between scheduled date and expiration date. ex 123456789
    - `:rules` [list of maps, default []]: list of CreditNote behavior rules. ex: [{:key \"invoiceCreationMode\" :value \"scheduled\"}]
      - `:key` [string]: rule to be customized, describes what CreditNote behavior will be altered. ex: \"invoiceCreationMode\"
      - `:value` [string]: value of the rule. ex: \"scheduled\", \"instant\", \"never\"

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the CreditNote is created. ex: \"5656565656565656\"
    - `:document-id` [string]: ID of the signed document to execute this CreditNote. ex: \"4545454545454545\"
    - `:status` [string]: current status of the CreditNote. ex: \"canceled\", \"created\", \"expired\", \"failed\", \"processing\", \"signed\", \"success\"
    - `:transaction-ids` [list of strings]: ledger transaction ids linked to this CreditNote. ex: [\"19827356981273\"]
    - `:workspace-id` [string]: ID of the Workspace that generated this CreditNote. ex: \"4545454545454545\"
    - `:debtor-workspace-id` [string]: ID of the debtor's Workspace, when it differs from the Workspace that generated this CreditNote. ex: \"4545454545454545\"
    - `:tax-amount` [integer]: tax amount included in the CreditNote. ex: 100
    - `:nominal-interest` [float]: yearly nominal interest rate of the CreditNote, in percentage. ex: 11.5
    - `:interest` [float]: yearly effective interest rate of the CreditNote, in percentage. ex: 12.5
    - `:created` [string]: creation datetime for the CreditNote. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the CreditNote. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [delete-id get-content get-id get-page
                                           get-stream post-multi]]))

(defn- resource []
  "credit-note")


(defn create
  "Send a list of CreditNote maps for creation at the Stark Infra API.

  ## Parameters (required):
    - `notes` [list of maps]: list of CreditNote maps to be created in the API. You can send up to 100 CreditNote maps in a single request; each may carry up to 100 Invoice maps and up to 10 CreditSigner maps.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of CreditNote maps with updated attributes"
  ([notes]
   (post-multi @credentials (resource) notes {}))

  ([notes user]
   (post-multi user (resource) notes {})))

(defn get
  "Receive a single CreditNote map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - CreditNote map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of CreditNote maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"canceled\", \"created\", \"expired\", \"failed\", \"processing\", \"signed\", \"success\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of CreditNote maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of up to 100 CreditNote maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. It must be an integer between 1 and 100. ex: 50
    - `:after` [string, default nil]: date filter for maps created after a specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created before a specified date. ex: \"2020-03-10\"
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"canceled\", \"created\", \"expired\", \"failed\", \"processing\", \"signed\", \"success\"]
    - `:tags` [list of strings, default nil]: tags to filter retrieved maps. ex: [\"tony\", \"stark\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of CreditNote maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn cancel
  "Cancel a CreditNote that has not reached a final status yet. Only CreditNotes with status \"created\", \"signed\" or \"processing\" can be canceled — this also cancels the signing document. CreditNotes with status \"success\", \"failed\", \"expired\" or already \"canceled\" are returned unchanged.

  ## Parameters (required):
    - `id` [string]: CreditNote unique id. ex: \"6306109539221504\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled CreditNote map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))

(defn pdf
  "Receive a CCB disbursement pdf file.

  ## Parameters (required):
    - `note-id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - CreditNote pdf file content, as a byte array"
  ([note-id]
   (get-content @credentials (resource) note-id "/pdf" {}))

  ([note-id user]
   (get-content user (resource) note-id "/pdf" {})))

(defn payment
  "Receive a CCB disbursement payment pdf file.

  ## Parameters (required):
    - `note-id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - CreditNote payment pdf file content, as a byte array"
  ([note-id]
   (get-content @credentials (resource) note-id "payment/pdf" {}))

  ([note-id user]
   (get-content user (resource) note-id "payment/pdf" {})))
