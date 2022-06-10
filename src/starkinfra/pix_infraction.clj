(ns starkinfra.pix-infraction
    "PixInfractions are used to report transactions that are suspected of
    fraud, to request a refund or to reverse a refund.
    When you initialize a PixInfraction, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the created object.
    
    ## Parameters (required):
        - `:reference-id` [string]: end_to_end_id or return_id of the transaction being reported. ex: \"E20018183202201201450u34sDGd19lz\"
        - `:type` [string]: type of infraction report. Options: \"fraud\", \"reversal\", \"reversalChargeback\"
    
    ## Parameters (optional):
        - `:description` [string, default None]: description for any details that can help with the infraction investigation.
    
    ## Attributes (return-only):
        - `:id` [string]: unique id returned when the PixInfraction is created. ex: \"5656565656565656\"
        - `:credited-bank-code` [string]: bank_code of the credited Pix participant in the reported transaction. ex: \"20018183\"
        - `:debited-bank-code` [string]: bank_code of the debited Pix participant in the reported transaction. ex: \"20018183\"
        - `:agent` [string]: Options: \"reporter\" if you created the PixInfraction, \"reported\" if you received the PixInfraction.
        - `:analysis` [string]: analysis that led to the result.
        - `:bacen-id` [string]: central bank's unique UUID that identifies the infraction report.
        - `:reported-by` [string]: agent that reported the PixInfraction. Options: \"debited\", \"credited\".
        - `:result` [string]: result after the analysis of the PixInfraction by the receiving party. Options: \"agreed\", \"disagreed\"
        - `:status` [string]: current PixInfraction status. Options: \"created\", \"failed\", \"delivered\", \"closed\", \"canceled\".
        - `:created` [datetime.datetime]: creation datetime for the PixInfraction. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:updated` [datetime.datetime]: latest update datetime for the PixInfraction. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixInfraction])
    (:use [starkinfra.user]
          [clojure.walk]))
          
(defn- clojure-to-java
    ([clojure-map]
        (let [{
            reference-id "reference-id"
            type "type"
            description "description"
        }
        (stringify-keys clojure-map)]
        
            (defn- apply-java-hashmap [x] (java.util.HashMap x))
            
            (PixInfraction. (java.util.HashMap.
                {
                    "referenceId" reference-id
                    "type" type
                    "description" description
                }
            )))))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :credited-bank-code (.creditedBankCode java-object)
            :debited-bank-code (.debitedBankCode java-object)
            :agent (.agent java-object)
            :analysis (.analysis java-object)
            :bacen-id (.bacenId java-object)
            :reported-by (.reportedBy java-object)
            :result (.result java-object)
            :status (.status java-object)
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
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "status" status
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
                "ids" (if (nil? ids) nil (into-array String ids))
                "type" type
            }
        ))))

(defn- clojure-update-to-java
    ([clojure-map]
        (let [{
            analysis "analysis"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "analysis" analysis
            }
        ))))

(defn create
    "Create PixInfractions in the Stark Infra API
    
    ## Parameters (required):
        - `:infractions` [list of PixInfractions]: list of PixInfraction objects to be created in the API.
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixInfraction objects with updated attributes"
    
    ([infractions]
        (def java-infractions (map clojure-to-java infractions))
        (def created-java-infractions (PixInfraction/create java-infractions))
        (map java-to-clojure created-java-infractions))

    ([infractions, user]
        (def java-infractions (map clojure-to-java infractions))
        (def created-java-infractions (PixInfraction/create java-infractions (#'starkinfra.user/get-java-user user)))
        (map java-to-clojure created-java-infractions)))

(defn query
    "Receive a generator of PixInfractions objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:type` [list of strings, default None]: filter for the type of retrieved PixInfractions. Options: \"fraud\", \"reversal\", \"reversalChargeback\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixInfraction objects with updated attributes"
    ([]
        (map java-to-clojure (PixInfraction/query)))
        
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixInfraction/query java-params)))

    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixInfraction/query java-params (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixInfractions objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your requests.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call.
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:type` [list of strings, default None]: filter for the type of retrieved PixInfractions. Options: \"fraud\", \"reversal\", \"reversalChargeback\"
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixInfraction objects with updated attributes and cursor to retrieve the next page of PixInfraction objects"

    ([]
        (def infraction-page (PixInfraction/page))
        (def cursor (.cursor infraction-page))
        (def infractions (map java-to-clojure (.infractions infraction-page)))
        {:infractions infractions :cursor cursor})

    ([params]
        (def java-params (clojure-page-to-java params))
        (def infraction-page (PixInfraction/page java-params))
        {:infractions (map java-to-clojure (.infractions infraction-page)), :cursor (.cursor infraction-page)})

    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def infraction-page (PixInfraction/page java-params (#'starkinfra.user/get-java-user user)))
        {:infractions (map java-to-clojure (.infractions infraction-page)), :cursor (.cursor infraction-page)}))

(defn get
    "Retrieve the PixInfraction object linked to your Workspace in the Stark Infra API using its id.
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\".
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixInfraction object that corresponds to the given id."
    ([id]
        (java-to-clojure
            (PixInfraction/get id)))
    
    ([id, user]
        (java-to-clojure
            (PixInfraction/get 
                id 
                (#'starkinfra.user/get-java-user user)))))

(defn update
    "Respond to a received PixInfraction.
    
    ## Parameters (required):
        - `:id` [string]: PixInfraction id. ex: '5656565656565656'
        - `:result` [string]: result after the analysis of the PixInfraction. Options: \"agreed\", \"disagreed\"
    
    ## Parameters (optional):
        - `:analysis` [string, default None]: analysis that led to the result.
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixInfraction with updated attributes"
    ([id, result]
        (java-to-clojure
            (PixInfraction/update id result)))

    ([id, result, params]
        (java-to-clojure
            (PixInfraction/update 
                id 
                result
                (clojure-update-to-java params))))

    ([id, result, params, user]
        (java-to-clojure
            (PixInfraction/update 
                id 
                result
                (clojure-update-to-java params)
                (#'starkinfra.user/get-java-user user)))))

(defn cancel
    "Cancel a PixInfraction entity previously created in the Stark Infra API
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - `:canceled` PixInfraction object"
    ([id]
        (java-to-clojure
            (PixInfraction/cancel id)))

    ([id, user]
        (java-to-clojure
            (PixInfraction/cancel 
                id 
                (#'starkinfra.user/get-java-user user)))))

(ns starkinfra.pix-infraction.log
    "Every time a PixInfraction entity is modified, a corresponding PixInfraction.Log
    is generated for the entity. This log is never generated by the user.
    
    ## Attributes:
        - `:id` [string]: unique id returned when the log is created. ex: \"5656565656565656\"
        - `:created` [datetime.datetime]: creation datetime for the log. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:type` [string]: type of the PixInfraction event which triggered the log creation. ex: \"created\", \"failed\", \"delivering\", \"delivered\", \"closed\", \"canceled\"
        - `:errors` [list of strings]: list of errors linked to this PixInfraction event
        - `:infraction` [PixInfraction]: PixInfraction entity to which the log refers to."
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixInfraction$Log])
    (:require [starkinfra.pix-infraction :as pix-infraction])
    (:use  [starkinfra.user]
            [clojure.walk]))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object),
            :created (.created java-object),
            :type (.type java-object),
            :errors (.errors java-object),
            :infraction (#'pix-infraction/java-to-clojure (.infraction java-object))
        }))

(defn- clojure-query-to-java 
    ([clojure-map]
        (let [{
            ids "ids"
            limit "limit"
            after "after"
            before "before"
            types "types"
            infraction-ids "infraction-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "ids" (if (nil? ids) nil (into-array String ids))
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "infractionIds" (if (nil? infraction-ids) nil (into-array String infraction-ids))
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
            infraction-ids "infraction-ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" cursor
                "limit" (if (nil? limit) nil (Integer. limit))
                "after" after
                "before" before
                "types" (if (nil? types) nil (into-array String types))
                "infractionIds" (if (nil? infraction-ids) nil (into-array String infraction-ids))
            }
        ))))

(defn get
    "Receive a single PixInfraction.Log object previously created by the Stark Infra API by its id
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixInfraction.Log object with updated attributes"
    ([id]
        (java-to-clojure
            (PixInfraction$Log/get id)))
            
    ([id, user]
        (java-to-clojure
            (PixInfraction$Log/get 
                id 
                (#'starkinfra.user/get-java-user user)))))

(defn query
    "Receive a generator of PixInfraction.Log objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:ids` [list of strings, default None]: Log ids to filter PixInfraction Logs. ex: [\"5656565656565656\"]
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. ex: [\"created\", \"failed\", \"delivering\", \"delivered\", \"closed\", \"canceled\"]
        - `:infraction-ids` [list of strings, default None]: list of PixInfraction IDs to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixInfraction.Log objects with updated attributes"

    ([]
        (map java-to-clojure (PixInfraction$Log/query)))

    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixInfraction$Log/query java-params)))
    
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixInfraction$Log/query 
            java-params 
            (#'starkinfra.user/get-java-user user)))))

(defn page
    "Receive a list of up to 100 PixInfraction.Log objects previously created in the Stark Infra API and the cursor to the next page.
    Use this function instead of query if you want to manually page your infractions.
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:ids` [list of strings, default None]: Log ids to filter PixInfraction Logs. ex: [\"5656565656565656\"]
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
        - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
        - `:types` [list of strings, default None]: filter retrieved objects by types. ex: [\"created\", \"failed\", \"delivering\", \"delivered\", \"closed\", \"canceled\"]
        - `:infraction-ids` [list of strings, default None]: list of PixInfraction ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixInfraction.Log objects with updated attributes
        - `:cursor` to retrieve the next page of PixInfraction.Log objects"
    ([]
        (def log-page (PixInfraction$Log/page))
        (def cursor (.cursor log-page))
        (def logs (map java-to-clojure (.logs log-page)))
        {:logs logs, :cursor cursor})
    
    ([params]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixInfraction$Log/page java-params))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)})
    
    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def log-page (PixInfraction$Log/page 
            java-params 
            (#'starkinfra.user/get-java-user user)))
        {:logs (map java-to-clojure (.logs log-page)), :cursor (.cursor log-page)}))
        