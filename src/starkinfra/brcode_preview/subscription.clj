(ns starkinfra.brcode-preview.subscription
  "Subscription is a recurring payment that can be used to charge a user periodically.

  BrcodePreview.Subscription has no verbs of its own: sdk-python's subscription
  module exposes only the Subscription class and the hydration helper
  BrcodePreview uses when reading its `:subscription` field back from the API.
  That field is a plain map shaped like this namespace's docstring, so there
  is nothing here to call.

  ## Attributes (return-only):
    - `:amount` [integer]: amount to be charged in cents. ex: 1000 (= R$ 10.00)
    - `:amount-min-limit` [integer]: minimum amount limit for the subscription. ex: 500 (= R$ 5.00)
    - `:bacen-id` [string]: BACEN (Brazilian Central Bank) identifier.
    - `:created` [string]: creation datetime for the subscription. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:description` [string]: description of the subscription.
    - `:installment-end` [string]: end datetime for the installments. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:installment-start` [string]: start datetime for the installments. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:interval` [string]: interval for the recurring charge. ex: \"monthly\"
    - `:pull-retry-limit` [integer]: maximum number of retries for pulling the payment.
    - `:receiver-bank-code` [string]: bank code of the receiver.
    - `:receiver-name` [string]: name of the receiver.
    - `:receiver-tax-id` [string]: tax ID of the receiver.
    - `:reference-code` [string]: reference code for the subscription.
    - `:sender-final-name` [string]: final sender name.
    - `:sender-final-tax-id` [string]: final sender tax ID.
    - `:status` [string]: current status of the subscription.
    - `:type` [string]: type of the subscription.
    - `:updated` [string]: last update datetime for the subscription. ex: \"2020-03-10T10:30:00.000000+00:00\"")
