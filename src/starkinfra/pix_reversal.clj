(ns starkinfra.pix-reversal
    "PixReversals are instant payments used to revert PixRequests. You can only
    revert inbound PixRequests.
    When you initialize a PixReversal, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the list of created objects.
    
    ## Parameters (required):
        - `:amount` [integer]: amount in cents to be reversed from the PixRequest. ex: 1234 (= R$ 12.34)
        - `:external_id` [string]: string that must be unique among all your PixReversals. Duplicated external IDs will cause failures. By default, this parameter will block any PixReversal that repeats amount and receiver information on the same date. ex: \"my-internal-id-123456\"
        - `:end-to-end-id` [string]: central bank's unique transaction ID. ex: \"E79457883202101262140HHX553UPqeq\"
        - `:reason` [string]: reason why the PixRequest is being reversed. Options are \"bankError\", \"fraud\", \"chashierError\", \"customerRequest\"
    
    ## Parameters (optional):
        - `:tags` [list of strings, default None]: list of strings for reference when searching for PixReversals. ex: [\"employees\", \"monthly\"]
    
    ## Attributes (return-only):
        - `:id` [string]: unique id returned when the PixReversal is created. ex: \"5656565656565656\".
        - `:return-id` [string]: central bank's unique reversal transaction ID. ex: \"D20018183202202030109X3OoBHG74wo\".
        - `:bank-code` [string]: code of the bank institution in Brazil. ex: \"20018183\"
        - `:fee` [string]: fee charged by this PixReversal. ex: 200 (= R$ 2.00)
        - `:status` [string]: current PixReversal status. ex: \"created\", \"processing\", \"success\", \"failed\"
        - `:flow` [string]: direction of money flow. ex: \"in\" or \"out\"
        - `:created` [datetime.datetime]: creation datetime for the PixReversal. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:updated` [datetime.datetime]: latest update datetime for the PixReversal. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set update])
    (:import [com.starkinfra PixReversal])
    (:use [starkinfra.user]
          [clojure.walk]))

(defn- clojure-to-java
    ([clojure-map]
        (let [{
            amount "amount"
            external-id "external-id"
            end-to-end-id "end-to-end-id"
            reason "reason"
            tags "tags"
        }
        (stringify-keys clojure-map)]
            
            (defn- apply-java-hashmap [x] (java.util.HashMap x))
            
            (PixReversal. (java.util.HashMap.
                {
                    "amount" amount
                    "externalId" external-id
                    "endToEndId" end-to-end-id
                    "reason" reason
                    "tags" (if (nil? tags) nil (into-array String tags))
                }
            )))))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :return-id (.returnId java-object)
            :bank-code (.bankCode java-object)
            :fee (.fee java-object)
            :status (.status java-object)
            :flow (.flow java-object)
            :created (.created java-object)
            :- `:updated` (.- `:updated` java-object)
            :amount (.amount java-object)
            :external-id (.externalId java-object)
            :end-to-end-id (.endToEndId java-object)
            :reason (.reason java-object)
            :tags (into [] (.tags java-object))
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
            return-ids "return-ids"
            external-ids "external-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil limit)
                "after" after
                "before" before
                "status" status
                "tags" (if (nil? tags) nil (into-array String tags))
                "ids" (if (nil? ids) nil (into-array String ids))
                "returnIds" (if (nil? return-ids) nil (into-array String return-ids))
                "externalIds" (if (nil? external-ids) nil external-ids)
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
            return-ids "return-ids"
            external-ids "external-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" (if (nil? cursor) nil cursor)
                "limit" (if (nil? limit) nil limit)
                "after" after
                "before" before
                "status" status
                "tags" (if (nil? tags) nil (into-array String tags))
                "ids" (if (nil? ids) nil (into-array String ids))
                "returnIds" (if (nil? return-ids) nil (into-array String return-ids))
                "externalIds" (if (nil? external-ids) nil external-ids)
            }))))

(defn create
    "Send a list of PixReversal objects for creation in the Stark Infra API
   
    ## Parameters (required):
        - `:reversals` [list of PixReversal objects]: list of PixReversal objects to be created in the API
   
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
   
    ## Return:
        - list of PixReversal objects with updated attributes"
    ([reversals]
        (def java-reversals (map clojure-to-java reversals))
        (def created-java-reversals (PixReversal/create java-reversals))
        (map java-to-clojure created-java-reversals))
        
    ([reversals, user]
        (def java-reversals (map clojure-to-java reversals))
        (def created-java-reversals (PixReversal/create java-reversals (#'starkinfra.user/get-java-user user)))
        (map java-to-clojure created-java-reversals)))

(defn get
    "Receive a single PixReversal object previously created in the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixReversal object with updated attributes"
    ([id]
        (java-to-clojure
            (PixReversal/get id)))
            
    ([id, user]
        (java-to-clojure
            (PixReversal/get
                id
                (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixReversal objects previously created in the Stark Infra API
   
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"processing\", \"success\", \"failed\"]
        - `:tags` [list of strings, default None]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:return-ids` [list of strings, default None]: central bank's unique reversal transaction IDs. ex: [\"D20018183202202030109X3OoBHG74wo\", \"D20018183202202030109X3OoBHG72rd\"].
        - `:external-ids` [list of strings, default None]: url safe strings that must be unique among all your PixReversals. Duplicated external IDs will cause failures. By default, this parameter will block any PixReversal that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
   
    ## Return:
        - generator of PixReversal objects with updated attributes"
    
    ([]
        (map java-to-clojure (PixReversal/query)))
        
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixReversal/query java-params)))
        
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixReversal/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixReversal objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your reversals.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"processing\", \"success\", \"failed\"]
        - `:tags` [list of strings, default None]: tags to filter retrieved objects. ex: [\"tony\", \"stark\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:return-ids` [list of strings, default None]: central bank's unique reversal transaction ID. ex: [\"D20018183202202030109X3OoBHG74wo\", \"D20018183202202030109X3OoBHG72rd\"].
        - `:external-ids` [list of strings, default None]: url safe string that must be unique among all your PixReversals. Duplicated external IDs will cause failures. By default, this parameter will block any PixReversal that repeats amount and receiver information on the same date. ex: [\"my-internal-id-123456\", \"my-internal-id-654321\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixReversal objects with updated attributes
        - `:cursor` to retrieve the next page of PixReversal objects"
    ([]
        (def reversal-page (PixReversal/page))
        (def cursor (.cursor reversal-page))
        (def reversals (map java-to-clojure (.reversals reversal-page)))
        {:reversals reversals, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def reversal-page (PixReversal/page java-params))
        {:reversals (map java-to-clojure (.reversals reversal-page)), :cursor (.cursor reversal-page)})
        
    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def reversal-page (PixReversal/page java-params (#'starkinfra.user/get-java-user user)))
        {:reversals (map java-to-clojure (.reversals reversal-page)), :cursor (.cursor reversal-page)}))

(defn parse
    "Create a single PixReversal object from a content string received from a handler listening at the reversal url.
    If the provided digital signature does not check out with the StarkInfra public key, a
    starkinfra.error.InvalidSignatureError will be raised.
    
    ## Parameters (required):
        - `:content` [string]: response content from request received at user endpoint (not parsed)
        - `:signature` [string]: base-64 digital signature received at response header \"Digital-Signature\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - Parsed PixReversal object"
    ([content, signature]
        (java-to-clojure
            (PixReversal/parse content signature)))

    ([content, signature, user]
        (java-to-clojure
            (PixReversal/parse content signature (#'starkinfra.user/get-java-user user)))))

(ns starkinfra.pix-reversal.log
    "Every time a PixReversal entity is modified, a corresponding PixReversal.Log
    is generated for the entity. This log is never generated by the user.
    
    ## Attributes:
        - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
        - `:created` [datetime.datetime]: creation datetime for the log. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:type` [string]: type of the PixReversal event which triggered the log creation. ex: \"sent\", \"denied\", \"failed\", \"created\", \"success\", \"approved\", \"credited\", \"refunded\", \"processing\"
        - `:errors` [list of strings]: list of errors linked to this PixReversal event
        - `:reversal` [PixReversal object]: PixReversal entity to which the log refers to."
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixReversal$Log])
    (:require [starkinfra.pix-reversal :as pix-reversal])
    (:use [starkinfra.user]
           [clojure.walk]))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :created (.created java-object)
            :type (.type java-object)
            :errors (.errors java-object)
            :reversal (#'pix-reversal/java-to-clojure (.reversal java-object))
        }))

(defn- clojure-query-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            after "after"
            before "before"
            types "types"
            reversal-ids "reversal-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "reversal-ids" (if (nil? reversal-ids) nil (into-array String reversal-ids))
            }))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            cursor "cursor"
            limit "limit"
            after "after"
            before "before"
            types "types"
            reversal-ids "reversal-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" cursor
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "reversal-ids" (if (nil? reversal-ids) nil (into-array String reversal-ids))
            }))))

(defn get
    "Receive a single PixReversal.Log object previously created by the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixReversal.Log object with updated attributes"
    ([id]
        (java-to-clojure
            (PixReversal$Log/get id)))

    ([id, user]
        (java-to-clojure
            (PixReversal$Log/get id (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixReversal.Log objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. Options: [\"sent\", \"denied\", \"failed\", \"created\", \"success\", \"approved\", \"credited\", \"refunded\", \"processing\"]
        - `:reversal`-ids [list of strings, default None]: list of PixReversal IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixReversal.Log objects with updated attributes"
    ([]
        (map java-to-clojure (PixReversal$Log/query)))
        
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixReversal$Log/query java-params)))
        
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixReversal$Log/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixReversal.Log objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your reversals.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. Options: [\"sent\", \"denied\", \"failed\", \"created\", \"success\", \"approved\", \"credited\", \"refunded\", \"processing\"]
        - `:reversal`-ids [list of strings, default None]: list of PixReversal IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixReversal.Log objects with updated attributes
        - `:cursor` to retrieve the next page of PixRequest.Log objects"
    ([]
        (def log-page (PixReversal$Log/page))
        (def cursor (.cursor log-page))
        (def logs (map java-to-clojure (.logs log-page)))
        {:logs logs, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixReversal$Log/page java-params))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})

    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixReversal$Log/page java-params (#'starkinfra.user/get-java-user user)))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))