(ns starkinfra.pix-key
    "PixKeys link bank account information to key ids.
    Key ids are a convenient way to search and pass bank account information.
    When you initialize a Pix Key, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the created object.
    
    ## Parameters (required):
        - `:account-created` [datetime.date, datetime.datetime or string]: opening Date or DateTime for the linked account. ex: \"2022-01-01T12:00:00:00\".
        - `:account-number` [string]: number of the linked account. ex: \"76543\".
        - `:account-type` [string]: type of the linked account. Options: \"checking\", \"savings\", \"salary\" or \"payment\".
        - `:branch-code` [string]: branch code of the linked account. ex: 1234\".
        - `:name` [string]: holder's name of the linked account. ex: \"Jamie Lannister\".
        - `:tax-id` [string]: holder's taxId (CPF/CNPJ) of the linked account. ex: \"012.345.678-90\".
        
    ## Parameters (optional):
        - `:id` [string, default None]: id of the registered PixKey. Allowed types are: CPF, CNPJ, phone number or email. If this parameter is not passed, an EVP will be created. ex: \"+5511989898989\";
        - `:tags` [list of strings, default None]: list of strings for reference when searching for PixKeys. ex: [\"employees\", \"monthly\"]
    
    ## Attributes (return-only):
        - `:owned` [datetime.datetime]: datetime when the key was owned by the holder. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:owner-type` [string]: type of the owner of the PixKey. Options: \"business\" or \"individual\".
        - `:status` [string]: current PixKey status. Options: \"created\", \"registered\", \"canceled\", \"failed\"
        - `:bank-code` [string]: bank_code of the account linked to the Pix Key. ex: \"20018183\".
        - `:bank-name` [string]: name of the bank that holds the account linked to the PixKey. ex: \"StarkBank\"
        - `:type` [string]: type of the PixKey. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\",
        - `:created` [datetime.datetime]: creation datetime for the PixKey. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixKey])
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
            id "id"
        } (stringify-keys clojure-map)]
        (defn- apply-java-hashmap [x] (java.util.HashMap x))
        (PixKey. (java.util.HashMap.
                {
                    "accountCreated" account-created
                    "accountNumber" account-number
                    "accountType" account-type
                    "branchCode" branch-code
                    "name" name
                    "taxId" tax-id
                    "id" id
                })))))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :tags (into [] (.tags java-object))
            :account-created (.accountCreated java-object)
            :account-number (.accountNumber java-object)
            :account-type (.accountType java-object)
            :branch-code (.branchCode java-object)
            :name (.name java-object)
            :tax-id (.taxId java-object)
            :owned (.owned java-object)
            :owner-type (.ownerType java-object)
            :status (.status java-object)
            :bank-code (.bankCode java-object)
            :bank-name (.bankName java-object)
            :type (.type java-object)
            :created (.created java-object)
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
            type "type"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
                "tags" (if (nil? tags) nil (into-array String tags))
                "ids" (if (nil? ids) nil (into-array String ids))
                "type" type
            }
        ))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            cursor "cursor"
            limit "limit"
            after "after"
            before "before"
            status "status"
            tags "tags"
            ids "ids"
            type "type"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" cursor
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
                "tags" (if (nil? tags) nil (into-array String tags))
                "ids" (if (nil? ids) nil (into-array String ids))
                "type" type
            }
        ))))

(defn- clojure-update-to-java
    ([clojure-map]
        (let [{
            account-created "account-created"
            account-number "account-number"
            account-type "account-type"
            branch-code "branch-code"
            name "name"
            tax-id "tax-id"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "accountCreated" account-created
                "accountNumber" account-number
                "accountType" account-type
                "branchCode" branch-code
                "name" name
                "taxId" tax-id
            }
        ))))

(defn- clojure-get-to-java
    ([clojure-map]
        (let [{
            end-to-end-id "end-to-end-id"
            user "user"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "endToEndId" end-to-end-id
                "user" user
            }
        ))))

(defn create
    "Create a PixKey linked to a specific account in the Stark Infra API
    
    ## Parameters (optional):
        - `:key` [PixKey object]: PixKey object to be created in the API.
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixKey object with updated attributes."
    ([key]
        (def java-key (clojure-to-java key))
        (def created-java-key (PixKey/create java-key))
        (java-to-clojure created-java-key))
        
    ([key, user]
        (def java-key (clojure-to-java key))
        (def created-java-key (PixKey/create java-key (#'starkinfra.user/get-java-user user)))
        (java-to-clojure created-java-key)))

(defn query
    "Receive a generator of PixKeys objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"registered\", \"canceled\", \"failed\"]
        - `:tags` [list of strings, default None]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:type` [string, default None]: filter for the type of retrieved PixKeys. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixKey objects with updated attributes"
    ([]
        (map java-to-clojure (PixKey/query)))
        
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixKey/query java-params)))
        
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixKey/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a generator of PixKeys objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call.
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"registered\", \"canceled\", \"failed\"]
        - `:tags` [list of strings, default None]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:type` [string, default None]: filter for the type of retrieved PixKeys. Options: \"cpf\", \"cnpj\", \"phone\", \"email\" and \"evp\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - `:cursor` to retrieve the next page of PixKey objects
        - generator of PixKey objects with updated attributes"
    
    ([]
        (def key-page (PixKey/page))
        (def cursor (.cursor key-page))
        (def keys (map java-to-clojure (.keys key-page)))
        {:keys keys, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def key-page (PixKey/page java-params))
        {:keys (map java-to-clojure (.keys key-page)), :cursor (.cursor key-page)})
        
    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def key-page (PixKey/page java-params (#'starkinfra.user/get-java-user user)))
        {:keys (map java-to-clojure (.keys key-page)), :cursor (.cursor key-page)}))

(defn get
    "Retrieve the PixKey object linked to your Workspace in the Stark Infra API by its id.

    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\".
        - `:payer-id` [string]: tax id (CPF/CNPJ) of the individual or business requesting the PixKey information. This id is used by the Central Bank to limit request rates. ex: \"20.018.183/0001-80\".
    
    ## Parameters (optional):
        - `:end-to-end-id` [string, default None]: central bank's unique transaction id. If the request results in the creation of a PixRequest, the same endToEndId should be used. If this parameter is not passed, one endToEndId will be automatically created. Example: \"E00002649202201172211u34srod19le\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
   
    ## Return:
        - PixKey object that corresponds to the given id."
        
    ([id, payer-id]
        (java-to-clojure 
            (PixKey/get id payer-id)))
    
    ([id, payer-id, params]
        (def java-params (clojure-get-to-java params))
        (java-to-clojure 
            (PixKey/get id payer-id params))))

(defn update
    "Update a PixKey parameters by passing id.
    
    ## Parameters (required):
        - `:id` [string]: PixKey id. ex: '5656565656565656'
        - `:reason` [string]: reason why the PixKey is being patched. Options: \"branchTransfer\", \"reconciliation\" or \"userRequested\".
    
    ## Parameters (optional):
        - `:account-created` [datetime.date, datetime.datetime or string, default None]: opening Date or DateTime for the account to be linked. ex: \"2022-01-01.
        - `:account-number` [string, default None]: number of the account to be linked. ex: \"76543\".
        - `:account-type` [string, default None]: type of the account to be linked. Options: \"checking\", \"savings\", \"salary\" or \"payment\".
        - `:branch-code` [string, default None]: branch code of the account to be linked. ex: 1234\".
        - `:name` [string, default None]: holder's name of the account to be linked. ex: \"Jamie Lannister\".
    
    ## Return:
        - PixKey with updated attributes"
    ([id, reason, params]
        (java-to-clojure 
            (PixKey/update id reason 
                (clojure-update-to-java params))))
        
    ([id, reason, params, user]
        (java-to-clojure 
            (PixKey/update id reason 
                (clojure-update-to-java params) 
                (#'starkinfra.user/get-java-user user)))))

(defn cancel
    "Cancel a pixKey entity previously created in the Stark Infra API
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - `:canceled` pixKey object"
    
    ([id]
        (java-to-clojure 
            (PixKey/cancel id)))
    
    ([id, user]
        (java-to-clojure 
            (PixKey/cancel id (#'starkinfra.user/get-java-user user)))))


(ns starkinfra.pix-key.log
    "# PixKey.Log object
    Every time a PixKey entity is modified, a corresponding PixKey.Log
    is generated for the entity. This log is never generated by the user.
    
    ## Attributes:
        - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
        - `:created` [datetime.datetime]: creation datetime for the log. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:type` [string]: type of the PixKey event which triggered the log creation. ex: \"created\", \"registered\", \"updated\", \"failed\", \"canceling\", \"canceled\".
        - `:errors` [list of strings]: list of errors linked to this PixKey event
        - `:key` [PixKey]: PixKey entity to which the log refers to."
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixKey$Log])
    (:require [starkinfra.pix-key :as pix-key])
    (:use [starkinfra.user]
          [clojure.walk]))

(defn- java-to-clojure
    ([java-object]
    {
        :id (.id java-object)
        :created (.created java-object)
        :type (.type java-object)
        :errors (.errors java-object)
        :key (#'pix-key/java-to-clojure (.key java-object))}))

(defn- clojure-query-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            after "after"
            before "before"
            types "types"
            key-ids "key-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "keyIds" (if (nil? key-ids) nil (into-array String key-ids))
            }
        ))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            cursor "cursor"
            ids "ids"
            limit "limit"
            after "after"
            before "before"
            types "types"
            key-ids "key-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" cursor
                "ids" (if (nil? ids) nil (into-array String ids))
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "keyIds" (if (nil? key-ids) nil (into-array String key-ids))
            }
        ))))

(defn get
    "Receive a single PixKey.Log object previously created by the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixKey.Log object with updated attributes"
    ([id]
        (java-to-clojure 
            (PixKey$Log/get id)))
    
    ([id, user]
        (java-to-clojure 
            (PixKey$Log/get 
                id 
                (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixKey.Log objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:ids` [list of strings, default None]: Log ids to filter PixKey Logs. ex: [\"5656565656565656\"]
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. ex: [\"created\", \"registered\", \"updated\", \"failed\", \"canceling\", \"canceled\"]
        - `:key-ids` [list of strings, default None]: list of PixKey IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixKey.Log objects with updated attributes"
    ([]
        (map java-to-clojure 
            (PixKey$Log/query)))

    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure 
            (PixKey$Log/query java-params)))

    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure 
            (PixKey$Log/query 
                java-params 
                (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixKey.Log objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your keys.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:ids` [list of strings, default None]: Log ids to filter PixKey Logs. ex: [\"5656565656565656\"]
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. ex: [\"created\", \"registered\", \"updated\", \"failed\", \"canceling\", \"canceled\"]
        - `:key-ids` [list of strings, default None]: list of PixKey IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixKey.Log objects with updated attributes
        - `:cursor` to retrieve the next page of PixKey.Log objects"
    ([]
        (def log-page (PixKey$Log/page))
        (def cursor (.cursor log-page))
        (def logs (map java-to-clojure (.logs log-page)))
        {:logs logs, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixKey$Log/page java-params))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixKey$Log/page java-params (#'starkinfra.user/get-java-user user)))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))
    