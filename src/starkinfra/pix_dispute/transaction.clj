(ns starkinfra.pix-dispute.transaction
  "Transaction map related to the PixDispute.

  PixDispute.Transaction has no verbs of its own: sdk-python's
  pixdispute/transaction module exposes only the Transaction class and the
  hydration helper PixDispute uses when reading its `:transactions` field
  back from the API. Every element of that list is a plain map shaped like
  this namespace's docstring, so there is nothing here to call.

  ## Attributes (return-only):
    - `:end-to-end-id` [string]: Central Bank's unique transaction id. ex: \"E79457883202101262140HHX553UPqeq\"
    - `:amount` [integer]: refundable amount. ex: 11234 (= R$ 112.34)
    - `:nominal-amount` [integer]: transaction amount. ex: 11234 (= R$ 112.34)
    - `:receiver-type` [string]: receiver person type. Options: \"individual\", \"business\"
    - `:receiver-tax-id-created` [string]: receiver's taxId creation date. For business type only.
    - `:receiver-account-created` [string]: receiver's account creation date.
    - `:receiver-bank-code` [string]: receiver's bank code. ex: \"20018183\"
    - `:receiver-id` [string]: identifier of accountholder in the graph.
    - `:sender-type` [string]: sender person type. Options: \"individual\", \"business\"
    - `:sender-tax-id-created` [string]: sender's taxId creation date. For business type only.
    - `:sender-account-created` [string]: sender's account creation date.
    - `:sender-bank-code` [string]: sender's bank code. ex: \"20018183\"
    - `:sender-id` [string]: identifier of accountholder in the graph.
    - `:settled` [string]: settled datetime of the transaction. ex: \"2020-03-10T10:30:00.000000+00:00\"")
