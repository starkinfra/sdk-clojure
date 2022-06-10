(ns starkinfra.pix-claim
    "When you initialize a PixClaim, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the created object.
    
    ## Parameters (required):
        - `:account-created` [datetime.date, datetime.datetime or string]: opening Date or DateTime for the account claiming the PixKey. ex: \"2022-01-01\".
        - `:account-number` [string]: number of the account claiming the PixKey. ex: \"76543\".
        - `:account-type` [string]: type of the account claiming the PixKey. Options: \"checking\", \"savings\", \"salary\" or \"payment\".
        - `:branch-code` [string]: branch code of the account claiming the PixKey. ex: 1234\".
        - `:name` [string]: holder's name of the account claiming the PixKey. ex: \"Jamie Lannister\".
        - `:tax-id` [string]: holder's taxId of the account claiming the PixKey (CPF/CNPJ). ex: \"012.345.678-90\".
        - `:key-id` [string]: id of the registered Pix Key to be claimed. Allowed keyTypes are CPF, CNPJ, phone number or email. ex: \"+5511989898989\".
    
    ## Attributes (return-only):
        - `:id` [string]: unique id returned when the PixClaim is created. ex: \"5656565656565656\"
        - `:status` [string]: current PixClaim status. Options: \"created\", \"failed\", \"delivered\", \"confirmed\", \"success\", \"canceled\"
        - `:type` [string]: type of Pix Claim. Options: \"ownership\", \"portability\".
        - `:key-type` [string]: keyType of the claimed PixKey. Options: \"CPF\", \"CNPJ\", \"phone\" or \"email\"
        - `:agent` [string]: Options: \"claimer\" if you requested the PixClaim or \"claimed\" if you received a PixClaim request.
        - `:bank-code` [string]: bank_code of the account linked to the PixKey being claimed. ex: \"20018183\".
        - `:claim`ed_bank_code [string]: bank_code of the account donating the PixKey. ex: \"20018183\".
        - `:created` [datetime.datetime]: creation datetime for the PixClaim. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:updated` [datetime.datetime]: update datetime for the PixClaim. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixClaim])
    (:use [starkinfra.user]
          [clojure.walk]))

(defn- clojure-to-java
    ([clojure-map]
        (let [{
            account-created "account-created"
            account-number "account-number"
            account-type "account-type"
            branch-code "branch-code"
            name "name"
            tax-id "tax-id"
            key-id "key-id"
        }
        (stringify-keys clojure-map)]
        
        (defn- apply-java-hashmap [x] (java.util.HashMap x))
        
        (PixClaim. (java.util.HashMap.
            {
                "accountCreated" account-created
                "accountNumber" account-number
                "accountType" account-type
                "branchCode" branch-code
                "name" name
                "taxId" tax-id
                "keyId" key-id
            })))))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :accoount-created (.accountCreated java-object)
            :account-number (.accountNumber java-object)
            :account-type (.accountType java-object)
            :branch-code (.branchCode java-object)
            :name (.name java-object)
            :tax-id (.taxId java-object)
            :key-id (.keyId java-object)
            :status (.status java-object)
            :type (.type java-object)
            :key-type (.keyType java-object)
            :agent (.agent java-object)
            :bank-code (.bankCode java-object)
            :claimed-bank-code (.claimedBankCode java-object)
            :created (.created java-object)
            :- `:updated` (.- `:updated` java-object)
        }))

(defn- clojure-query-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            after "after"
            before "before"
            status "status"
            ids "ids"
            type "type"
            agent "agent"
            key-type "key-type"
            key-id "key-id"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
                "ids" (if (nil? ids) nil (into-array String ids))
                "type" type
                "agent" agent
                "keyType" key-type
                "keyId" key-id
            }))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            after "after"
            before "before"
            status "status"
            ids "ids"
            type "type"
            agent "agent"
            key-type "key-type"
            key-id "key-id"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
                "ids" (if (nil? ids) nil (into-array String ids))
                "type" type
                "agent" agent
                "keyType" key-type
                "keyId" key-id
            }))))

(defn create
    "Create a PixClaim to request the transfer of a PixKey to an account
    hosted at other Pix participants in the Stark Infra API.
    
    ## Parameters (required):
        - `:claim` [PixClaim object]: PixClaim object to be created in the API.
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixClaim object with updated attributes."
    ([claim]
        (def java-claim (clojure-to-java claim))
        (def created-java-claim (PixClaim/create java-claim))
        (map java-to-clojure created-java-claim))

    ([claim, user]
        (def java-claim (clojure-to-java claim))
        (def created-java-claim (PixClaim/create java-claim (#'starkinfra.user/get-java-user user)))
        (map java-to-clojure created-java-claim)))

(defn get
    "Retrieve a PixClaim object linked to your Workspace in the Stark Infra API by its id.
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixClaim object that corresponds to the given id."
    ([id]
        (java-to-clojure (PixClaim/get id)))

    ([id, user]
        (java-to-clojure (PixClaim/get id (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixClaims objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"failed\", \"delivered\", \"confirmed\", \"success\", \"canceled\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:type` [strings, default None]: filter for the type of retrieved PixClaims. Options: \"ownership\" or \"portability\".
        - `:agent` [string, default None]: filter for the agent of retrieved PixClaims. Options: \"claimer\" or \"claimed\".
        - `:key-type` [string, default None]: filter for the PixKey type of retrieved PixClaims. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\",
        - `:key-id` [string, default None]: filter PixClaims linked to a specific PixKey id. Example: \"+5511989898989\".
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixClaim objects with updated attributes"
    ([]
        (map java-to-clojure (PixClaim/query)))
    
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure
            (PixClaim/query java-params)))
            
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure
            (PixClaim/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "eceive a list of up to 100 PixClaims objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your requests.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call.
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"failed\", \"delivered\", \"confirmed\", \"success\", \"canceled\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:type` [strings, default None]: filter for the type of retrieved PixClaims. Options: \"ownership\" or \"portability\".
        - `:agent` [string, default None]: filter for the agent of retrieved PixClaims. Options: \"claimer\" or \"claimed\".
        - `:key-type` [string, default None]: filter for the PixKey type of retrieved PixClaims. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\",
        - `:key-id` [string, default None]: filter PixClaims linked to a specific PixKey id. Example: \"+5511989898989\".
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixClaim objects with updated attributes and cursor to retrieve the next page of PixClaim objects"
    ([]
        (def claim-page (PixClaim/page))
        (def cursor (.cursor claim-page))
        (def claims (map java-to-clojure (.claims claim-page)))
        {:claims claims, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def claim-page (PixClaim/page java-params))
        {:claims (map java-to-clojure (.claims claim-page)), :cursor (.cursor claim-page)})
        
    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def claim-page (PixClaim/page java-params (#'starkinfra.user/get-java-user user)))
        {:claims (map java-to-clojure (.claims claim-page)), :cursor (.cursor claim-page)}))

(ns starkinfra.pix-claim.log
    "Every time a PixClaim entity is modified, a corresponding PixClaim.Log
    is generated for the entity. This log is never generated by the user.
    
    ## Attributes:
        - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
        - `:created` [datetime.datetime]: creation datetime for the log. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:type` [string]: type of the PixClaim event which triggered the log creation. ex: \"created\", \"failed\", \"delivering\", \"delivered\", \"confirming\", \"confirmed\", \"success\", \"canceling\", \"canceled\"
        - `:errors` [list of strings]: list of errors linked to this PixClaim event
        - `:agent` [string]: agent that modified the PixClaim resulting in the Log. Options: \"claimer\", \"claimed\".
        - `:reason` [string]: reason why the PixClaim was modified, resulting in the Log. Options: \"fraud\", \"userRequested\", \"accountClosure\", \"defaultOperation\", \"reconciliation\"
        - `:claim` [PixClaim]: PixClaim entity to which the log refers to."
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixClaim$Log])
    (:require [starkinfra.pix-claim :as claim])
    (:use [starkinfra.user]
            [clojure.walk]))

(defn- java-to-clojure
    ([java-object]
        {:id (.id java-object)
            :created (.created java-object)
            :type (.type java-object)
            :errors (.errors java-object)
            :agent (.agent java-object)
            :reason (.reason java-object)
            :claim (#'claim/java-to-clojure (.claim java-object))}))

(defn- clojure-query-to-java
    ([clojure-map]
        (let[{
            limit "limit"
            after "after"
            before "before"
            ids "ids"
            types "types"
            claim-ids "claim-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "ids" (if (nil? ids) nil (into-array String ids))
                "types" types
                "claimIds" (if (nil? claim-ids) nil (into-array String claim-ids))}))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let[{
            cursor "cursor"
            limit "limit"
            after "after"
            before "before"
            ids "ids"
            types "types"
            claim-ids "claim-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" (if (nil? cursor) nil (Integer. cursor))
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "ids" (if (nil? ids) nil (into-array String ids))
                "types" types
                "claimIds" (if (nil? claim-ids) nil (into-array String claim-ids))}))))

(defn get 
    "# Retrieve a specific PixClaim.Log
    Receive a single PixClaim.Log object previously created by the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixClaim.Log object with updated attributes"
    ([id]
        (java-to-clojure
            (PixClaim$Log/get id)))

    ([id, user]
        (java-to-clojure
            (PixClaim$Log/get
                id
                (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixClaim.Log objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:ids` [list of strings, default None]: Log ids to filter PixClaim Logs. ex: [\"5656565656565656\"]
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. ex: [\"created\", \"failed\", \"delivering\", \"delivered\", \"confirming\", \"confirmed\", \"success\", \"canceling\", \"canceled\"]
        - `:claim-ids` [list of strings, default None]: list of PixClaim ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixClaim.Log objects with updated attributes"
    ([]
        (map java-to-clojure (PixClaim$Log/query)))

    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixClaim$Log/query java-params)))
        
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixClaim$Log/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixClaim.Log objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your claims.

    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:ids` [list of strings, default None]: Log ids to filter PixClaim Logs. ex: [\"5656565656565656\"]
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. ex: [\"created\", \"failed\", \"delivering\", \"delivered\", \"confirming\", \"confirmed\", \"success\", \"canceling\", \"canceled\"]
        - `:claim-ids` [list of strings, default None]: list of PixClaim IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixClaim.Log objects with updated attributes
        - `:cursor` to retrieve the next page of PixClaim.Log objects"
    ([]
        (def log-page (PixClaim$Log/page))
        (def cursor (.cursor log-page))
        (def logs (map java-to-clojure (.logs log-page)))
        {:logs logs, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixClaim$Log/page java-params))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})
        
    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixClaim$Log/page java-params (#'starkinfra.user/get-java-user user)))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))
            