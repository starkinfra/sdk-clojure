(ns starkinfra.pix-request
    "PixRequests are used to receive or send instant payments to accounts
    hosted in any Pix participant.
    When you initialize a PixRequest, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the list of created objects.
    
    ## Parameters (required):
        - `:amount` [integer]: amount in cents to be transferred. ex: 11234 (= R$ 112.34)
        - `:external-id` [string]: string that must be unique among all your PixRequests. Duplicated external IDs will cause failures. By default, this parameter will block any PixRequests that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
        - `:sender-name` [string]: sender's full name. ex: \"Edward Stark\"
        - `:sender-tax-id` [string]: sender's tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
        - `:sender-branch-code` [string]: sender's bank account branch code. Use '-' in case there is a verifier digit. ex: \"1357-9\"
        - `:sender-account-number` [string]: sender's bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
        - `:sender-account-type` [string, default \"checking\"]: sender's bank account type. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
        - `:receiver-name` [string]: receiver's full name. ex: \"Edward Stark\"
        - `:receiver-tax-id` [string]: receiver's tax ID (CPF or CNPJ) with or without formatting. ex: \"01234567890\" or \"20.018.183/0001-80\"
        - `:receiver-bank-code` [string]: receiver's bank institution code in Brazil. ex: \"20018183\"
        - `:receiver-account-number` [string]: receiver's bank account number. Use '-' before the verifier digit. ex: \"876543-2\"
        - `:receiver-branch-code` [string]: receiver's bank account branch code. Use '-' in case there is a verifier digit. ex: \"1357-9\"
        - `:receiver-account-type` [string]: receiver's bank account type. ex: \"checking\", \"savings\", \"salary\" or \"payment\"
        - `:end-to-end-id` [string]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
    
    ## Parameters (optional):
        - `:receiver-key-id` [string, default None]: receiver's dict key. ex: \"20.018.183/0001-80\"
        - `:description` [string, default None]: optional description to override default description to be shown in the bank statement. ex: \"Payment for service #1234\"
        - `:reconciliation-id` [string, default None]: Reconciliation ID linked to this payment. ex: \"b77f5236-7ab9-4487-9f95-66ee6eaf1781\"
        - `:initiator-tax-id` [string, default None]: Payment initiator's tax id (CPF/CNPJ). ex: \"01234567890\" or \"20.018.183/0001-80\"
        - `:cash-amount` [integer, default None]: Amount to be withdrawal from the cashier in cents. ex: 1000 (= R$ 10.00)
        - `:cashier-bank-code` [string, default None]: Cashier's bank code. ex: \"00000000\"
        - `:cashier-type` [string, default None]: Cashier's type. ex: [merchant, other, participant]
        - `:tags` [list of strings, default None]: list of strings for reference when searching for PixRequests. ex: [\"employees\", \"monthly\"]
        - `:method` [string, default None]: execution  method for thr creation of the PIX. ex: \"manual\", \"payerQrcode\", \"dynamicQrcode\".
    
    ## Attributes (return-only):
        - `:id` [string]: unique id returned when the PixRequest is created. ex: \"5656565656565656\"
        - `:fee` [integer]: fee charged when PixRequest is paid. ex: 200 (= R$ 2.00)
        - `:status` [string]: current PixRequest status. ex: \"created\", \"processing\", \"success\", \"failed\"
        - `:flow` [string]: direction of money flow. ex: \"in\" or \"out\"
        - `:sender-bank-code` [string]: sender's bank institution code in Brazil. ex: \"20018183\"
        - `:created` [datetime.datetime]: creation datetime for the PixRequest. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:updated` [datetime.datetime]: latest update datetime for the PixRequest. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set update])
    (:import [com.starkinfra PixRequest])
    (:use [starkinfra.user]
            [clojure.walk]))

(defn- clojure-to-java
    ([clojure-map]
        (let [{
            amount "amount"
            external-id "external-id"
            sender-name "sender-name"
            sender-tax-id "sender-tax-id"
            sender-branch-code "sender-branch-code"
            sender-account-number "sender-account-number"
            sender-account-type "sender-account-type"
            receiver-name "receiver-name"
            receiver-tax-id "receiver-tax-id"
            receiver-bank-code "receiver-bank-code"
            receiver-account-number "receiver-account-number"
            receiver-branch-code "receiver-branch-code"
            receiver-account-type "receiver-account-type"
            end-to-end-id "end-to-end-id"
            receiver-key-id "receiver-key-id"
            description "description"
            reconciliation-id "reconciliation-id"
            initiator-tax-id "initiator-tax-id"
            cash-amount "cash-amount"
            cashier-bank-code "cashier-bank-code"
            cashier-type "cashier-type"
            tags "tags"
            method "method"
        } (stringify-keys clojure-map)]
            
            (defn- apply-java-hashmap [x] (java.util.HashMap x))
            
            (PixRequest. (java.util.HashMap.
                {
                    "amount" amount
                    "externalId" external-id
                    "senderName" sender-name
                    "senderTaxId" sender-tax-id
                    "senderBranchCode" sender-branch-code
                    "senderAccountNumber" sender-account-number
                    "senderAccountType" sender-account-type
                    "receiverName" receiver-name
                    "receiverTaxId" receiver-tax-id
                    "receiverBankCode" receiver-bank-code
                    "receiverAccountNumber" receiver-account-number
                    "receiverBranchCode" receiver-branch-code
                    "receiverAccountType" receiver-account-type
                    "endToEndId" end-to-end-id
                    "receiverKeyId" receiver-key-id
                    "description" description
                    "reconciliationId" reconciliation-id
                    "initiatorTaxId" initiator-tax-id
                    "cashAmount" cash-amount
                    "cashierBankCode" cashier-bank-code
                    "cashierType" cashier-type
                    "tags" tags
                    "method" method
                })))))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :fee (.fee java-object)
            :status (.status java-object)
            :flow (.flow java-object)
            :sender-bank-code (.senderBankCode java-object)
            :created (.created java-object)
            :- `:updated` (.- `:updated` java-object)
            :amount (.amount java-object)
            :external-id (.externalId java-object)
            :sender-name (.senderName java-object)
            :sender-tax-id (.senderTaxId java-object)
            :sender-branch-code (.senderBranchCode java-object)
            :sender-account-number (.senderAccountNumber java-object)
            :sender-account-type (.senderAccountType java-object)
            :receiver-name (.receiverName java-object)
            :receiver-tax-id (.receiverTaxId java-object)
            :receiver-bank-code (.receiverBankCode java-object)
            :receiver-account-number (.receiverAccountNumber java-object)
            :receiver-branch-code (.receiverBranchCode java-object)
            :receiver-account-type (.receiverAccountType java-object)
            :end-to-end-id (.endToEndId java-object)
            :receiver-key-id (.receiverKeyId java-object)
            :description (.description java-object)
            :reconciliation-id (.reconciliationId java-object)
            :initiator-tax-id (.initiatorTaxId java-object)
            :cash-amount (.cashAmount java-object)
            :cashier-bank-code (.cashierBankCode java-object)
            :cashier-type (.cashierType java-object)
            :tags (into [] (.tags java-object))
            :method (.method java-object)
        }))

(defn- clojure-query-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            after "after"
            before "before"
            status "status"
            tags "tags"
            ids "ids"
            end-to-end-ids "end-to-end-ids"
            external-id "external-id"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
                "tags" (if (nil? tags) nil (into-array String tags))
                "ids" (if (nil? ids) nil (into-array String ids))
                "endToEndIds" (if (nil? end-to-end-ids) nil (into-array String end-to-end-ids))
                "externalId" external-id
            }))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            page "page"
            limit "limit"
            after "after"
            before "before"
            status "status"
            tags "tags"
            ids "ids"
            end-to-end-ids "end-to-end-ids"
            external-id "external-id"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "page" page
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
                "tags" (if (nil? tags) nil (into-array String tags))
                "ids" (if (nil? ids) nil (into-array String ids))
                "endToEndIds" (if (nil? end-to-end-ids) nil (into-array String end-to-end-ids))
                "externalId" external-id
            }))))

(defn create 
    "Send a list of PixRequest objects for creation in the Stark Infra API
    
    ## Parameters (required):
        - `:requests` [list of PixRequest objects]: list of PixRequest objects to be created in the API
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixRequest objects with updated attributes"
    ([requests]
        (def java-requests (map clojure-to-java requests))
        (def created-java-requests (PixRequest/create java-requests))
        (map java-to-clojure created-java-requests))
        
    ([requests, user]
        (def java-requests (map clojure-to-java requests))
        (def created-java-requests (PixRequest/create java-requests (#'starkinfra.user/get-java-user user)))
        (map java-to-clojure created-java-requests)))

(defn get
    "Receive a single PixRequest object previously created in the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixRequest object with updated attributes"
    ([id]
        (java-to-clojure
            (PixRequest/get id)))
            
    ([id, user]
        (java-to-clojure
            (PixRequest/get id (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixRequest objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"processing\", \"success\", \"failed\"]
        - `:tags` [list of strings, default None]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:end-to-end-ids` [list of strings, default None]: central bank's unique transaction IDs. ex: [\"E79457883202101262140HHX553UPqeq\", \"E79457883202101262140HHX553UPxzx\"]
        - `:external-ids` [list of strings, default None]: url safe strings that must be unique among all your PixRequests. Duplicated external IDs will cause failures. By default, this parameter will block any PixRequests that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixRequest objects with updated attributes"
    ([]
        (map java-to-clojure
            (PixRequest/query)))

    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure
            (PixRequest/query java-params)))
            
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure
            (PixRequest/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixRequest objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your requests.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"processing\", \"success\", \"failed\"]
        - `:tags` [list of strings, default None]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:end-to-end-ids` [list of strings, default None]: central bank's unique transaction IDs. ex: [\"E79457883202101262140HHX553UPqeq\", \"E79457883202101262140HHX553UPxzx\"]
        - `:external-ids` [list of strings, default None]: url safe strings that must be unique among all your PixRequests. Duplicated external IDs will cause failures. By default, this parameter will block any PixRequests that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixRequest objects with updated attributes
        - `:cursor` to retrieve the next page of PixRequest objects"
    
    ([]
        (def request-page (PixRequest/page))
        (def cursor (.cursor request-page))
        (def requests (map java-to-clojure (.requests request-page)))
        {:requests requests, :cursor cursor})

    ([params]
        (def java-params (clojure-page-to-java params))
        (def request-page (PixRequest/page java-params))
        {:payments (map java-to-clojure (.requests request-page)), :cursor (.cursor request-page)})

    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def request-page (PixRequest/page java-params (#'starkinfra.user/get-java-user user)))
        {:payments (map java-to-clojure (.requests request-page)), :cursor (.cursor request-page)}))

(defn parse
    "Create a single PixRequest object from a content string received from a handler listening at the request url.
    If the provided digital signature does not check out with the StarkInfra public key, a
    starkinfra.error.InvalidSignatureError will be raised.
    
    ## Parameters (required):
        - `:content` [string]: response content from request received at user endpoint (not parsed)
        - `:signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - Parsed PixRequest object"
    ([content, signature]
        (java-to-clojure
            (PixRequest/parse content signature)))

    ([content, signature, user]
        (java-to-clojure
            (PixRequest/parse content signature (#'starkinfra.user/get-java-user user)))))

(ns starkinfra.pix-request.log
    "Every time a PixRequest entity is modified, a corresponding PixRequest.Log
    is generated for the entity. This log is never generated by the user.
    
    ## Attributes:
        - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
        - `:created` [datetime.datetime]: creation datetime for the log. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:type` [string]: type of the PixRequest event which triggered the log creation. ex: \"sent\", \"denied\", \"failed\", \"created\", \"success\", \"approved\", \"credited\", \"refunded\", \"processing\"
        - `:errors` [list of strings]: list of errors linked to this PixRequest event
        - `:request` [PixRequest]: PixRequest entity to which the log refers to."
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixRequest$Log])
    (:require [starkinfra.pix-request :as pix-request])
    (:use [starkinfra.user]
          [clojure.walk]))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :created (.created java-object)
            :errors (into [] (.errors java-object))
            :type (.type java-object)
            :request (#'pix-request/java-to-clojure (.request java-object))
        }))

(defn- clojure-query-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            after "after"
            before "before"
            types "types"
            request-ids "request-ids"
            reconciliation-ids "reconciliation-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "paymentIds" (if (nil? request-ids) nil (into-array String request-ids))
                "reconciliationIds" (if (nil? reconciliation-ids) nil (into-array String reconciliation-ids))
            }
        ))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            cursor "cursor"
            limit "limit"
            after "after"
            before "before"
            types "types"
            request-ids "request-ids"
            reconciliation-ids "reconciliation-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" cursor
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "paymentIds" (if (nil? request-ids) nil (into-array String request-ids))
                "reconciliationIds" (if (nil? reconciliation-ids) nil (into-array String reconciliation-ids))
            }
        ))))

(defn get
    "Receive a single PixRequest.Log object previously created by the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixRequest.Log object with updated attributes"
    ([id]
        (java-to-clojure
            (PixRequest$Log/get id)))

    ([id, user]
        (java-to-clojure
            (PixRequest$Log/get id (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixRequest.Log objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. Options: [\"sent\", \"denied\", \"failed\", \"created\", \"success\", \"approved\", \"credited\", \"refunded\", \"processing\"]
        - `:request-ids` [list of strings, default None]: list of PixRequest ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:reconciliation-id` [string, default None]: PixRequest reconciliation id to filter retrieved objects. ex: \"b77f5236-7ab9-4487-9f95-66ee6eaf1781\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixRequest.Log objects with updated attributes"
    ([]
        (map java-to-clojure (PixRequest$Log/query)))
        
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixRequest$Log/query java-params)))

    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixRequest$Log/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixRequest.Log objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your requests.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. Options: [\"sent\", \"denied\", \"failed\", \"created\", \"success\", \"approved\", \"credited\", \"refunded\", \"processing\"]
        - `:request-ids` [list of strings, default None]: list of PixRequest IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:reconciliation-id` [string]: PixRequest reconciliation id to filter retrieved objects. ex: \"b77f5236-7ab9-4487-9f95-66ee6eaf1781\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixRequest.Log objects with updated attributes
        - `:cursor` to retrieve the next page of PixRequest.Log objects"
    ([]
       (def log-page (PixRequest$Log/page))
       (def cursor (.cursor log-page))
       (def logs (map java-to-clojure (.logs log-page)))
       {:logs logs, :cursor cursor})

    ([params]
       (def java-params (clojure-page-to-java params))
       (def log-page (PixRequest$Log/page java-params))
       {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

    ([params, user]
       (def java-params (clojure-page-to-java params))
       (def log-page (PixRequest$Log/page java-params (#'starkinfra.user/get-java-user user)))
       {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))