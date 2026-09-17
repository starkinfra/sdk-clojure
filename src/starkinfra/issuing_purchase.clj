(ns starkinfra.issuing-purchase
  "Displays the IssuingPurchase maps created in your Workspace.

  ## Attributes (return-only):
    - `:holder-name` [string]: card holder name. ex: \"Tony Stark\"
    - `:product-id` [string]: unique card product number (BIN) registered within the card network. ex: \"53810200\"
    - `:card-id` [string]: unique id returned when IssuingCard is created. ex: \"5656565656565656\"
    - `:card-ending` [string]: last 4 digits of the card number. ex: \"1234\"
    - `:purpose` [string]: purchase purpose. ex: \"purchase\"
    - `:installment-count` [integer]: quantity of installments to be confirmed. Minimum = 1. ex: 12
    - `:amount` [integer]: IssuingPurchase value in cents. Minimum = 0. ex: 1234 (= R$ 12.34)
    - `:tax` [integer]: IOF amount taxed for international purchases. ex: 1234 (= R$ 12.34)
    - `:issuer-amount` [integer]: issuer amount. ex: 1234 (= R$ 12.34)
    - `:issuer-currency-code` [string]: issuer currency code. ex: \"USD\"
    - `:issuer-currency-symbol` [string]: issuer currency symbol. ex: \"$\"
    - `:merchant-amount` [integer]: merchant amount. ex: 1234 (= R$ 12.34)
    - `:merchant-currency-code` [string]: merchant currency code. ex: \"USD\"
    - `:merchant-currency-symbol` [string]: merchant currency symbol. ex: \"$\"
    - `:merchant-category-code` [string]: merchant category code. ex: \"fastFoodRestaurants\"
    - `:merchant-category-type` [string]: merchant category type. ex \"food\"
    - `:merchant-category-number` [integer]: MCC number of the merchant category. ex: 5814
    - `:merchant-country-code` [string]: merchant country code. ex: \"USA\"
    - `:acquirer-id` [string]: acquirer ID. ex: \"5656565656565656\"
    - `:merchant-id` [string]: merchant ID. ex: \"5656565656565656\"
    - `:merchant-name` [string]: merchant name. ex: \"Google Cloud Platform\"
    - `:merchant-fee` [integer]: fee charged by the merchant to cover specific costs, such as ATM withdrawal logistics, etc. ex: 200 (= R$ 2.00)
    - `:wallet-id` [string]: virtual wallet ID. ex: \"5656565656565656\"
    - `:method-code` [string]: method code. Options: \"chip\", \"token\", \"server\", \"manual\", \"magstripe\" or \"contactless\"
    - `:score` [float]: internal score calculated for the authenticity of the purchase. nil in case of insufficient data. ex: 7.6
    - `:confirmed` [string]: confirmation datetime. nil until the purchase is confirmed. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:end-to-end-id` [string]: unique id used to identify the transaction through all of its life cycle, even before the purchase is denied or approved and gets its usual id. ex: \"679cd385-642b-49d0-96b7-89491e1249a5\"
    - `:tags` [list of strings]: list of strings for tagging returned by the sub-issuer during the authorization. ex: [\"travel\", \"food\"]

  ## Attributes (IssuingPurchase only):
    - `:id` [string]: unique id returned when IssuingPurchase is created. ex: \"5656565656565656\"
    - `:issuing-transaction-ids` [list of strings]: ledger transaction ids linked to this Purchase
    - `:status` [string]: current IssuingCard status. Options: \"approved\", \"canceled\", \"denied\", \"confirmed\", \"voided\"
    - `:description` [string]: IssuingPurchase description. ex: \"Office Supplies\"
    - `:metadata` [map]: map object used to store additional information about the IssuingPurchase map. ex: {:authorization-id \"OjZAqj\"}
    - `:zip-code` [string]: zip code of the merchant location. ex: \"02101234\"
    - `:updated` [string]: latest update datetime for the IssuingPurchase. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingPurchase. ex: \"2020-03-10T10:30:00.000000+00:00\"

  ## Attributes (authorization request only):
    - `:is-partial-allowed` [boolean]: true if the merchant allows partial purchases. ex: false
    - `:card-tags` [list of strings]: tags of the IssuingCard responsible for this purchase. ex: [\"travel\", \"food\"]
    - `:holder-id` [string]: card holder ID. ex: \"5656565656565656\"
    - `:holder-tags` [list of strings]: tags of the IssuingHolder responsible for this purchase. ex: [\"technology\", \"john snow\"]"
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.parse :refer [parse-and-verify]]
            [starkinfra.utils.rest :refer [get-id get-page get-stream patch-id]]))

(defn- resource []
  "issuing-purchase")


(defn get
  "Receive a single IssuingPurchase map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingPurchase map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingPurchase maps previously created in the Stark Infra API.

  ## Options:
    - `:ids` [list of strings, default nil]: purchase IDs
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Unlimited if nil. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:end-to-end-ids` [list of strings, default nil]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
    - `:holder-ids` [list of strings, default nil]: card holder IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:card-ids` [list of strings, default nil]: card IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"approved\", \"canceled\", \"denied\", \"confirmed\", \"voided\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingPurchase maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingPurchase maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:end-to-end-ids` [list of strings, default nil]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
    - `:holder-ids` [list of strings, default nil]: card holder IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:card-ids` [list of strings, default nil]: card IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:status` [list of strings, default nil]: filter for status of retrieved maps. ex: [\"approved\", \"canceled\", \"denied\", \"confirmed\", \"voided\"]
    - `:ids` [list of strings, default nil]: purchase IDs
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingPurchase maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update an IssuingPurchase by passing id.

  ## Parameters (required):
    - `id` [string]: IssuingPurchase id. ex: \"5656565656565656\"

  ## Options:
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"tony\", \"stark\"]
    - `:description` [string, default nil]: new IssuingPurchase description. Max of 140 characters. ex: \"Office Supplies\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target IssuingPurchase with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn parse
  "Create a single verified IssuingPurchase authorization request from a content string.
  Use this method to parse and verify the authenticity of the authorization request received
  at the informed endpoint. Authorization requests are posted to your registered endpoint
  whenever IssuingPurchases are received. They present IssuingPurchase data that must be
  analyzed and answered with approval or declination. If the provided digital signature does
  not check out with the Stark Infra public key, an ex-info carrying `:code \"invalidSignature\"`
  is thrown. If the authorization request is not answered within 2 seconds or is not answered
  with an HTTP status code 200 the IssuingPurchase will go through the pre-configured
  stand-in validation.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - parsed IssuingPurchase map"
  ([content signature]
   (parse-and-verify content signature @credentials nil))

  ([content signature user]
   (parse-and-verify content signature user nil)))

(defn response
  "Helps you respond IssuingPurchase requests.

  ## Parameters (required):
    - `status` [string]: sub-issuer response to the authorization. ex: \"approved\" or \"denied\"

  ## Parameters (conditionally required):
    - `params` [map]:
      - `:reason` [string]: denial reason. Options: \"other\", \"blocked\", \"lostCard\", \"stolenCard\", \"invalidPin\", \"invalidCard\", \"cardExpired\", \"issuerError\", \"concurrency\", \"standInDenial\", \"subIssuerError\", \"invalidPurpose\", \"invalidZipCode\", \"invalidWalletId\", \"inconsistentCard\", \"settlementFailed\", \"cardRuleMismatch\", \"invalidExpiration\", \"prepaidInstallment\", \"holderRuleMismatch\", \"insufficientBalance\", \"tooManyTransactions\", \"invalidSecurityCode\", \"invalidPaymentMethod\", \"confirmationDeadline\", \"withdrawalAmountLimit\", \"insufficientCardLimit\", \"insufficientHolderLimit\"

  ## Options:
    - `:amount` [integer, default nil]: amount in cents that was authorized. ex: 1234 (= R$ 12.34)
    - `:tags` [list of strings, default nil]: tags to filter retrieved map. ex: [\"tony\", \"stark\"]

  ## Return:
    - dumped JSON string that must be returned to us on the IssuingPurchase request"
  ([status]
   (response status nil))

  ([status params]
   (json/dumps (json/api-json
                {:authorization (array-map :status status
                                           :amount (:amount params)
                                           :reason (:reason params)
                                           :tags (:tags params))}))))
