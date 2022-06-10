(ns starkinfra.pix-director
    "Mandatory data that must be registered within the Central Bank for emergency contact purposes.
    When you initialize a PixDirector, the entity will not be automatically
    created in the Stark Infra API. The 'create' function sends the objects
    to the Stark Infra API and returns the list of created objects.

    ## Parameters (required):
        - `:name` [string]: name of the PixDirector. ex: \"Edward Stark\".
        - `:tax-id` [string]: tax ID (CPF) of the PixDirector. ex: \"012.345.678-90\"
        - `:phone` [string]: phone of the PixDirector. ex: \"+551198989898\"
        - `:email` [string]: email of the PixDirector. ex: \"ned.stark@starkbank.com\"
        - `:password` [string]: password of the PixDirector. ex: \"12345678\"
        - `:team-email` [string]: team email. ex: \"pix.team@company.com\"
        - `:team-phones` [list of strings]: list of phones of the team. ex: [\"+5511988889999\", \"+5511988889998\"]
   
    ## Attributes (return-only):
        - `:id` [string]: unique id returned when the PixDirector is created. ex: \"5656565656565656\"
        - `:status` [string]: current PixDirector status. ex: \"success\""
    (:refer-clojure :exclude [get set])
    (:import [com.starkinfra PixDirector])
    (:use [starkinfra.user]
          [clojure.walk]))

    (defn- clojure-to-java
        ([clojure-map]
            (let [{
                name "name"
                tax-id "tax-id"
                phone "phone"
                email "email"
                password "password"
                team-email "team-email"
                team-phones "team-phones"
            }
            (stringify-keys clojure-map)]
            
            (defn- apply-java-hashmap [x] (java.util.HashMap x))
            
            (PixDirector. (java.util.HashMap.
                {
                    "name" name
                    "taxId" tax-id
                    "phone" phone
                    "email" email
                    "password" password
                    "teamEmail" team-email
                    "teamPhones" (apply-java-hashmap team-phones)
                }
            )))))

(defn- java-to-clojure
    ([java-object]
        {
            :id (.id java-object)
            :status (.status java-object)
            :name (.name java-object)
            :tax-id (.taxId java-object)
            :phone (.phone java-object)
            :email (.email java-object)
            :password (.password java-object)
            :team-email (.teamEmail java-object)
            :team-phones (.teamPhones java-object)
        }))

(defn create
    "Send a PixDirector object for creation in the Stark Infra API
    
    ## Parameters (required):
        - `:director` [list of PixDirector Object]: list of PixDirector objects to be created in the API
    
    ## Parameters (optional):
        - `:user` [Organization/Project object, default None]: Organization or Project object. Not necessary if starkinfra.user was set before function call
    
    ## Return:
        - PixDirector objects with updated attributes"
    ([directors]
        (def java-directors (map clojure-to-java directors))
        (def created-java-directors (PixDirector/create java-directors))
        (map java-to-clojure created-java-directors))

    ([directors user]
        (def java-directors (map clojure-to-java directors))
        (def created-java-directors (PixDirector/create java-directors (#'starkinfra.user/get-java-user user)))
        (map java-to-clojure created-java-directors)))
        