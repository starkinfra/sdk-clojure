(ns starkinfra.credit-note.invoice
  "Invoice issued after the contract is signed, to be paid by the credit receiver.

  CreditNote.Invoice has no verbs of its own: sdk-python's creditnote/invoice
  module exposes only the Invoice class and the hydration helpers CreditNote
  uses when reading its `:invoices` field back from the API. A CreditNote is
  created with plain maps shaped like this namespace's docstring in its
  `:invoices` list, and reads them back the same way, so there is nothing
  here to call.

  ## Parameters (required):
    - `:amount` [integer]: Invoice value in cents. Minimum = 1 (any value will be accepted). ex: 1234 (= R$ 12.34)

  ## Parameters (optional):
    - `:due` [string, default now + 2 days]: Invoice due date in UTC ISO format. ex: \"2020-10-28T17:59:26.249976+00:00\" for immediate invoices and \"2020-10-28\" for scheduled invoices
    - `:expiration` [integer, default 5097600 (59 days)]: time interval in seconds between due date and expiration date. ex: 123456789
    - `:tags` [list of strings, default []]: list of strings for tagging
    - `:descriptions` [list of maps, default []]: list of Description maps, each with:
      - `:key` [string]: Description for the value. ex: \"Taxes\"
      - `:value` [string, default \"\"]: amount related to the described key. ex: \"R$100,00\"
    - `:fine` [float, default 2.0]: Invoice fine for overdue payment in %, shared by every invoice in the CreditNote. ex: 2.5
    - `:interest` [float, default 1.0]: Invoice monthly interest for overdue payment in %, shared by every invoice in the CreditNote. ex: 1.5

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when Invoice is created. ex: \"5656565656565656\"
    - `:name` [string]: payer name. ex: \"Iron Bank S.A.\"
    - `:tax-id` [string]: payer tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
    - `:pdf` [string]: public Invoice PDF URL. ex: \"https://invoice.starkbank.com/pdf/d454fa4e524441c1b0c1a729457ed9d8\"
    - `:link` [string]: public Invoice webpage URL. ex: \"https://my-workspace.sandbox.starkbank.com/invoicelink/d454fa4e524441c1b0c1a729457ed9d8\"
    - `:nominal-amount` [integer]: Invoice emission value in cents (will change if invoice is updated, but not if it's paid). ex: 400000
    - `:fine-amount` [integer]: Invoice fine value calculated over nominal-amount. ex: 20000
    - `:interest-amount` [integer]: Invoice interest value calculated over nominal-amount. ex: 10000
    - `:discount-amount` [integer]: Invoice discount value calculated over nominal-amount. ex: 3000
    - `:discounts` [list of maps]: list of Discount maps, each with:
      - `:percentage` [float]: percentage of discount applied until specified due date
      - `:due` [string]: due datetime for the discount
    - `:brcode` [string]: BR Code for the Invoice payment. ex: \"00020101021226800014br.gov.bcb.pix2558invoice.starkbank.com/f5333103-3279-4db2-8389-5efe335ba93d5204000053039865802BR5913Arya Stark6009Sao Paulo6220051656565656565656566304A9A0\"
    - `:status` [string]: current Invoice status. ex: \"registered\" or \"paid\"
    - `:fee` [integer]: fee charged by this Invoice. ex: 200 (= R$ 2.00)
    - `:transaction-ids` [list of strings]: ledger transaction ids linked to this Invoice (if there are more than one, all but the first are reversals or failed reversal chargebacks). ex: [\"19827356981273\"]
    - `:created` [string]: creation datetime for the Invoice. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the Invoice. ex: \"2020-03-10T10:30:00.000000+00:00\"")
