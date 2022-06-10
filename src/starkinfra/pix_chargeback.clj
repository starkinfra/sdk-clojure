(ns starkinfra.pix-chargeback
    "When you initialize a PixChargeback, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the created object.
    
    ## Parameters (required):
        - `:amount` [integer]: amount in cents to be reversed. ex: 11234 (= R$ 112.34)
        - `:reference-id` [string]: end_to_end_id or return_id of the transaction to be reversed. ex: \"E20018183202201201450u34sDGd19lz\"
        - `:reason` [string]: reason why the reversal was requested. Options: \"fraud\", \"flaw\", \"reversalChargeback\"
    
    ## Parameters (optional):
        - `:description` [string, default None]: description for the PixChargeback.
    
    ## Attributes (return-only):
        - `:analysis` [string]: analysis that led to the result.
        - `:bacen-id` [string]: central bank's unique UUID that identifies the PixChargeback.
        - `:sender-bank-code` [string]: bank_code of the Pix participant that created the PixChargeback. ex: \"20018183\"
        - `:receiver-bank-code` [string]: bank_code of the Pix participant that received the PixChargeback. ex: \"20018183\"
        - `:rejection-reason` [string]: reason for the rejection of the pix chargeback. Options: \"noBalance\", \"accountClosed\", \"unableToReverse\"
        - `:reversal-reference-id` [string]: return id of the reversal transaction. ex: \"D20018183202202030109X3OoBHG74wo\".
        - `:id` [string]: unique id returned when the PixChargeback is created. ex: \"5656565656565656\"
        - `:result` [string]: result after the analysis of the PixChargeback by the receiving party. Options: \"rejected\", \"accepted\", \"partiallyAccepted\"
        - `:status` [string]: current PixChargeback status. Options: \"created\", \"failed\", \"delivered\", \"closed\", \"canceled\".
        - `:created` [datetime.datetime]: creation datetime for the PixChargeback. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:updated` [datetime.datetime]: latest update datetime for the PixChargeback. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixChargeback])
    (:use [starkinfra.user]
          [clojure.walk]))

    (defn- clojure-to-java
        ([clojure-map]
            (let [{
                amount "amount"
                reference-id "reference-id"
                reason "reason"
                description "description"}
                (stringify-keys clojure-map)]

            (defn- apply-java-hashmap [x] (java.util.HashMap x))

            (PixChargeback. (java.util.HashMap.
                {
                    "amount" amount
                    "referenceId" reference-id
                    "reason" reason
                    "description" description
                })))))

    (defn- java-to-clojure
        ([java-object]
            {:id (.id java-object)
             :amount (.amount java-object)
             :reference-id (.referenceId java-object)
             :reason (.reason java-object)
             :description (.description java-object)
             :created (.created java-object)
             :- `:updated` (.- `:updated` java-object)
             :analysis (.analysis java-object)
             :bacen-id (.bacenId java-object)
             :sender-bank-code (.senderBankCode java-object)
             :receiver-bank-code (.receiverBankCode java-object)
             :rejection-reason (.rejectionReason java-object)
             :reversal-reference-id (.reversalReferenceId java-object)
             :result (.result java-object)
             :status (.status java-object)}))
    
    (defn- clojure-query-to-java
        ([clojure-map]
            (let [{
                limit "limit"
                after "after"
                before "before"
                status "status"
                ids "ids"
            } (stringify-keys clojure-map)]
            (java.util.HashMap.
                {
                    "limit" (if (nil? limit) nil (Integer. limit))
                    "after" after
                    "before" before
                    "status" status
                    "ids" (if (nil? ids) nil (into-array String ids))
                }))))

    (defn- clojure-page-to-java
        ([clojure-map]
            (let [{
                limit "limit"
                after "after"
                before "before"
                status "status"
                ids "ids"
            } (stringify-keys clojure-map)]
            (java.util.HashMap.
                {
                    "limit" (if (nil? limit) nil (Integer. limit))
                    "after" after
                    "before" before
                    "status" status
                    "ids" (if (nil? ids) nil (into-array String ids))
                }))))

    (defn- clojure-update-to-java
        ([params]
            (let [{
                rejection-reason "rejection-reason"
                reversal-reference-id "reversal-reference-id"
                analysis "analysis"
            } (stringify-keys params)]
            (java.util.HashMap.
                {
                    "rejectionReason" rejection-reason
                    "reversalReferenceId" reversal-reference-id
                    "analysis" analysis
                }))))

    (defn create
        "Create PixChargebacks in the Stark Infra API

        ## Parameters (optional):
            - `:chargebacks` [list of PixChargeback]: list of PixChargeback objects to be created in the API.

        ## Parameters (optional):
            - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
        
        ## Return:
            - list of PixChargeback objects with updated attributes"
        ([chargebacks]
            (def java-chargebacks (map clojure-to-java chargebacks))
            (def created-java-chargebacks (PixChargeback/create java-chargebacks))
            (map java-to-clojure created-java-chargebacks))

        ([chargebacks, user]
            (def java-chargebacks (map clojure-to-java chargebacks))
            (def created-java-chargebacks (PixChargeback/create java-chargebacks (#'starkinfra.user/get-java-user user)))
            (map java-to-clojure created-java-chargebacks)))

    (defn get
        "Retrieve the PixChargeback object linked to your Workspace in the Stark Infra API using its id.
        
        ## Parameters (required):
            - `:id` [string]: object unique id. ex: \"5656565656565656\".
        
        ## Parameters (optional):
            - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
        
        ## Return:
            - PixChargeback object that corresponds to the given id."
        ([id]
            (java-to-clojure
            (PixChargeback/get id)))

        ([id, user]
            (java-to-clojure
                (PixChargeback/get
                    id
                    (#'starkinfra.user/get-java-user user)))))

    (defn query
        "Receive a generator of PixChargebacks objects previously created in the Stark Infra API
        
        ## Parameters (optional):
            - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
            - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
            - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
            - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
            - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
            - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
        
        ## Return:
            - generator of PixChargeback objects with updated attributes"
        ([]
            (map java-to-clojure (PixChargeback/query)))
        
        ([params]
            (def java-params (clojure-query-to-java params))
            (map java-to-clojure (PixChargeback/query java-params)))

        ([params, user]
            (def java-params (clojure-query-to-java params))
            (map java-to-clojure (PixChargeback/query java-params (#'starkinfra.user/get-java-user user)))))

    (defn page
        "Receive a generator of PixChargebacks objects previously created in the Stark Infra API
        
        ## Parameters (optional):
            - `:cursor` [string, default None]: cursor returned on the previous page function call.
            - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
            - `:after` [datetime.date or string, default None]: date filter for objects created after a specified date. ex: datetime.date(2020, 3, 10)
            - `:before` [datetime.date or string, default None]: date filter for objects created before a specified date. ex: datetime.date(2020, 3, 10)
            - `:status` [list of strings, default None]: filter for status of retrieved objects. ex: [\"created\", \"failed\", \"delivered\", \"closed\", \"canceled\"]
            - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
            - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
        
        ## Return:
            - `:cursor` to retrieve the next page of PixChargeback objects
            - generator of PixChargeback objects with updated attributes"
        ([]
            (def chargebacks-page (PixChargeback/page))
            (def cursor (.cursor chargebacks-page))
            (def chargebacks (map java-to-clojure (.chargebacks chargebacks-page)))
            {:chargebacks chargebacks, :cursor cursor})

        ([params]
            (def java-params (clojure-page-to-java params))
            (def chargebacks-page (PixChargeback/page java-params))
            {:chargebacks (map java-to-clojure (.chargebacks chargebacks-page)), :cursor (.cursor chargebacks-page)})

        ([params, user]
            (def java-params (clojure-page-to-java params))
            (def chargebacks-page (PixChargeback/page java-params (#'starkinfra.user/get-java-user user)))
            {:chargebacks (map java-to-clojure (.chargebacks chargebacks-page)), :cursor (.cursor chargebacks-page)}))
            
    (defn update
    "Respond to a received PixChargeback.
    
    ## Parameters (required):
        - id [string]: PixChargeback id. ex: '5656565656565656'
        - result [string]: result after the analysis of the PixChargeback. Options: \"rejected\", \"accepted\", \"partiallyAccepted\".
    
    ## Parameters (conditionally required):
        - rejection_reason [string, default None]: if the PixChargeback is rejected a reason is required. Options: \"noBalance\", \"accountClosed\", \"unableToReverse\",
        - reversal_reference_id [string, default None]: return_id of the reversal transaction. ex: \"D20018183202201201450u34sDGd19lz\"
    
    ## Parameters (optional):
        - analysis [string, default None]: description of the analysis that led to the result.
    
    ## Return:
        - PixChargeback with updated attributes"
        ([id, result]
            (java-to-clojure
            (PixChargeback/update id result)))
        
        [id, result, params]
            (def java-params (clojure-update-to-java params))
            (java-to-clojure
                (PixChargeback/update id result java-params))
                
        ([id, result, params, user]
            (def java-params (clojure-update-to-java params))
            (java-to-clojure
                (PixChargeback/update id result java-params (#'starkinfra.user/get-java-user user)))))

    (defn cancel
        "Cancel a PixChargeback entity previously created in the Stark Infra API
        
        ## Parameters (required):
            - id [string]: object unique id. ex: \"5656565656565656\"
        
        ## Parameters (optional):
            - user [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
        
        ## Return:
            - canceled PixChargeback object"
            
        ([id]
            (java-to-clojure
                (PixChargeback/cancel id)))
        
        ([id, user]
            (java-to-clojure
                (PixChargeback/cancel id (#'starkinfra.user/get-java-user user)))))