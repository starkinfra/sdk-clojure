(ns starkinfra.brcode-preview
  "The BrcodePreview map is used to preview information from a BR Code before paying it.
  When you initialize a BrcodePreview, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the maps
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:id` [string]: BR Code from a Pix payment. This is also de information directly encoded in a QR Code. ex: \"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"
    - `:payer-id` [string]: tax id (CPF/CNPJ) of the individual or business requesting the PixKey information. This id is used by the Central Bank to limit request rates. ex: \"20.018.183/0001-80\"

  ## Parameters (optional):
    - `:end-to-end-id` [string]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
    - `:scheduled` [string, default nil]: date the payment is scheduled to be processed; affects the preview values for due dynamic QR codes. ex: \"2020-03-10\"

  ## Attributes (return-only):
    - `:account-number` [string]: Payment receiver account number. ex: \"1234567\"
    - `:account-type` [string]: Payment receiver account type. ex: \"checking\"
    - `:amount` [integer]: Value in cents that this payment is expecting to receive. If 0, any value is accepted. ex: 123 (= R$1,23)
    - `:amount-type` [string]: amount type of the Brcode. If the amount type is \"custom\" the Brcode's amount can be changed by the sender at the moment of payment. Options: \"fixed\" or \"custom\"
    - `:bank-code` [string]: Payment receiver bank code. ex: \"20018183\"
    - `:branch-code` [string]: Payment receiver branch code. ex: \"0001\"
    - `:cash-amount` [integer]: Amount to be withdrawn from the cashier in cents. ex: 1000 (= R$ 10.00)
    - `:cashier-bank-code` [string]: Cashier's bank code. ex: \"20018183\"
    - `:cashier-type` [string]: Cashier's type. Options: \"merchant\", \"participant\" and \"other\"
    - `:data` [list of maps]: additional data of the dynamic QR code, in key/value pairs. ex: [{:key \"additional-info\" :value \"order #12345\"}]
    - `:discount-amount` [integer]: Discount value calculated over nominal_amount. ex: 3000
    - `:due` [string]: BR Code due date. ex: \"2020-03-10\"
    - `:expired` [string]: date and time after which the dynamic QR code is considered expired. ex: \"2022-02-01\"
    - `:fine-amount` [integer]: Fine value calculated over nominal_amount. ex: 20000
    - `:interest-amount` [integer]: Interest value calculated over nominal_amount. ex: 10000
    - `:jws` [string]: JWS of the dynamic QR code. Returned only when \"jws\" is passed in the expand query parameter. ex: \"eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9...\"
    - `:key-id` [string]: Receiver's PixKey id. ex: \"+5511989898989\"
    - `:name` [string]: Payment receiver name. ex: \"Tony Stark\"
    - `:nominal-amount` [integer]: Brcode emission amount, without fines, fees and discounts. ex: 1234 (= R$ 12.34)
    - `:reconciliation-id` [string]: Reconciliation ID linked to this payment. If the brcode is dynamic, the reconciliation_id will have from 26 to 35 alphanumeric characters, ex: \"cd65c78aeb6543eaaa0170f68bd741ee\". If the brcode is static, the reconciliation_id will have up to 25 alphanumeric characters \"ah27s53agj6493hjds6836v49\"
    - `:reduction-amount` [integer]: Reduction value to discount from nominal_amount. ex: 1000
    - `:status` [string]: Payment status. ex: \"created\", \"overdue\", \"paid\", \"voided\", \"canceled\" or \"expired\"
    - `:subscription` [map]: BR code subscription information. A plain map (never a resource of its own), with keys:
      - `:amount` [integer]: amount to be charged in cents. ex: 1000 = R$ 10.00
      - `:amount-min-limit` [integer]: minimum amount limit for the subscription. ex: 500 = R$ 5.00
      - `:bacen-id` [string]: BACEN (Brazilian Central Bank) identifier.
      - `:created` [string]: creation datetime for the subscription. ex: \"2020-03-10\"
      - `:description` [string]: description of the subscription.
      - `:installment-end` [string]: end datetime for the installments. ex: \"2020-03-10\"
      - `:installment-start` [string]: start datetime for the installments. ex: \"2020-03-10\"
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
      - `:updated` [string]: last update datetime for the subscription. ex: \"2020-03-10\"
    - `:tax-id` [string]: Payment receiver tax ID. ex: \"012.345.678-90\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [post-multi]]))

(defn- resource []
  "brcode-preview")


(defn create
  "Retrieve BrcodePreviews.
  Process BR Codes before paying them.

  ## Parameters (required):
    - `previews` [list of maps]: List of BrcodePreview maps to preview. You can send up to 100 BrcodePreview maps in a single request. ex: [{:id \"00020126580014br.gov.bcb.pix0136a629532e-7693-4846-852d-1bbff817b5a8520400005303986540510.005802BR5908T'Challa6009Sao Paulo62090505123456304B14A\"}]

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of BrcodePreview maps with updated attributes"
  ([previews]
   (post-multi @credentials (resource) previews {}))

  ([previews user]
   (post-multi user (resource) previews {})))
