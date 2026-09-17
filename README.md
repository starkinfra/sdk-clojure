# Stark Infra Clojure SDK

Welcome to the Stark Infra Clojure SDK! This tool is made for Clojure
developers who want to easily integrate with our API.
This SDK version is compatible with the Stark Infra API v2.

# Introduction

# Index

- [Introduction](#introduction)
    - [Supported Clojure versions](#supported-clojure-versions)
    - [API documentation](#stark-infra-api-documentation)
    - [Versioning](#versioning)
- [Setup](#setup)
    - [Install our SDK](#1-install-our-sdk)
    - [Create your Private and Public Keys](#2-create-your-private-and-public-keys)
    - [Register your user credentials](#3-register-your-user-credentials)
    - [Setting up the user](#4-setting-up-the-user)
    - [Setting up the error language](#5-setting-up-the-error-language)
- [Resource listing and manual pagination](#resource-listing-and-manual-pagination)
- [Testing in Sandbox](#testing-in-sandbox)
- [Usage](#usage)
    - [Issuing](#issuing)
<!-- @@INDEX:D@@ -->
<!-- @@INDEX:E@@ -->
    - [Pix](#pix)
        - [PixRequests](#create-pixrequests): Create Pix transactions
        - [PixBalance](#get-your-pixbalance): View your account balance
        - [PixKey](#create-a-pixkey): Create a Pix Key
<!-- @@INDEX:A@@ -->
<!-- @@INDEX:B@@ -->
<!-- @@INDEX:C@@ -->
    - [Ledger](#ledger)
<!-- @@INDEX:F-ledger@@ -->
    - [Lending](#lending)
<!-- @@INDEX:F-lending@@ -->
    - [Identity](#identity)
<!-- @@INDEX:G@@ -->
    - [Webhook](#webhook):
        - [Webhook](#create-a-webhook-subscription): Configure your webhook endpoints and subscriptions
        - [WebhookEvents](#process-webhook-events): Manage Webhook events
        - [WebhookEventAttempts](#query-failed-webhook-event-delivery-attempts-information): Query failed webhook event deliveries
    - [Request](#request): Send a custom request to Stark Infra. This can be used to access features that haven't been mapped yet.
- [Handling errors](#handling-errors)
- [Help and Feedback](#help-and-feedback)

# Supported Clojure Versions

This library supports Clojure versions 1.10+.

# Stark Infra API documentation

Feel free to take a look at our [API docs](https://www.starkinfra.com/docs/api).

# Versioning

This project adheres to the following versioning pattern:

Given a version number MAJOR.MINOR.PATCH, increment:

- MAJOR version when the **API** version is incremented. This may include backwards incompatible changes;
- MINOR version when **breaking changes** are introduced OR **new functionalities** are added in a backwards compatible manner;
- PATCH version when backwards compatible bug **fixes** are implemented.

# Setup

## 1. Install our SDK

1.1 Using Leiningen/Boot:
```clojure
[starkinfra/sdk "0.1.0"]
```

1.2 Using Clojure CLI/deps.edn:
```clojure
starkinfra/sdk {:mvn/version "0.1.0"}
```

1.3 Using Gradle:
```groovy
implementation 'starkinfra:sdk:0.1.0'
```

1.4 Using Maven:
```xml
<dependency>
  <groupId>starkinfra</groupId>
  <artifactId>sdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

## 2. Create your Private and Public Keys

We use ECDSA. That means you need to generate a secp256k1 private
key to sign your requests to our API, and register your public key
with us, so we can validate those requests.

You can use one of the following methods:

2.1. Check out the options in our [tutorial](https://starkbank.com/faq/how-to-create-ecdsa-keys).

2.2. Use our SDK:

```clojure
(ns my-lib.core
  (:require [starkinfra.key :as key]))

(def key-pair (key/create))
(def private-pem (:private-pem key-pair))
(def public-pem (:public-pem key-pair))

;or, to also save .pem files in a specific path
(def key-pair (key/create "file/keys/"))
(def private-pem (:private-pem key-pair))
(def public-pem (:public-pem key-pair))
```

**NOTE**: When you are creating new credentials, it is recommended that you create the
keys inside the infrastructure that will use it, in order to avoid risky internet
transmissions of your **private-key**. Then you can export the **public-key** alone to the
computer where it will be used in the new Project creation.

## 3. Register your user credentials

You can interact directly with our API using two types of users: Projects and Organizations.

- **Projects** are workspace-specific users, that is, they are bound to the workspaces they are created in.
One workspace can have multiple Projects.
- **Organizations** are general users that control your entire organization.
They can control all your Workspaces and even create new ones. The Organization is bound to your company's tax ID only.
Since this user is unique in your entire organization, only one credential can be linked to it.

3.1. To create a Project in Sandbox:

3.1.1. Log into [StarkInfra Sandbox](https://web.sandbox.starkinfra.com)

3.1.2. Go to Menu > Integrations

3.1.3. Click on the "New Project" button

3.1.4. Create a Project: Give it a name and upload the public key you created in section 2

3.1.5. After creating the Project, get its Project ID

3.1.6. Use the Project ID and private key to create the map below:

```clojure
(ns my-lib.core
  (:require [starkinfra.user :as user]))

;Get your private key from an environment variable or an encrypted database.
;This is only an example of a private key content. You should use your own key.
(def private-key-content "
-----BEGIN EC PARAMETERS-----
BgUrgQQACg==
-----END EC PARAMETERS-----
-----BEGIN EC PRIVATE KEY-----
MHQCAQEEIMCwW74H6egQkTiz87WDvLNm7fK/cA+ctA2vg/bbHx3woAcGBSuBBAAK
oUQDQgAE0iaeEHEgr3oTbCfh8U2L+r7zoaeOX964xaAnND5jATGpD/tHec6Oe9U1
IF16ZoTVt1FzZ8WkYQ3XomRD4HS13A==
-----END EC PRIVATE KEY-----
")

(def project (user/project
               "sandbox"
               "5656565656565656"
               private-key-content))
```

3.2. To create Organization credentials in Sandbox:

3.2.1. Log into [StarkInfra Sandbox](https://web.sandbox.starkinfra.com)

3.2.2. Go to Menu > Integrations

3.2.3. Click on the "Organization public key" button

3.2.4. Upload the public key you created in section 2 (only a legal representative of the organization can upload the public key)

3.2.5. Click on your profile picture and then on the "Organization" menu to get the Organization ID

3.2.6. Use the Organization ID and private key to create the map below:

```clojure
(ns my-lib.core
  (:require [starkinfra.user :as user]))

(def organization (user/organization
                    "sandbox"
                    "5656565656565656"
                    private-key-content
                    nil))  ;You only need to set the workspace-id when you are operating a specific workspace

;To dynamically use your organization credentials in a specific workspace-id,
;you can use the organization-replace function:
(starkinfra.pix-balance/get {} (user/organization-replace organization "4848484848484848"))
```

NOTE 1: Never hard-code your private key. Get it from an environment variable or an encrypted database.

NOTE 2: We support `"sandbox"` and `"production"` as environments.

NOTE 3: The credentials you registered in `sandbox` do not exist in `production` and vice versa.

## 4. Setting up the user

There are three kinds of users that can access our API: **Organization**, **Project** and **Member**.

- `Project` and `Organization` are designed for integrations and are the ones meant for our SDKs.
- `Member` is the one you use when you log into our webpage with your e-mail.

There are two ways to inform the user to the SDK:

4.1 Passing the user as the last argument in all functions:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-balance :as pix-balance]))

(def balance (pix-balance/get {} project))  ;or organization
```

4.2 Set it as a default user in the SDK:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-balance :as pix-balance]
            [starkinfra.settings :as settings]))

(settings/user project)  ;or organization

(def balance (pix-balance/get))
```

Just select the way of passing the user that is more convenient to you.
On all following examples, we will assume a default user has been set.

## 5. Setting up the error language

The error language can also be set in the same way as the default user:

```clojure
(ns my-lib.core
  (:require [starkinfra.settings :as settings]))

(settings/language "en-US")
```

Language options are "en-US" for English and "pt-BR" for Brazilian Portuguese. English is the default.

# Resource listing and manual pagination

Almost all SDK resources provide a `query` and a `page` function.

- The `query` function provides a straightforward way to efficiently iterate through all results that match the filters you inform,
seamlessly retrieving the next batch of elements from the API only when you reach the end of the current batch.
If you are not worried about data volume or processing time, this is the way to go. It returns a lazy sequence.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(doseq [request (pix-request/query {:limit 200})]
  (println request))
```

- The `page` function gives you full control over the API pagination. With each function call, you receive up to
100 results and the cursor to retrieve the next batch of elements. This allows you to stop your queries and
pick up from where you left off whenever it is convenient. When there are no more elements to be retrieved, the returned cursor will be `nil`.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(loop [cursor nil]
  (let [page (pix-request/page {:limit 50 :cursor cursor})]
    (doseq [request (:content page)]
      (println request))
    (when-not (empty? (:cursor page))
      (recur (:cursor page)))))
```

To simplify the following SDK examples, we will only use the `query` function, but feel free to use `page` instead.

# Testing in Sandbox

Your initial balance is zero. For many operations in Stark Infra, you'll need funds
in your account, which can be added to your balance by creating a starkbank Invoice.

In the Sandbox environment, most of the created starkbank Invoices will be automatically paid,
so there's nothing else you need to do to add funds to your account. Just create
a few starkbank Invoices and wait around a bit.

In Production, you (or one of your clients) will need to actually pay this Pix Request
for the value to be credited to your account.

# Usage

Here are a few examples on how to use the SDK. If you have any doubts, use the built-in
`doc` function to get more info on the desired functionality
(for example: `(doc starkinfra.pix-request/create)`)

## Issuing

<!-- @@USAGE:D@@ -->
<!-- @@USAGE:E@@ -->

## Pix

### Create PixRequests

You can create a Pix request to transfer money from one of your users to anyone else:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]))

(def requests (pix-request/create
                [{:amount 100  ;(R$ 1.00)
                  :external-id "141234121"  ;so we can block anything you send twice by mistake
                  :sender-branch-code "0000"
                  :sender-account-number "00000-0"
                  :sender-account-type "checking"
                  :sender-name "Tyrion Lannister"
                  :sender-tax-id "012.345.678-90"
                  :receiver-bank-code "00000001"
                  :receiver-branch-code "0001"
                  :receiver-account-number "00000-1"
                  :receiver-account-type "checking"
                  :receiver-name "Jamie Lannister"
                  :receiver-tax-id "45.987.245/0001-92"
                  :end-to-end-id (end-to-end-id/create "20018183")  ;pass your bank code to create an end to end ID
                  :description "For saving my life"
                  :reason "subscriptionFlaw"}
                 {:amount 200  ;(R$ 2.00)
                  :external-id "2135613462"
                  :sender-account-number "00000-0"
                  :sender-branch-code "0000"
                  :sender-account-type "checking"
                  :sender-name "Arya Stark"
                  :sender-tax-id "012.345.678-90"
                  :receiver-bank-code "00000001"
                  :receiver-account-number "00000-1"
                  :receiver-branch-code "0001"
                  :receiver-account-type "checking"
                  :receiver-name "John Snow"
                  :receiver-tax-id "012.345.678-90"
                  :end-to-end-id (end-to-end-id/create "20018183")
                  :tags ["Needle" "sword"]
                  :reason "subscriptionFlaw"}]))

(doseq [request requests]
  (println request))
```

### Query PixRequests

You can query multiple Pix requests according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(doseq [request (pix-request/query
                  {:limit 10
                   :after "2020-01-01"
                   :before "2020-04-01"
                   :status "success"
                   :tags ["iron" "suit"]
                   :end-to-end-ids ["E79457883202101262140HHX553UPqeq"]})]
  (println request))
```

### Get a PixRequest

After its creation, information on a Pix request may be retrieved by its id. Its status indicates whether it has been paid.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(def request (pix-request/get "5155165527080960"))

(println request)
```

### Process inbound PixRequest authorizations

It's easy to process authorization requests that arrived at your endpoint.
Remember to pass the signature header so the SDK can make sure it's Stark Infra that sent you the event.
If you do not approve or decline the authorization within 1 second, the authorization will be denied.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(def request (listen))  ;this is your handler to listen for authorization requests

(def pix-request (pix-request/parse
                   (:body request)
                   (get-in request [:headers "Digital-Signature"])))

(println pix-request)

(send-response  ;you should also implement this method
  (pix-request/response "approved"))  ;this optional function just helps you build the response JSON

;or

(send-response
  (pix-request/response "denied" "orderRejected"))
```

### Query PixRequest logs

You can query Pix request logs to better understand Pix request life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request.log :as log]))

(doseq [log (log/query {:limit 50 :after "2022-01-01" :before "2022-01-20"})]
  (println log))
```

### Get a PixRequest log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Get your PixBalance

To see how much money you have in your account, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-balance :as pix-balance]))

(def balance (pix-balance/get))

(println balance)
```

### Create a PixKey

You can create a Pix Key to link a bank account information to a key id:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key :as pix-key]))

(def key (pix-key/create
           {:account-created "2022-02-01T00:00:00+00:00"
            :account-number "00000"
            :account-type "savings"
            :branch-code "0000"
            :name "Jamie Lannister"
            :tax-id "012.345.678-90"
            :id "+5511989898989"}))

(println key)
```

### Query PixKeys

You can query multiple Pix keys you own according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key :as pix-key]))

(doseq [key (pix-key/query
              {:limit 1
               :after "2022-01-01"
               :before "2022-01-12"
               :status "registered"
               :tags ["iron" "bank"]
               :ids ["+5511989898989"]
               :type "phone"})]
  (println key))
```

### Get a PixKey

Information on a Pix key may be retrieved by its id.
An endToEndId must be informed so you can link any resulting purchases to this query,
avoiding sweep blocks by the Central Bank.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key :as pix-key]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]))

(def key (pix-key/get
           "5155165527080960"
           {:payer-id "012.345.678-90"
            :end-to-end-id (end-to-end-id/create "20018183")}))

(println key)
```

### Update a PixKey

Update the account information linked to a Pix Key.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key :as pix-key]))

(def key (pix-key/update
           "+5511989898989"
           {:reason "branchTransfer"
            :name "Jamie Lannister"}))

(println key)
```

### Cancel a PixKey

Cancel a specific Pix Key using its id. You can inform an optional reason for the requested action.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key :as pix-key]))

(def key (pix-key/cancel "5155165527080960" {:reason "fraud"}))

(println key)
```

### Query PixKey logs

You can query Pix key logs to better understand a Pix key life cycle.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key.log :as log]))

(doseq [log (log/query
              {:limit 50
               :ids ["5729405850615808"]
               :after "2022-01-01"
               :before "2022-01-20"
               :types ["created"]
               :key-ids ["+5511989898989"]})]
  (println log))
```

### Get a PixKey log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

<!-- @@USAGE:A@@ -->
<!-- @@USAGE:B@@ -->
<!-- @@USAGE:C@@ -->

## Ledger

<!-- @@USAGE:F-ledger@@ -->

## Lending

<!-- @@USAGE:F-lending@@ -->

## Identity

<!-- @@USAGE:G@@ -->

## Webhook

### Create a webhook subscription

To create a webhook subscription and be notified whenever an event occurs, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.webhook :as webhook]))

(def subscription (webhook/create
                    {:url "https://webhook.site/dd784f26-1d6a-4ca6-81cb-fda0267761ec"
                     :subscriptions ["credit-note"
                                     "business-identity"
                                     "issuing-card" "issuing-invoice" "issuing-purchase"
                                     "pix-request.in" "pix-request.out"
                                     "pix-reversal.in" "pix-reversal.out"
                                     "pix-claim" "pix-key" "pix-chargeback" "pix-infraction"]}))

(println subscription)
```

### Query webhook subscriptions

To search for registered webhook subscriptions, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.webhook :as webhook]))

(doseq [subscription (webhook/query)]
  (println subscription))
```

### Get a webhook subscription

You can get a specific webhook subscription by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.webhook :as webhook]))

(def subscription (webhook/get "1082736198236817"))

(println subscription)
```

### Delete a webhook subscription

You can also delete a specific webhook subscription by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.webhook :as webhook]))

(def subscription (webhook/delete "1082736198236817"))

(println subscription)
```

### Process webhook events

It's easy to process events delivered to your Webhook endpoint.
Remember to pass the signature header so the SDK can make sure it was Stark Infra that sent you the event.

```clojure
(ns my-lib.core
  (:require [clojure.string :as string]
            [starkinfra.event :as event]))

(def request (listen))  ;this is the method you made to get the events posted to your webhook endpoint

(def parsed (event/parse
              (:body request)
              (get-in request [:headers "Digital-Signature"])))

(def subscription (:subscription parsed))

(cond
  (string/includes? subscription "pix-request") (println (get-in parsed [:log :request]))
  (string/includes? subscription "pix-reversal") (println (get-in parsed [:log :reversal]))
  (string/includes? subscription "issuing-card") (println (get-in parsed [:log :card]))
  (string/includes? subscription "issuing-invoice") (println (get-in parsed [:log :invoice]))
  (string/includes? subscription "issuing-purchase") (println (get-in parsed [:log :purchase]))
  (string/includes? subscription "credit-note") (println (get-in parsed [:log :note]))
  (string/includes? subscription "business-identity") (println (get-in parsed [:log :identity])))
```

### Query webhook events

To search for webhook events, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.event :as event]))

(doseq [parsed (event/query {:after "2020-03-20" :is-delivered false})]
  (println parsed))
```

### Get a webhook event

You can get a specific webhook event by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.event :as event]))

(def parsed (event/get "1082736198236817"))

(println parsed)
```

### Delete a webhook event

You can also delete a specific webhook event by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.event :as event]))

(def parsed (event/delete "10827361982368179"))

(println parsed)
```

### Set webhook events as delivered

This can be used in case you've lost events.
With this function, you can manually set events retrieved from the API as
"delivered" to help future event queries with `:is-delivered` false.

```clojure
(ns my-lib.core
  (:require [starkinfra.event :as event]))

(def parsed (event/update "1298371982371921" {:is-delivered true}))

(println parsed)
```

### Query failed webhook event delivery attempts information

You can also get information on failed webhook event delivery attempts.

```clojure
(ns my-lib.core
  (:require [starkinfra.event.attempt :as attempt]))

(doseq [try-out (attempt/query {:after "2020-03-20"})]
  (println (:code try-out))
  (println (:message try-out)))
```

### Get a failed webhook event delivery attempt information

To retrieve information on a single attempt, use the following function:

```clojure
(ns my-lib.core
  (:require [starkinfra.event.attempt :as attempt]))

(def try-out (attempt/get "1616161616161616"))

(println try-out)
```

## Request

This resource allows you to send HTTP requests to Stark Infra routes.

Every function returns a map with `:status` and `:content`, and `:content` keys
are already converted to kebab-case keywords.

### GET

You can perform a GET request to any Stark Infra route.

It's possible to get a single resource using its id in the path.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def example-id "5155165527080960")
(def response (request/get (str "pix-request/" example-id)))

(println (:content response))
```

You can also get the specific resource log.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def example-id "5699165527090460")
(def response (request/get (str "pix-request/log/" example-id)))

(println (:content response))
```

This same function will be used to list all created items for the requested resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(loop [cursor nil]
  (let [response (request/get "pix-request" {:after "2024-01-01"
                                             :before "2024-02-01"
                                             :cursor cursor})
        next-cursor (get-in response [:content :cursor])]
    (println (get-in response [:content :requests]))
    (when-not (empty? next-cursor)
      (recur next-cursor))))
```

To list logs, you will use the same logic as for getting a single log.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def response (request/get "pix-request/log" {:after "2024-01-01" :before "2024-02-01"}))

(println (get-in response [:content :logs]))
```

### POST

You can perform a POST request to any Stark Infra route.

This will create an object for each item sent in your request.

**Note**: It's not possible to create multiple resource types simultaneously. You need to send separate requests if you want to create multiple resource types.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def response (request/post
                "issuing-holder"
                {:holders [{:name "Jaime Lannister"
                            :external-id "my_external_id"
                            :tax-id "012.345.678-90"}]}))

(println (:content response))
```

### PATCH

You can perform a PATCH request to any Stark Infra route.

It's possible to update a single item of a Stark Infra resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def example-id "5155165527080960")
(def response (request/patch (str "issuing-holder/" example-id) {:tags ["Arya" "Stark"]}))

(println (:content response))
```

### PUT

You can perform a PUT request to any Stark Infra route.

It's possible to create or overwrite a single item of a Stark Infra resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def response (request/put "issuing-rule" {:rules [{:name "Travel" :amount 200000 :interval "day"}]}))

(println (:content response))
```

### DELETE

You can perform a DELETE request to any Stark Infra route.

It's possible to delete a single item of a Stark Infra resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.request :as request]))

(def example-id "5155165527080960")
(def response (request/delete (str "issuing-holder/" example-id)))

(println (:content response))
```

# Handling errors

The SDK signals errors by throwing `clojure.lang.ExceptionInfo`. The `ex-data`
of the thrown exception carries the API's own error payload, so you can branch
on its `:code`.

Whenever the API detects an error in your request (status code 400), the
exception data holds the list of individual errors it found:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(try
  (pix-request/create [{:amount 100 :external-id "141234121"}])
  (catch clojure.lang.ExceptionInfo exception
    (doseq [error (:errors (ex-data exception))]
      (println (:code error))
      (println (:message error)))))
```

If the API runs into an internal error (status code 500), rest assured that the
development team is already rushing in to fix the mistake and get you back up to
speed.

`parse` throws an exception carrying `:code "invalidSignature"` when the
provided content and signature do not check out with the Stark Infra public key:

```clojure
(ns my-lib.core
  (:require [starkinfra.event :as event]))

(try
  (event/parse content signature)
  (catch clojure.lang.ExceptionInfo exception
    (when (= "invalidSignature" (:code (ex-data exception)))
      (println "this event did not come from Stark Infra"))))
```

The `starkinfra.request` functions are the exception: mirroring sdk-python, they
never throw on an API error and hand you the `:status` and `:content` of the
failed response instead.

# Help and Feedback

If you have any questions about our SDK, just send us an email.
We will respond you quickly, pinky promise. We are here to help you integrate with us ASAP.
We also love feedback, so don't be shy about sharing your thoughts with us.

Email: help@starkinfra.com
