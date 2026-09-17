(ns starkinfra.issuing-token
  "The IssuingToken map displays the information of the tokens created in
  your Workspace.

  ## Attributes (return-only):
    - `:card-id` [string]: card ID which the token is bounded to. ex: \"5656565656565656\"
    - `:wallet-id` [string]: wallet provider which the token is bounded to. ex: \"google\"
    - `:wallet-name` [string]: wallet name. ex: \"GOOGLE\"
    - `:merchant-id` [string]: merchant unique id. ex: \"5656565656565656\"
    - `:wallet-device-score` [float]: device score informed by the digital wallet.
    - `:wallet-account-score` [float]: account score informed by the digital wallet

  ## Attributes (IssuingToken only):
    - `:id` [string]: unique id returned when IssuingToken is created. ex: \"5656565656565656\"
    - `:external-id` [string]: a unique string among all your IssuingTokens, used to avoid resource duplication. ex: \"DSHRMC00002626944b0e3b539d4d459281bdba90c2588791\"
    - `:tags` [list of strings]: list of strings for reference when searching for IssuingToken. ex: [\"employees\", \"monthly\"]
    - `:status` [string]: current IssuingToken status. ex: \"active\", \"blocked\", \"canceled\", \"frozen\" or \"pending\"
    - `:updated` [string]: latest update datetime for the IssuingToken. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:created` [string]: creation datetime for the IssuingToken. ex: \"2020-03-10T10:30:00.000000+00:00\"

  ## Attributes (authorization request only):
    - `:activation-code` [string]: activation code recived through the bank app or sms. ex: \"481632\"
    - `:method-code` [string]: provisioning method. Options: \"app\", \"token\", \"manual\", \"server\" or \"browser\"
    - `:device-type` [string]: device type used for tokenization. ex: \"Phone\"
    - `:device-name` [string]: device name used for tokenization. ex: \"My phone\"
    - `:device-serial-number` [string]: device serial number used for tokenization. ex: \"2F6D63\"
    - `:device-os-name` [string]: device operational system name used for tokenization. ex: \"Android\"
    - `:device-os-version` [string]: device operational system version used for tokenization. ex: \"4.4.4\"
    - `:device-imei` [string]: device imei used for tokenization. ex: \"352099001761481\"
    - `:wallet-instance-id` [string]: unique id refered to the wallet app in the current device. ex: \"71583be4777eb89aaf0345eebeb82594f096615ed17862d0\""
  (:refer-clojure :exclude [get update])
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.parse :refer [parse-and-verify]]
            [starkinfra.utils.rest :refer [delete-id get-id get-page get-stream
                                           patch-id]]))

(defn- resource []
  "issuing-token")


(defn get
  "Receive a single IssuingToken map previously created in the Stark Infra API by its id.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - IssuingToken map with updated attributes"
  ([id]
   (get-id @credentials (resource) id {}))

  ([id user]
   (get-id user (resource) id {})))

(defn query
  "Receive a stream of IssuingToken maps previously created in the Stark Infra API.

  ## Options:
    - `:limit` [integer, default nil]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [string, default nil]: current IssuingToken status. ex: \"active\", \"blocked\", \"canceled\", \"frozen\" or \"pending\"
    - `:card-ids` [list of strings, default nil]: list of card ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"DSHRMC00002626944b0e3b539d4d459281bdba90c2588791\", \"DSHRMC00002626941c531164a0b14c66ad9602ee716f1e85\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of IssuingToken maps with updated attributes"
  ([]
   (get-stream @credentials (resource) {}))

  ([params]
   (get-stream @credentials (resource) params))

  ([params user]
   (get-stream user (resource) params)))

(defn page
  "Receive a list of IssuingToken maps previously created in the Stark Infra API and the cursor to the next page.
  Use this function instead of query if you want to manually page your requests.

  ## Options:
    - `:cursor` [string, default nil]: cursor returned on the previous page function call
    - `:limit` [integer, default 100]: maximum number of maps to be retrieved. Max = 100. ex: 35
    - `:after` [string, default nil]: date filter for maps created only after specified date. ex: \"2020-03-10\"
    - `:before` [string, default nil]: date filter for maps created only before specified date. ex: \"2020-03-10\"
    - `:status` [string, default nil]: current IssuingToken status. ex: \"active\", \"blocked\", \"canceled\", \"frozen\" or \"pending\"
    - `:card-ids` [list of strings, default nil]: list of card ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `:external-ids` [list of strings, default nil]: external IDs. ex: [\"5656565656565656\", \"4545454545454545\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map with `:content` and `:cursor`:
      - `:content`: list of IssuingToken maps with updated attributes
      - `:cursor`: cursor string to retrieve the next page, empty when there is none"
  ([]
   (get-page @credentials (resource) {}))

  ([params]
   (get-page @credentials (resource) params))

  ([params user]
   (get-page user (resource) params)))

(defn update
  "Update an IssuingToken by passing id.

  ## Parameters (required):
    - `id` [string]: IssuingToken id. ex: \"5656565656565656\"

  ## Options:
    - `:status` [string, default nil]: you may block the IssuingToken by passing \"blocked\" or activate by passing \"active\" in the status. ex: \"active\", \"blocked\"
    - `:tags` [list of strings, default nil]: list of strings for tagging. ex: [\"travel\", \"food\"]
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - target IssuingToken with updated attributes"
  ([id params]
   (patch-id @credentials (resource) params id))

  ([id params user]
   (patch-id user (resource) params id)))

(defn cancel
  "Cancel an IssuingToken entity previously created in the Stark Infra API.

  ## Parameters (required):
    - `id` [string]: IssuingToken unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - canceled IssuingToken map"
  ([id]
   (delete-id @credentials (resource) id {}))

  ([id user]
   (delete-id user (resource) id {})))

(defn parse
  "Create a single verified IssuingToken request from a content string.
  Use this method to parse and verify the authenticity of the request received
  at the informed endpoint. Token requests are posted to your registered
  endpoint whenever IssuingTokens are received. If the provided digital
  signature does not check out with the Stark Infra public key, an ex-info
  carrying `:code \"invalidSignature\"` is thrown.

  ## Parameters (required):
    - `content` [string]: response content from request received at user endpoint (not parsed)
    - `signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - parsed IssuingToken map"
  ([content signature]
   (parse-and-verify content signature @credentials nil))

  ([content signature user]
   (parse-and-verify content signature user nil)))

(defn response-authorization
  "Helps you respond IssuingToken authorization requests.
  When a new tokenization is triggered by your user, a POST request will be
  made to your registered URL to get your decision to complete the
  tokenization. The POST request must be answered in the following format,
  within 2 seconds, and with an HTTP status code 200.

  ## Parameters (required):
    - `status` [string]: sub-issuer response to the authorization. ex: \"approved\" or \"denied\"

  ## Parameters (conditionally required):
    - `params` [map]:
      - `:reason` [string, default \"\"]: denial reason. Options: \"other\", \"bruteForce\", \"subIssuerError\", \"lostCard\", \"invalidCard\", \"invalidHolder\", \"expiredCard\", \"canceledCard\", \"blockedCard\", \"invalidExpiration\", \"invalidSecurityCode\", \"missingTokenAuthorizationUrl\", \"maxCardTriesExceeded\", \"maxWalletInstanceTriesExceeded\"
      - `:activation-methods` [list of maps, default nil]: list of maps with \"type\" and \"value\" string pairs
      - `:design-id` [string, default nil]: design unique id. ex: \"5656565656565656\"

  ## Options:
    - `:tags` [list of strings, default nil]: tags to filter retrieved map. ex: [\"tony\", \"stark\"]

  ## Return:
    - dumped JSON string that must be returned to us on the IssuingToken request"
  ([status]
   (response-authorization status nil))

  ([status params]
   (json/dumps (json/api-json
                {:authorization (array-map :status status
                                           :reason (or (:reason params) "")
                                           :activation-methods (:activation-methods params)
                                           :design-id (:design-id params)
                                           :tags (:tags params))}))))

(defn response-activation
  "Helps you respond IssuingToken activation requests.
  When a new token activation is triggered by your user, a POST request will
  be made to your registered URL for you to confirm the activation code you
  informed to them. You may identify this request through the present
  `:activation-code` in the payload. The POST request must be answered in
  the following format, within 2 seconds, and with an HTTP status code 200.

  ## Parameters (required):
    - `status` [string]: sub-issuer response to the activation. ex: \"approved\" or \"denied\"

  ## Options:
    - `:reason` [string, default \"\"]: denial reason. Options: \"other\", \"bruteForce\", \"subIssuerError\", \"lostCard\", \"invalidCard\", \"invalidHolder\", \"expiredCard\", \"canceledCard\", \"blockedCard\", \"invalidExpiration\", \"invalidSecurityCode\", \"missingTokenAuthorizationUrl\", \"maxCardTriesExceeded\", \"maxWalletInstanceTriesExceeded\"
    - `:tags` [list of strings, default nil]: tags to filter retrieved map. ex: [\"tony\", \"stark\"]

  ## Return:
    - dumped JSON string that must be returned to us on the IssuingToken request"
  ([status]
   (response-activation status nil))

  ([status params]
   (json/dumps (json/api-json
                {:authorization (array-map :status status
                                           :reason (or (:reason params) "")
                                           :tags (:tags params))}))))
