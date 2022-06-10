(ns starkinfra.pix-statement
    "The PixStatement object stores information about all the transactions that
    happened on a specific day at your settlment account according to the Central Bank.
    It must be created by the user before it can be accessed.
    This feature is only available for direct participants.
    When you initialize a PixStatement, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the created object.
    
    ## Parameters (required):
        - `:after` [datetime.date]: transactions that happened at this date are stored in the PixStatement, must be the same as before. ex: (2022-01-01)
        - `:before` [datetime.date]: transactions that happened at this date are stored in the PixStatement, must be the same as after. ex: (2022-01-01)
        - `:type` [string]: types of entities to include in statement. Options: [\"interchange\", \"interchangeTotal\", \"transaction\"]
    
    ## Attributes (return-only):
        - `:id` [string]: unique id returned when the PixStatement is created. ex: \"5656565656565656\"
        - `:status` [string]: current PixStatement status. ex: [\"success\", \"failed\"]
        - `:transaction-count` [integer]: number of transactions that happened during the day that the PixStatement was requested. ex: 11
        - `:created` [datetime.datetime]: creation datetime for the PixStatement. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)
        - `:updated` [datetime.datetime]: latest update datetime for the PixStatement. ex: datetime.datetime(2020, 3, 10, 10, 30, 0, 0)"
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixStatement])
    (:use [starkinfra.user]
          [clojure.walk]))

(defn- clojure-to-java
    ([clojure-map]
        (let [{
            after "after"
            before "before"
            type "type"
        }
        (stringify-keys clojure-map)]
            
            (defn- apply-java-hashmap [x] (java.util.HashMap x))
            
            (PixStatement. (java.util.HashMap.
                {
                    "after" after
                    "before" before
                    "type" type
                }
            )))))

(defn- java-to-clojure 
    ([java-object]
        {
            :id (.id java-object)
            :status (.status java-object)
            :transaction-count (.transactionCount java-object)
            :created (.created java-object)
            :- `:updated` (.- `:updated` java-object)
            :after (.after java-object)
            :before (.before java-object)
            :type (.type java-object)
        }))

(defn- clojure-query-to-java
    ([clojure-map]
        (let [{
            limit "limit"
            ids "ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "limit" (if (nil? limit) nil limit)
                "ids" (if (nil? ids) nil (into-array String ids))
            }))))

(defn- clojure-page-to-java
    ([clojure-map]
        (let [{
            cursor "cursor"
            limit "limit"
            ids "ids"
        } (stringify-keys clojure-map)]
        (java.util.HashMap.
            {
                "cursor" cursor
                "limit" (if (nil? limit) nil limit)
                "ids" (if (nil? ids) nil (into-array String ids))
            }
        ))))

(defn create
    "Create a PixStatement linked to your Workspace in the Stark Infra API
   
    ## Parameters (optional):
        - `:statement` [PixStatement object]: PixStatement object to be created in the API.
   
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
   
    ## Return:
        - PixStatement object with updated attributes."
    
    ([statements]
        (def java-statements (clojure-to-java statements))
        (def created-java-statements (PixStatement/create java-statements))
        (map java-to-clojure created-java-statements))
        
    ([statements, user]
        (def java-statements (clojure-to-java statements))
        (def created-java-statements (PixStatement/create java-statements (#'starkinfra.user/get-java-user user)))
        (map java-to-clojure created-java-statements)))

(defn get
    "Retrieve the PixStatement object linked to your Workspace in the Stark Infra API by its id.
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixStatement object that corresponds to the given id."
    ([id]
        (java-to-clojure (PixStatement/get id)))
        
    ([id, user]
        (java-to-clojure 
            (PixStatement/get
                id
                (#'starkinfra.user/get-java-user user)
            ))))

(defn query
    "Receive a generator of PixStatements objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - generator of PixStatement objects with updated attributes"
    ([]
        (map java-to-clojure (PixStatement/query)))
        
    ([params]
        (def java-params (clojure-query-to-java params))
        (map java-to-clojure (PixStatement/query java-params)))
        
    ([params, user]
        (def java-params (clojure-query-to-java params))
        (def java-to-clojure
            (PixStatement/query java-params
                (#'starkinfra.user/get-java-user user)
            ))))

(defn page
    "Receive a list of up to 100 PixStatements objects previously created in the Stark Infra API
    
    ## Parameters (optional):
        - `:cursor` [string, default None]: cursor returned on the previous page function call
        - `:limit` [integer, default 100]: maximum number of objects to be retrieved. Max = 100. ex: 35
        - `:ids` [list of strings, default None]: list of ids to filter retrieved objects. ex: [\"5656565656565656\", \"4545454545454545\"]
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - list of PixStatement objects with updated attributes
        - `:cursor` to retrieve the next page of PixStatement objects"
    ([]
        (def statement-page (PixStatement/page))
        (def cursor (.cursor statement-page))
        (def statements (map java-to-clojure (.statements statement-page)))
        {:statements statements, :cursor cursor})
        
    ([params]
        (def java-params (clojure-page-to-java params))
        (def statement-page (PixStatement/page java-params))
        {:statements (map java-to-clojure (.statements statement-page)), :cursor (.cursor statement-page)})
        
    ([params, user]
        (def java-params (clojure-page-to-java params))
        (def statement-page (PixStatement/page java-params (#'starkinfra.user/get-java-user user)))
        {:statements (map java-to-clojure (.statements statement-page)), :cursor (.cursor statement-page)}))

(defn csv
    "Retrieve a specific PixStatement by its ID in a .csv file.
    
    ## Parameters (required):
        - `:id` [string]: object unique id. ex: \"5656565656565656\"
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - .zip file containing a PixStatement in .csv format"
    ([id]
        (clojure.java.io/input-stream (PixStatement/csv id)))
        
    ([id, user]
        (clojure.java.io/input-stream 
            (PixStatement/csv
                id
                (#'starkinfra.user/get-java-user user)
            ))))