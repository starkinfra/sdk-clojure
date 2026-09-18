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
        - [Products](#query-issuingproducts): View available sub-issuer card products (a.k.a. card number ranges or BINs)
        - [Holders](#create-issuingholders): Manage card holders
        - [Cards](#create-issuingcards): Create virtual and/or physical cards
        - [Design](#query-issuingdesigns): View your current card or package designs
        - [EmbossingKit](#query-issuingembossingkits): View your current embossing kits
        - [Stock](#query-issuingstocks): View your current stock of a certain IssuingDesign linked to an Embosser on the workspace
        - [Restock](#create-issuingrestocks): Create restock orders of a specific IssuingStock object
        - [StockRule](#create-issuingstockrules): Get notified when a specific IssuingStock reaches a minimum balance
        - [EmbossingRequest](#create-issuingembossingrequests): Create embossing requests
        - [TokenRequest](#create-an-issuingtokenrequest): Generate the payload to create the token
        - [Token](#process-token-authorizations): Authorize and manage your tokens
        - [TokenActivation](#process-token-activations): Get notified on how to inform the activation code to the holder
        - [TokenDesign](#get-an-issuingtokendesign): View your current token card arts
        - [Purchases](#process-purchase-authorizations): Authorize and view your past purchases
        - [Invoices](#create-issuinginvoices): Add money to your issuing balance
        - [Withdrawals](#create-issuingwithdrawals): Send money back to your Workspace from your issuing balance
        - [Balance](#get-your-issuingbalance): View your issuing balance
        - [Transactions](#query-issuingtransactions): View the transactions that have affected your issuing balance
        - [Enums](#issuing-enums): Query enums related to the issuing purchases, such as merchant categories, countries and card purchase methods
        - [Billing Invoices](#issuing-billinginvoice): View your current billing invoices
        - [Billing Transactions](#issuing-billingtransaction): View your current billing transactions
    - [Pix](#pix)
        - [PixRequests](#create-pixrequests): Create Pix transactions
        - [PixBalance](#get-your-pixbalance): View your account balance
        - [PixKey](#create-a-pixkey): Create a Pix Key
        - [PixReversals](#create-pixreversals): Reverse Pix transactions
        - [PixStatement](#create-a-pixstatement): Request your account statement
        - [PixKeyHolmes](#create-pixkeyholmes): Investigate the registration of a Pix Key in the DICT
        - [PixDirector](#create-a-pixdirector): Create a Pix Director
        - [PixDomain](#query-pixdomains): View registered SPI participants certificates
        - [PixClaim](#create-a-pixclaim): Claim a Pix Key
        - [PixInfraction](#create-pixinfractions): Create Pix Infraction reports
        - [PixFraud](#create-a-pixfraud): Create a Pix Fraud
        - [PixUser](#get-a-pixuser): Get fraud statistics of a user
        - [PixChargeback](#create-pixchargebacks): Create Pix Chargeback requests
        - [PixDispute](#create-pixdisputes): Create Pix Disputes
        - [PixInternalTransactionReport](#create-pixinternaltransactionreports): Report internal transactions to the Central Bank
        - [StaticBrcode](#create-staticbrcodes): Create static Pix BR codes
        - [DynamicBrcode](#create-dynamicbrcodes): Create dynamic Pix BR codes
        - [BrcodePreview](#create-brcodepreviews): Read data from BR Codes before paying them
        - [PixPullSubscription](#create-pixpullsubscriptions): Set up recurring Pix debit authorizations
        - [PixPullRequest](#create-pixpullrequests): Trigger automatic Pix debits against a subscription
    - [Ledger](#ledger)
        - [Ledger](#create-ledgers): Track the balance of a given amount
        - [LedgerTransaction](#create-ledgertransactions): Move amounts in and out of a Ledger
    - [Lending](#lending)
        - [CreditNote](#create-creditnotes): Create credit notes
        - [CreditPreview](#create-creditpreviews): Create credit previews
        - [CreditHolmes](#create-creditholmes): Create credit holmes debt verification
    - [Identity](#identity)
        - [IndividualIdentity](#create-individualidentities): Create individual identities
        - [IndividualAccountRequest](#create-individualaccountrequests): Create individual account requests
        - [IndividualAccountAttachment](#create-individualaccountattachments): Create individual account attachments
        - [BusinessIdentity](#create-businessidentities): Create business identities
        - [BusinessAttachment](#create-businessattachments): Create business attachments
        - [BusinessAccountRequest](#create-businessaccountrequests): Create business account requests
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

### Query IssuingProducts

To take a look at the sub-issuer card products available to you, just run the following:

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-product :as issuing-product]))

(def products (issuing-product/query))

(doseq [product products]
  (println product))
```

This will tell which card products and card number prefixes you have at your disposal.

### Create IssuingHolders

You can create card holders to which your cards will be bound.
They support spending rules that will apply to all underlying cards.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-holder :as issuing-holder]))

(def holders (issuing-holder/create
               [{:name "Iron Bank S.A."
                 :external-id "1234"
                 :tax-id "012.345.678-90"
                 :tags ["Traveler Employee"]
                 :rules [{:name "General USD"
                          :interval "day"
                          :amount 100000
                          :currency-code "USD"
                          :categories [{:type "services"}
                                       {:code "fastFoodRestaurants"}]
                          :countries [{:code "USA"}]
                          :methods [{:code "token"}]}]}]))

(doseq [holder holders]
  (println holder))
```

### Query IssuingHolders

You can query multiple holders according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-holder :as issuing-holder]))

(def holders (issuing-holder/query))

(doseq [holder holders]
  (println holder))
```

### Cancel an IssuingHolder

To cancel a single Issuing Holder by its id, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-holder :as issuing-holder]))

(def holder (issuing-holder/cancel "5155165527080960"))

(println holder)
```

### Get an IssuingHolder

To get a specific Issuing Holder by its id, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-holder :as issuing-holder]))

(def holder (issuing-holder/get "5155165527080960"))

(println holder)
```

### Query IssuingHolder logs

You can query IssuingHolder logs to better understand IssuingHolder life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-holder.log :as log]))

(def logs (log/query {:limit 50}))

(doseq [log logs]
  (println (:id log)))
```

### Get an IssuingHolder log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-holder.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create IssuingCards

You can issue cards with specific spending rules.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card :as issuing-card]))

(def cards (issuing-card/create
             [{:holder-name "Developers"
               :holder-tax-id "012.345.678-90"
               :holder-external-id "1234"
               :rules [{:name "general"
                        :interval "week"
                        :amount 50000
                        :currency-code "USD"
                        :categories [{:type "services"}
                                     {:code "fastFoodRestaurants"}]
                        :countries [{:code "BRA"}]
                        :methods [{:code "token"}]}]}]))

(doseq [card cards]
  (println card))
```

### Query IssuingCards

You can get a list of created cards given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card :as issuing-card]))

(def cards (issuing-card/query {:after "2020-01-01" :before "2020-03-01"}))

(doseq [card cards]
  (println card))
```

### Get an IssuingCard

After its creation, information on a card may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card :as issuing-card]))

(def card (issuing-card/get "5155165527080960"))

(println card)
```

### Update an IssuingCard

You can update a specific card by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card :as issuing-card]))

(def card (issuing-card/update "5155165527080960" {:status "blocked"}))

(println card)
```

### Cancel an IssuingCard

You can also cancel a card by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card :as issuing-card]))

(def card (issuing-card/cancel "5155165527080960"))

(println card)
```

### Query IssuingCard logs

Logs are pretty important to understand the life cycle of a card.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card.log :as log]))

(def logs (log/query {:limit 150}))

(doseq [log logs]
  (println log))
```

### Get an IssuingCard log

You can get a single log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-card.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Query IssuingDesigns

You can get a list of available designs given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-design :as issuing-design]))

(def designs (issuing-design/query {:limit 1}))

(doseq [design designs]
  (println design))
```

### Get an IssuingDesign

Information on a design may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-design :as issuing-design]))

(def design (issuing-design/get "5747368922185728"))

(println design)
```

### Query IssuingEmbossingKits

You can get a list of existing embossing kits given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-kit :as issuing-embossing-kit]))

(def kits (issuing-embossing-kit/query {:after "2022-11-01" :before "2022-12-01"}))

(doseq [kit kits]
  (println kit))
```

### Get an IssuingEmbossingKit

After its creation, information on an embossing kit may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-kit :as issuing-embossing-kit]))

(def kit (issuing-embossing-kit/get "5664445921492992"))

(println kit)
```

### Query IssuingStocks

You can get a list of available stocks given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock :as issuing-stock]))

(def stocks (issuing-stock/query {:after "2020-01-01" :before "2020-03-01"}))

(doseq [stock stocks]
  (println stock))
```

### Get an IssuingStock

Information on a stock may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock :as issuing-stock]))

(def stock (issuing-stock/get "5792731695677440"))

(println stock)
```

### Query IssuingStock logs

Logs are pretty important to understand the life cycle of a stock.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock.log :as log]))

(def logs (log/query {:limit 150}))

(doseq [log logs]
  (println log))
```

### Get an IssuingStock log

You can get a single log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock.log :as log]))

(def log (log/get "5809977331548160"))

(println log)
```

### Create IssuingRestocks

You can order restocks for a specific IssuingStock.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-restock :as issuing-restock]))

(def restocks (issuing-restock/create
                [{:count 100
                  :stock-id "5136459887542272"}]))

(doseq [restock restocks]
  (println restock))
```

### Query IssuingRestocks

You can get a list of created restocks given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-restock :as issuing-restock]))

(def restocks (issuing-restock/query {:after "2022-11-01" :before "2022-12-01"}))

(doseq [restock restocks]
  (println restock))
```

### Get an IssuingRestock

After its creation, information on a restock may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-restock :as issuing-restock]))

(def restock (issuing-restock/get "5664445921492992"))

(println restock)
```

### Query IssuingRestock logs

Logs are pretty important to understand the life cycle of a restock.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-restock.log :as log]))

(def logs (log/query {:limit 150}))

(doseq [log logs]
  (println log))
```

### Get an IssuingRestock log

You can get a single log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-restock.log :as log]))

(def log (log/get "6310318875607040"))

(println log)
```

### Create IssuingStockRules

You can create rules to be notified whenever a specific IssuingStock reaches a minimum balance.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock-rule :as issuing-stock-rule]))

(def rules (issuing-stock-rule/create
             [{:minimum-balance 10000
               :stock-id "5136459887542272"
               :emails ["john.doe@enterprise.com"]
               :phones ["+55 (11) 91234 5678"]}]))

(doseq [rule rules]
  (println rule))
```

### Query IssuingStockRules

You can get a list of created stock rules given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock-rule :as issuing-stock-rule]))

(def rules (issuing-stock-rule/query {:after "2022-11-01" :before "2022-12-01"}))

(doseq [rule rules]
  (println rule))
```

### Get an IssuingStockRule

After its creation, information on a stock rule may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock-rule :as issuing-stock-rule]))

(def rule (issuing-stock-rule/get "5664445921492992"))

(println rule)
```

### Update an IssuingStockRule

You can update a specific IssuingStockRule by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock-rule :as issuing-stock-rule]))

(def rule (issuing-stock-rule/update "5664445921492992" {:minimum-balance 20000}))

(println rule)
```

### Cancel an IssuingStockRule

You can also cancel a specific IssuingStockRule by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-stock-rule :as issuing-stock-rule]))

(def rule (issuing-stock-rule/cancel "5664445921492992"))

(println rule)
```

### Create IssuingEmbossingRequests

You can create a request to emboss a physical card.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-request :as issuing-embossing-request]))

(def embossing-requests (issuing-embossing-request/create
                           [{:kit-id "5648359658356736"
                             :card-id "5714424132272128"
                             :display-name-1 "Antonio Stark"
                             :shipping-city "Sao Paulo"
                             :shipping-country-code "BRA"
                             :shipping-district "Bela Vista"
                             :shipping-service "loggi"
                             :shipping-state-code "SP"
                             :shipping-street-line-1 "Av. Paulista, 200"
                             :shipping-street-line-2 "10 andar"
                             :shipping-tracking-number "My_custom_tracking_number"
                             :shipping-zip-code "12345-678"
                             :embosser-id "5746980898734080"}]))

(doseq [embossing-request embossing-requests]
  (println embossing-request))
```

### Query IssuingEmbossingRequests

You can get a list of created embossing requests given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-request :as issuing-embossing-request]))

(def embossing-requests (issuing-embossing-request/query {:after "2022-11-01" :before "2022-12-01"}))

(doseq [embossing-request embossing-requests]
  (println embossing-request))
```

### Get an IssuingEmbossingRequest

After its creation, information on an embossing request may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-request :as issuing-embossing-request]))

(def embossing-request (issuing-embossing-request/get "5191752558313472"))

(println embossing-request)
```

### Query IssuingEmbossingRequest logs

Logs are pretty important to understand the life cycle of an embossing request.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-request.log :as log]))

(def logs (log/query {:limit 150}))

(doseq [log logs]
  (println log))
```

### Get an IssuingEmbossingRequest log

You can get a single log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-embossing-request.log :as log]))

(def log (log/get "6724771005857792"))

(println log)
```
### Create an IssuingTokenRequest

You can create a request that provides the required data you must send to the wallet app.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token-request :as issuing-token-request]))

(def request (issuing-token-request/create
               {:card-id "5189831499972623"
                :wallet-id "google"
                :method-code "app"}))

(println request)
```

### Process Token authorizations

It's easy to process token authorizations delivered to your endpoint.
Remember to pass the signature header so the SDK can make sure it's Stark Infra that sent you the event.
If you do not approve or decline the authorization within 2 seconds, the authorization will be denied.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token :as issuing-token]))

(def request (listen))  ;this is your handler to listen for authorization requests

(def authorization (issuing-token/parse
                     (:body request)
                     (get-in request [:headers "Digital-Signature"])))

(send-response  ;you should also implement this method
  (issuing-token/response-authorization  ;this optional function just helps you build the response JSON
   "approved"
   {:activation-methods [{:type "app" :value "com.subissuer.android"}
                         {:type "text" :value "** *****-5678"}]
    :design-id "4584031664472031"
    :tags ["token" "user/1234"]}))

;or

(send-response
  (issuing-token/response-authorization
   "denied"
   {:reason "other"}))
```

### Process Token activations

It's easy to process token activation notifications delivered to your endpoint.
Remember to pass the signature header so the SDK can make sure it's Stark Infra that sent you the event.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token-activation :as issuing-token-activation]))

(def request (listen))  ;this is your handler to listen for activation requests

(def activation (issuing-token-activation/parse
                  (:body request)
                  (get-in request [:headers "Digital-Signature"])))
```

After that, you may generate the activation code and send it to the cardholder.
The cardholder enters the received code in the wallet app. We'll receive and send it to
your tokenAuthorizationUrl for your validation, completing the provisioning process.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token :as issuing-token]))

(def request (listen))  ;this is your handler to listen for authorization requests

(def activation (issuing-token/parse
                  (:body request)
                  (get-in request [:headers "Digital-Signature"])))

(send-response
  (issuing-token/response-activation  ;this optional function just helps you build the response JSON
   "approved"
   {:tags ["token" "user/1234"]}))

;or

(send-response
  (issuing-token/response-activation
   "denied"
   {:reason "other"
    :tags ["token" "user/1234"]}))
```

### Get an IssuingToken

You can get a single token by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token :as issuing-token]))

(def token (issuing-token/get "5749080709922816"))

(println token)
```

### Query IssuingTokens

You can get a list of created tokens given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token :as issuing-token]))

(doseq [token (issuing-token/query
               {:limit 5
                :after "2022-01-01"
                :before "2022-01-20"
                :status "active"
                :card-ids ["5656565656565656" "4545454545454545"]
                :external-ids ["DSHRMC00002626944b0e3b539d4d459281bdba90c2588791"
                               "DSHRMC00002626941c531164a0b14c66ad9602ee716f1e85"]})]
  (println token))
```

### Update an IssuingToken

You can update a specific token by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token :as issuing-token]))

(def token (issuing-token/update "5155165527080960" {:status "blocked"}))

(println token)
```

### Cancel an IssuingToken

You can also cancel a token by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token :as issuing-token]))

(def token (issuing-token/cancel "5155165527080960"))

(println token)
```

### Get an IssuingTokenDesign

You can get a single design by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token-design :as issuing-token-design]))

(def design (issuing-token-design/get "5749080709922816"))

(println design)
```

### Query IssuingTokenDesigns

You can get a list of available designs given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-token-design :as issuing-token-design]))

(doseq [design (issuing-token-design/query {:limit 5})]
  (println design))
```

### Get an IssuingTokenDesign PDF

A design PDF can be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [clojure.java.io :as io]
            [starkinfra.issuing-token-design :as issuing-token-design]))

(def pdf (issuing-token-design/pdf "5155165527080960"))

(io/copy pdf (io/file "design.pdf"))
```

### Process Purchase authorizations

It's easy to process purchase authorizations delivered to your endpoint.
Remember to pass the signature header so the SDK can make sure it's Stark Infra that sent you the event.
If you do not approve or decline the authorization within 2 seconds, the authorization will be denied.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-purchase :as issuing-purchase]))

(def request (listen))  ;this is your handler to listen for authorization requests

(def authorization (issuing-purchase/parse
                     (:body request)
                     (get-in request [:headers "Digital-Signature"])))

(send-response  ;you should also implement this method
  (issuing-purchase/response  ;this optional function just helps you build the response JSON
   "approved"
   {:amount (:amount authorization)
    :tags ["my-purchase-id/123"]}))

;or

(send-response
  (issuing-purchase/response
   "denied"
   {:reason "other"
    :tags ["other-id/456"]}))
```

### Query IssuingPurchases

You can get a list of created purchases given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-purchase :as issuing-purchase]))

(doseq [purchase (issuing-purchase/query {:after "2020-01-01" :before "2020-03-01"})]
  (println purchase))
```

### Get an IssuingPurchase

After its creation, information on a purchase may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-purchase :as issuing-purchase]))

(def purchase (issuing-purchase/get "5155165527080960"))

(println purchase)
```

### Update an IssuingPurchase

You can update a specific IssuingPurchase by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-purchase :as issuing-purchase]))

(def purchase (issuing-purchase/update
               "5155165527080960"
               {:description "Dinner" :tags ["customer-x" "reimbursement"]}))

(println purchase)
```

### Query IssuingPurchase logs

Logs are pretty important to understand the life cycle of a purchase.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-purchase.log :as log]))

(doseq [log (log/query {:limit 150})]
  (println log))
```

### Get an IssuingPurchase log

You can get a single log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-purchase.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create IssuingInvoices

You can create Pix invoices to transfer money from accounts you have in any bank to your Issuing balance,
allowing you to run your issuing operation.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-invoice :as issuing-invoice]))

(def invoice (issuing-invoice/create {:amount 1000}))

(println invoice)
```

### Get an IssuingInvoice

After its creation, information on an invoice may be retrieved by its id.
Its status indicates whether it's been paid.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-invoice :as issuing-invoice]))

(def invoice (issuing-invoice/get "5155165527080960"))

(println invoice)
```

### Query IssuingInvoices

You can get a list of created invoices given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-invoice :as issuing-invoice]))

(doseq [invoice (issuing-invoice/query {:after "2020-01-01" :before "2020-03-01"})]
  (println invoice))
```

### Query IssuingInvoice logs

Logs are pretty important to understand the life cycle of an invoice.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-invoice.log :as log]))

(doseq [log (log/query {:limit 150})]
  (println log))
```

### Get an IssuingInvoice log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-invoice.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create IssuingWithdrawals

You can create withdrawals to send cash back from your Issuing balance to your Banking balance
by using the IssuingWithdrawal resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-withdrawal :as issuing-withdrawal]))

(def withdrawal (issuing-withdrawal/create
                  {:amount 10000
                   :external-id "123"
                   :description "Sending back"}))

(println withdrawal)
```

### Get an IssuingWithdrawal

After its creation, information on a withdrawal may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-withdrawal :as issuing-withdrawal]))

(def withdrawal (issuing-withdrawal/get "5155165527080960"))

(println withdrawal)
```

### Query IssuingWithdrawals

You can get a list of created withdrawals given some filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-withdrawal :as issuing-withdrawal]))

(doseq [withdrawal (issuing-withdrawal/query {:after "2020-01-01" :before "2020-03-01"})]
  (println withdrawal))
```

### Get your IssuingBalance

To know how much money you have available to run authorizations, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-balance :as issuing-balance]))

(def balance (issuing-balance/get))

(println balance)
```

### Query IssuingTransactions

To understand your balance changes (issuing statement), you can query
transactions. Note that our system creates transactions for you when
you make purchases, withdrawals, receive issuing invoice payments, for example.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-transaction :as issuing-transaction]))

(doseq [transaction (issuing-transaction/query {:after "2020-01-01" :before "2020-03-01"})]
  (println transaction))
```

### Get an IssuingTransaction

You can get a specific transaction by its id:

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-transaction :as issuing-transaction]))

(def transaction (issuing-transaction/get "5155165527080960"))

(println transaction)
```

### Issuing Enums

#### Query MerchantCategories

You can query any merchant categories using this resource.
You may also use MerchantCategories to define specific category filters in IssuingRules.
Either codes (which represents specific MCCs) or types (code groups) will be accepted as filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.merchant-category :as merchant-category]))

(doseq [category (merchant-category/query {:search "food"})]
  (println category))
```

#### Query MerchantCountries

You can query any merchant countries using this resource.
You may also use MerchantCountries to define specific country filters in IssuingRules.

```clojure
(ns my-lib.core
  (:require [starkinfra.merchant-country :as merchant-country]))

(doseq [country (merchant-country/query {:search "brazil"})]
  (println country))
```

#### Query CardMethods

You can query available card methods using this resource.
You may also use CardMethods to define specific purchase method filters in IssuingRules.

```clojure
(ns my-lib.core
  (:require [starkinfra.card-method :as card-method]))

(doseq [method (card-method/query {:search "token"})]
  (println method))
```

### Query IssuingBillingInvoices

You can query multiples available Billing Invoices using this resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-billing-invoice :as issuing-billing-invoice]))

(doseq [billing-invoice (issuing-billing-invoice/query
                         {:after "2023-01-01" :before "2024-03-01" :limit 10})]
  (println billing-invoice))
```

### Get an IssuingBillingInvoice

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-billing-invoice :as issuing-billing-invoice]))

(def billing-invoice (issuing-billing-invoice/get "9302937674764487"))

(println billing-invoice)
```

### Query IssuingBillingTransactions

You can query multiples available Billing Transaction invoices using this resource.

```clojure
(ns my-lib.core
  (:require [starkinfra.issuing-billing-transaction :as issuing-billing-transaction]))

(doseq [billing-transaction (issuing-billing-transaction/query
                             {:after "2023-01-01" :before "2024-03-01" :limit 10})]
  (println billing-transaction))
```

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

### Create PixReversals

You can reverse a PixRequest either partially or totally using a PixReversal.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-reversal :as pix-reversal]))

(def reversals (pix-reversal/create
                 [{:amount 100
                   :end-to-end-id "E00000000202201060100rzsJzG9PzMg"
                   :external-id "17238435823958934"
                   :reason "bankError"}]))

(println reversals)
```

### Query PixReversals

You can query multiple Pix reversals according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-reversal :as pix-reversal]))

(def reversals (pix-reversal/query {:limit 10
                                    :after "2020-01-01"
                                    :before "2020-04-01"
                                    :status "success"
                                    :tags ["iron" "suit"]
                                    :return-ids ["D20018183202202030109X3OoBHG74wo"]}))

(doseq [reversal reversals]
  (println reversal))
```

### Get a PixReversal

After its creation, information on a Pix reversal may be retrieved by its id.
Its status indicates whether it has been successfully processed.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-reversal :as pix-reversal]))

(def reversal (pix-reversal/get "5155165527080960"))

(println reversal)
```

### Process inbound PixReversal authorizations

It's easy to process authorization requests that arrived at your endpoint.
Remember to pass the signature header so the SDK can make sure it's StarkInfra that sent you the event.
If you do not approve or decline the authorization within 1 second, the authorization will be denied.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-reversal :as pix-reversal]))

(def request (listen))  ;this is your handler to listen for authorization requests

(def reversal (pix-reversal/parse
                (:body request)
                (get-in request [:headers "Digital-Signature"])))

(println reversal)

(send-response  ;you should also implement this method
  (pix-reversal/response "approved"))  ;this optional function just helps you build the response JSON

;or

(send-response
  (pix-reversal/response "denied" "orderRejected"))
```

### Query PixReversal logs

You can query Pix reversal logs to better understand their life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-reversal.log :as log]))

(def logs (log/query {:limit 50
                      :after "2020-01-01"
                      :before "2020-01-20"}))

(doseq [entry logs]
  (println entry))
```

### Get a PixReversal log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-reversal.log :as log]))

(def entry (log/get "5155165527080960"))

(println entry)
```

### Create a PixStatement

Statements are generated directly by the Central Bank and are only available for direct participants.
To create a statement of all the transactions that happened on your account during a specific day, run:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-statement :as pix-statement]))

(def statement (pix-statement/create
                 {:after "2022-01-01" ; This is the date that you want to create a statement.
                  :before "2022-01-01" ; After and before must be the same date.
                  :type "transaction"})) ; Options are "interchange", "interchangeTotal", "transaction".

(println statement)
```

### Query PixStatements

You can query multiple Pix statements according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-statement :as pix-statement]))

(def statements (pix-statement/query {:limit 50}))

(doseq [statement statements]
  (println statement))
```

### Get a PixStatement

Statements are only available for direct participants. To get a Pix statement by its id:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-statement :as pix-statement]))

(def statement (pix-statement/get "5155165527080960"))

(println statement)
```

### Get a PixStatement .csv file

To get the .csv file corresponding to a Pix statement using its id, run:

```clojure
(ns my-lib.core
  (:require [clojure.java.io :as io]
            [starkinfra.pix-statement :as pix-statement]))

(def csv (pix-statement/csv "5155165527080960"))

(io/copy csv (io/file "test.zip"))
```

### Create PixKeyHolmes

To investigate whether a Pix Key is registered in the Central Bank's DICT,
open up a PixKeyHolmes for it:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key-holmes :as pix-key-holmes]))

(def holmes (pix-key-holmes/create
              [{:key-id "+5511989898989"}
               {:key-id "valid@sandbox.com"
                :tags ["sherlock"]}]))

(doseq [sherlock holmes]
  (println sherlock))
```

### Query PixKeyHolmes

You can query multiple PixKeyHolmes according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-key-holmes :as pix-key-holmes]))

(def holmes (pix-key-holmes/query {:after "2022-06-01"
                                   :before "2022-10-30"
                                   :status "solved"}))

(doseq [sherlock holmes]
  (println sherlock))
```

### Create a PixDirector

To register the Pix director contact information at the Central Bank, run the following:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-director :as pix-director]))

(def director (pix-director/create
                {:name "Edward Stark"
                 :tax-id "03.300.300/0001-00"
                 :phone "+5511999999999"
                 :email "ned.stark@company.com"
                 :password "12345678"
                 :team-email "pix.team@company.com"
                 :team-phones ["+5511988889999" "+5511988889998"]}))

(println director)
```

### Query PixDomains

Here you can list all Pix Domains registered at the Brazilian Central Bank. The Pix Domain object displays the domain
name and the QR Code domain certificates of registered Pix participants able to issue dynamic QR Codes.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-domain :as pix-domain]))

(def domains (pix-domain/query))

(doseq [domain domains]
  (println domain))
```
### Create a PixClaim

You can create a Pix claim to request the transfer of a Pix key from another bank to one of your accounts:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-claim :as pix-claim]))

(def claim (pix-claim/create
             {:account-created "2022-02-01T00:00:00+00:00"
              :account-number "5692908409716736"
              :account-type "checking"
              :branch-code "0000"
              :name "testKey"
              :tax-id "012.345.678-90"
              :key-id "+5511989898989"}))

(println claim)
```

### Query PixClaims

You can query multiple Pix claims according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-claim :as pix-claim]))

(def claims (pix-claim/query
              {:limit 1
               :after "2022-01-01"
               :before "2022-01-12"
               :status "registered"
               :ids ["5729405850615808"]
               :bacen-id "ccf9bd9c-e99d-999e-bab9-b999ca999f99"
               :type "ownership"
               :flow "out"
               :key-type "phone"
               :key-id "+5511989898989"}))

(doseq [claim claims]
  (println claim))
```

### Get a PixClaim

After its creation, information on a Pix claim may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-claim :as pix-claim]))

(def claim (pix-claim/get "5155165527080960"))

(println claim)
```

### Update a PixClaim

A Pix Claim can be confirmed or canceled by patching its status.
A received Pix Claim must be confirmed by the donor to be completed.
Ownership Pix Claims can only be canceled by the donor if the reason is "fraud".
A sent Pix Claim can also be canceled.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-claim :as pix-claim]))

(def claim (pix-claim/update "5155165527080960" {:status "confirmed"}))

(println claim)
```

### Query PixClaim logs

You can query Pix claim logs to better understand Pix claim life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-claim.log :as log]))

(def logs (log/query
            {:limit 50
             :ids ["5729405850615808"]
             :after "2022-01-01"
             :before "2022-01-20"
             :types ["registered"]
             :claim-ids ["5719405850615809"]}))

(doseq [log logs]
  (println log))
```

### Get a PixClaim log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-claim.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create PixInfractions

Pix Infraction reports are used to report transactions that raise fraud suspicion, to request a refund or to
reverse a refund. Infraction reports can be created by either participant of a transaction.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction :as pix-infraction]))

(def infractions (pix-infraction/create
                    [{:reference-id "E20018183202201201450u34sDGd19lz"
                      :type "reversal"
                      :method "scam"
                      :operator-email "fraud@company.com"
                      :operator-phone "+5511989898989"}]))

(doseq [infraction infractions]
  (println infraction))
```

### Query PixInfractions

You can query multiple infraction reports according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction :as pix-infraction]))

(def infractions (pix-infraction/query
                    {:limit 1
                     :after "2022-01-01"
                     :before "2022-01-12"
                     :status "delivered"
                     :ids ["5155165527080960"]
                     :bacen-id "ccf9bd9c-e99d-999e-bab9-b999ca999f99"}))

(doseq [infraction infractions]
  (println infraction))
```

### Get a PixInfraction

After its creation, information on a Pix Infraction may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction :as pix-infraction]))

(def infraction (pix-infraction/get "5155165527080960"))

(println infraction)
```

### Update a PixInfraction

A received Pix Infraction can be confirmed or declined by patching its status.
After a Pix Infraction is patched, its status changes to closed.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction :as pix-infraction]))

(def infraction (pix-infraction/update "5155165527080960" {:result "agreed"}))

(println infraction)
```

### Cancel a PixInfraction

Cancel a specific Pix Infraction using its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction :as pix-infraction]))

(def infraction (pix-infraction/cancel "5155165527080960"))

(println infraction)
```

### Query PixInfraction logs

You can query infraction report logs to better understand their life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction.log :as log]))

(def logs (log/query
            {:limit 50
             :ids ["5729405850615808"]
             :after "2022-01-01"
             :before "2022-01-20"
             :types ["created"]
             :infraction-ids ["5155165527080960"]}))

(doseq [log logs]
  (println log))
```

### Get a PixInfraction log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-infraction.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create a PixFraud

Pix Frauds can be created by either participant or automatically when a Pix Infraction is accepted.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-fraud :as pix-fraud]))

(def frauds (pix-fraud/create
              [{:external-id "my_external_id_1234"
                :type "mule"
                :tax-id "01234567890"}]))

(doseq [fraud frauds]
  (println fraud))
```

### Query Pix Frauds

You can query multiple Pix frauds according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-fraud :as pix-fraud]))

(def frauds (pix-fraud/query
              {:limit 1
               :after "2022-01-01"
               :before "2022-01-12"
               :status "created"
               :ids ["5155165527080960"]}))

(doseq [fraud frauds]
  (println fraud))
```

### Get a PixFraud

After its creation, information on a Pix Fraud may be retrieved by its ID.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-fraud :as pix-fraud]))

(def fraud (pix-fraud/get "5155165527080960"))

(println fraud)
```

### Cancel a PixFraud

Cancel a specific Pix Fraud using its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-fraud :as pix-fraud]))

(def fraud (pix-fraud/cancel "5155165527080960"))

(println fraud)
```

### Query PixFraud logs

You can query PixFraud logs to better understand their life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-fraud.log :as log]))

(def logs (log/query
            {:limit 50
             :after "2022-01-01"
             :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get a PixFraud log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-fraud.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Get a PixUser

You can get a specific fraud statistics of a user with his taxId.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-user :as pix-user]))

(def user (pix-user/get "01234567890"))

(println user)
```

### Create PixChargebacks

A Pix chargeback can be created when fraud is detected on a transaction or a system malfunction
results in an erroneous transaction.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback :as pix-chargeback]))

(def chargebacks (pix-chargeback/create
                    [{:amount 100
                      :reference-id "E20018183202201201450u34sDGd19lz"
                      :reason "fraud"}]))

(doseq [chargeback chargebacks]
  (println chargeback))
```

### Query PixChargebacks

You can query multiple Pix chargebacks according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback :as pix-chargeback]))

(def chargebacks (pix-chargeback/query
                    {:limit 1
                     :after "2022-01-01"
                     :before "2022-01-12"
                     :status "registered"
                     :ids ["5155165527080960"]
                     :bacen-id "ccf9bd9c-e99d-999e-bab9-b999ca999f99"}))

(doseq [chargeback chargebacks]
  (println chargeback))
```

### Get a PixChargeback

After its creation, information on a Pix Chargeback may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback :as pix-chargeback]))

(def chargeback (pix-chargeback/get "5155165527080960"))

(println chargeback)
```

### Update a PixChargeback

A received Pix Chargeback can be accepted or rejected by patching its status.
After a Pix Chargeback is patched, its status changes to closed.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback :as pix-chargeback]
            [starkinfra.utils.return-id :as return-id]))

(def chargeback (pix-chargeback/update "5155165527080960"
                   {:result "accepted"
                    :reversal-reference-id (return-id/create "20018183")}))

(println chargeback)
```

### Cancel a PixChargeback

Cancel a specific Pix Chargeback using its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback :as pix-chargeback]))

(def chargeback (pix-chargeback/cancel "5155165527080960"))

(println chargeback)
```

### Query PixChargeback logs

You can query Pix chargeback logs to better understand Pix chargeback life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback.log :as log]))

(def logs (log/query
            {:limit 50
             :ids ["5729405850615808"]
             :after "2022-01-01"
             :before "2022-01-20"
             :types ["created"]
             :chargeback-ids ["5155165527080960"]}))

(doseq [log logs]
  (println log))
```

### Get a PixChargeback log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-chargeback.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create PixDisputes

Pix disputes can be created when a fraud is detected creating a chain of transactions in order to reverse the funds to the origin.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-dispute :as pix-dispute]))

(def disputes (pix-dispute/create
                 [{:reference-id "E20018183202512191914WcfANNEIYnt"
                   :method "scam"
                   :operator-phone "+5511999999999"
                   :operator-email "operator@example.com"}]))

(doseq [dispute disputes]
  (println dispute))
```

### Query PixDisputes

You can query multiple PixDisputes according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-dispute :as pix-dispute]))

(def disputes (pix-dispute/query
                 {:limit 10
                  :after "2020-01-01"
                  :before "2020-04-01"
                  :status "success"
                  :tags ["iron" "suit"]}))

(doseq [dispute disputes]
  (println dispute))
```

### Get a PixDispute

After its creation, information on a PixDispute may be retrieved by its id. Its status indicates whether it has been paid.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-dispute :as pix-dispute]))

(def dispute (pix-dispute/get "5155165527080960"))

(println dispute)
```

### Cancel a PixDispute

Cancel a specific PixDispute using its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-dispute :as pix-dispute]))

(def dispute (pix-dispute/cancel "5155165527080960"))

(println dispute)
```

### Query PixDispute logs

You can query PixDispute logs to better understand PixDispute life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-dispute.log :as log]))

(def logs (log/query
            {:limit 50
             :after "2022-01-01"
             :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get a PixDispute log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-dispute.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create PixInternalTransactionReports

Transactions that happen internally, outside of the SPI, must be reported to the
Central Bank so they are reflected in your statements. You can do so by creating
PixInternalTransactionReports:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-internal-transaction-report :as report]))

(def reports (report/create
               [{:amount 10000
                 :created "2024-01-01T12:00:00+00:00"
                 :end-to-end-id "E12345678202401011234567890123456"
                 :method "manual"
                 :reference-type "request"
                 :sender-account-number "12345"
                 :sender-branch-code "0001"
                 :sender-account-type "checking"
                 :sender-bank-code "12345678"
                 :sender-tax-id "123.456.789-01"
                 :receiver-account-number "67890"
                 :receiver-branch-code "0001"
                 :receiver-account-type "savings"
                 :receiver-bank-code "87654321"
                 :receiver-tax-id "987.654.321-00"
                 :receiver-key-id "user@example.com"}]))

(doseq [report reports]
  (println report))
```

### Query PixInternalTransactionReports

You can query multiple PixInternalTransactionReports according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-internal-transaction-report :as report]))

(def reports (report/query
               {:after "2024-01-01"
                :before "2024-01-30"
                :status "success"}))

(doseq [report reports]
  (println report))
```

### Get a PixInternalTransactionReport

After its creation, information on a PixInternalTransactionReport may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-internal-transaction-report :as report]))

(def report (report/get "5656565656565656"))

(println report)
```

### Query PixInternalTransactionReport logs

You can query PixInternalTransactionReport logs to better understand their life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-internal-transaction-report.log :as log]))

(def logs (log/query
            {:limit 50
             :after "2024-01-01"
             :before "2024-01-20"}))

(doseq [log logs]
  (println log))
```

### Get a PixInternalTransactionReport log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-internal-transaction-report.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```
### Create StaticBrcodes

StaticBrcodes store account information via a BR code or an image (QR code)
that represents a PixKey and a few extra fixed parameters, such as an amount
and a reconciliation ID. They can easily be used to receive Pix transactions.

```clojure
(ns my-lib.core
  (:require [starkinfra.static-brcode :as static-brcode]))

(def brcodes (static-brcode/create
              [{:name "Jamie Lannister"
                :key-id "+5511988887777"
                :amount 100
                :reconciliation-id "123"
                :city "Rio de Janeiro"}]))

(doseq [brcode brcodes]
  (println brcode))
```

### Query StaticBrcodes

You can query multiple StaticBrcodes according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.static-brcode :as static-brcode]))

(def brcodes (static-brcode/query
              {:limit 1
               :after "2022-06-01"
               :before "2022-06-30"
               :uuids ["5ddde28043a245c2848b08cf315effa2"]}))

(doseq [brcode brcodes]
  (println brcode))
```

### Get a StaticBrcode

After its creation, information on a StaticBrcode may be retrieved by its UUID.

```clojure
(ns my-lib.core
  (:require [starkinfra.static-brcode :as static-brcode]))

(def brcode (static-brcode/get "5ddde28043a245c2848b08cf315effa2"))

(println brcode)
```

### Create DynamicBrcodes

BR codes store information represented by Pix QR Codes, which are used to send
or receive Pix transactions in a convenient way.
DynamicBrcodes represent charges with information that can change at any time,
since all data needed for the payment is requested dynamically to an URL stored
in the BR Code. Stark Infra will receive the GET request and forward it to your
registered endpoint with a GET request containing the UUID of the BR code for
identification.

```clojure
(ns my-lib.core
  (:require [starkinfra.dynamic-brcode :as dynamic-brcode]))

(def brcodes (dynamic-brcode/create
              [{:name "Jamie Lannister"
                :city "Rio de Janeiro"
                :external-id "my_unique_id_01"
                :type "instant"}]))

(doseq [brcode brcodes]
  (println brcode))
```

### Query DynamicBrcodes

You can query multiple DynamicBrcodes according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.dynamic-brcode :as dynamic-brcode]))

(def brcodes (dynamic-brcode/query
              {:limit 1
               :after "2022-06-01"
               :before "2022-06-30"
               :uuids ["ac7caa14e601461dbd6b12bf7e4cc48e"]}))

(doseq [brcode brcodes]
  (println brcode))
```

### Get a DynamicBrcode

After its creation, information on a DynamicBrcode may be retrieved by its UUID.

```clojure
(ns my-lib.core
  (:require [starkinfra.dynamic-brcode :as dynamic-brcode]))

(def brcode (dynamic-brcode/get "ac7caa14e601461dbd6b12bf7e4cc48e"))

(println brcode)
```

### Verify a DynamicBrcode read

When a DynamicBrcode is read by your user, a GET request will be made to your registered URL to
retrieve additional information needed to complete the transaction.
Use this method to verify the authenticity of a GET request received at your registered endpoint.
If the provided digital signature does not check out with the Stark Infra public key, an ex-info
carrying `:code "invalidSignature"` is thrown.

```clojure
(ns my-lib.core
  (:require [starkinfra.dynamic-brcode :as dynamic-brcode]))

(def request (listen)) ;; this is the method you made to get the read requests posted to your registered endpoint

(def uuid (dynamic-brcode/verify
           (get-parameter (:url request) "uuid")
           (get (:headers request) "Digital-Signature")))
```

### Answer to a Due DynamicBrcode read

When a Due DynamicBrcode is read by your user, a GET request containing
the BR code UUID will be made to your registered URL to retrieve additional
information needed to complete the transaction.

The GET request must be answered in the following format within 5 seconds
and with an HTTP status code 200.

```clojure
(ns my-lib.core
  (:require [starkinfra.dynamic-brcode :as dynamic-brcode]))

(def request (listen)) ;; this is the method you made to get the read requests posted to your registered endpoint

(def uuid (dynamic-brcode/verify
           (get-parameter (:url request) "uuid")
           (get (:headers request) "Digital-Signature")))

(def invoice (get-my-invoice uuid)) ;; you should implement this method to get the information of the BR code from its uuid

(send-response ;; you should also implement this method to respond the read request
 (dynamic-brcode/response-due
  {:version (:version invoice)
   :created (:created invoice)
   :due (:due invoice)
   :key-id (:key-id invoice)
   :status (:status invoice)
   :reconciliation-id (:reconciliation-id invoice)
   :nominal-amount (:amount invoice)
   :sender-name (:sender-name invoice)
   :sender-tax-id (:sender-tax-id invoice)
   :receiver-name (:receiver-name invoice)
   :receiver-tax-id (:receiver-tax-id invoice)
   :receiver-street-line (:receiver-street-line invoice)
   :receiver-city (:receiver-city invoice)
   :receiver-state-code (:receiver-state-code invoice)
   :receiver-zip-code (:receiver-zip-code invoice)}))
```

### Answer to an Instant DynamicBrcode read

When an Instant DynamicBrcode is read by your user, a GET request
containing the BR code UUID will be made to your registered URL to retrieve
additional information needed to complete the transaction.

The get request must be answered in the following format
within 5 seconds and with an HTTP status code 200.

```clojure
(ns my-lib.core
  (:require [starkinfra.dynamic-brcode :as dynamic-brcode]))

(def request (listen)) ;; this is the method you made to get the read requests posted to your registered endpoint

(def uuid (dynamic-brcode/verify
           (get-parameter (:url request) "uuid")
           (get (:headers request) "Digital-Signature")))

(def invoice (get-my-invoice uuid)) ;; you should implement this method to get the information of the BR code from its uuid

(send-response ;; you should also implement this method to respond the read request
 (dynamic-brcode/response-instant
  {:version (:version invoice)
   :created (:created invoice)
   :key-id (:key-id invoice)
   :status (:status invoice)
   :reconciliation-id (:reconciliation-id invoice)
   :amount (:amount invoice)
   :cashier-type (:cashier-type invoice)
   :cashier-bank-code (:cashier-bank-code invoice)
   :cash-amount (:cash-amount invoice)}))
```

### Create BrcodePreviews

You can create BrcodePreviews to preview BR Codes before paying them.

```clojure
(ns my-lib.core
  (:require [starkinfra.brcode-preview :as brcode-preview]))

(def previews (brcode-preview/create
               [{:id "00020126420014br.gov.bcb.pix0120nedstark@hotmail.com52040000530398654075000.005802BR5909Ned Stark6014Rio de Janeiro621605126674869738606304FF71"
                 :payer-id "012.345.678-90"}
                {:id "00020126430014br.gov.bcb.pix0121aryastark@hotmail.com5204000053039865406100.005802BR5910Arya Stark6014Rio de Janeiro6216051262678188104863042BA4"
                 :payer-id "012.345.678-90"}]))

(doseq [preview previews]
  (println preview))
```

### Create PixPullSubscriptions

You can create recurring Pix debit authorizations to allow a receiver to pull a series of Pix payments from a sender.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription :as pix-pull-subscription]))

(def subscriptions (pix-pull-subscription/create
                    [{:bacen-id "RR2017032900000000000000003"
                      :external-id "my-subscription-001"
                      :installment-start "2026-04-01T12:00:00+00:00"
                      :interval "month"
                      :receiver-name "Edward Stark"
                      :receiver-tax-id "20.018.183/0001-80"
                      :receiver-bank-code "20018183"
                      :reference-code "contract-202604"
                      :sender-account-number "876543-2"
                      :sender-bank-code "20018183"
                      :sender-branch-code "1357-9"
                      :sender-city-code "3550308"
                      :sender-tax-id "01234567890"
                      :type "push"
                      :amount 11234
                      :description "Monthly subscription"
                      :tags ["employees" "monthly"]}]))

(doseq [subscription subscriptions]
  (println subscription))
```

### Query PixPullSubscriptions

You can query multiple PixPullSubscriptions according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription :as pix-pull-subscription]))

(def subscriptions (pix-pull-subscription/query
                    {:limit 10
                     :after "2026-01-01"
                     :before "2026-04-30"
                     :status ["active"]
                     :tags ["monthly"]}))

(doseq [subscription subscriptions]
  (println subscription))
```

### Get a PixPullSubscription

After its creation, information on a PixPullSubscription may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription :as pix-pull-subscription]))

(def subscription (pix-pull-subscription/get "5656565656565656"))

(println subscription)
```

### Update a PixPullSubscription

You can update a PixPullSubscription by passing its id.

When patching `:status` to `"confirmed"`, `:sender-city-code` MUST be present in the patch.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription :as pix-pull-subscription]))

(def subscription (pix-pull-subscription/update
                   "5656565656565656"
                   {:status "confirmed"
                    :sender-city-code "3550308"}))

(println subscription)
```

### Cancel a PixPullSubscription

You can cancel a PixPullSubscription by passing its id and a reason. The reason is sent as a query parameter on the DELETE request.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription :as pix-pull-subscription]))

(def subscription (pix-pull-subscription/cancel
                   "5656565656565656"
                   {:reason "accountClosed"}))

(println subscription)
```

### Query PixPullSubscription logs

You can query PixPullSubscription logs to better understand PixPullSubscription life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription.log :as log]))

(def logs (log/query
           {:limit 50
            :after "2026-01-01"
            :before "2026-04-30"
            :subscription-ids ["5656565656565656"]}))

(doseq [log logs]
  (println log))
```

### Get a PixPullSubscription log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Process inbound PixPullSubscription events

Inbound PixPullSubscription events will be POSTed at your registered endpoint. You can use the `parse` function to verify the digital signature and reconstruct the PixPullSubscription map.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-subscription :as pix-pull-subscription]))

(def subscription (pix-pull-subscription/parse
                   "{\"bacenId\": \"RR2017032900000000000000003\", ...}"
                   "MEUCIQC7FVhXdripx/aXg5yNLxmNoZlehpyvX3QYDXJ8o3PAZQIgVe1omKFh7Vd54ML4U1z7L+kpx+GHl+G2XLeFTLZeBJk="))

(println subscription)
```

### Create PixPullRequests

You can create PixPullRequests to trigger automatic debits against an active PixPullSubscription.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request :as pix-pull-request]))

(def requests (pix-pull-request/create
               [{:amount 11234
                 :due "2026-04-15T12:00:00+00:00"
                 :end-to-end-id "E00002649202201172211u34srod19le"
                 :receiver-account-number "876543-2"
                 :receiver-account-type "checking"
                 :receiver-bank-code "20018183"
                 :reconciliation-id "cycle-202604"
                 :subscription-id "5656565656565656"
                 :tags ["monthly"]}]))

(doseq [request requests]
  (println request))
```

### Query PixPullRequests

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request :as pix-pull-request]))

(def requests (pix-pull-request/query
               {:limit 10
                :after "2026-01-01"
                :before "2026-04-30"
                :status ["created" "success"]
                :subscription-ids ["5656565656565656"]}))

(doseq [request requests]
  (println request))
```

### Get a PixPullRequest

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request :as pix-pull-request]))

(def request (pix-pull-request/get "5656565656565656"))

(println request)
```

### Update a PixPullRequest

Change the status to `"scheduled"` or `"denied"`. When denying, `:reason` is required.

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request :as pix-pull-request]))

(def request (pix-pull-request/update
              "5656565656565656"
              {:status "denied"
               :reason "senderAccountClosed"}))

(println request)
```

### Cancel a PixPullRequest

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request :as pix-pull-request]))

(def request (pix-pull-request/cancel
              "5656565656565656"
              {:reason "senderUserRequested"}))

(println request)
```

### Query PixPullRequest logs

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request.log :as log]))

(def logs (log/query
           {:limit 50
            :after "2026-01-01"
            :before "2026-04-30"
            :request-ids ["5656565656565656"]}))

(doseq [log logs]
  (println log))
```

### Get a PixPullRequest log

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-pull-request.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

## Ledger

### Create Ledgers

Send a list of Ledger maps for creation in the Stark Infra API.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger :as ledger]))

(def ledgers (ledger/create
               [{:external-id "my-internal-id-123456"
                 :tags ["account/123" "savings"]
                 :rules [{:key "minimumBalance" :value 0}]}]))

(doseq [entry ledgers]
  (println entry))
```

### Query Ledgers

You can query multiple Ledgers according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger :as ledger]))

(def ledgers (ledger/query {:limit 10
                            :after "2020-01-01"
                            :before "2020-03-01"}))

(doseq [entry ledgers]
  (println entry))
```

### Get a Ledger

After its creation, information on a Ledger may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger :as ledger]))

(def entry (ledger/get "5155165527080960"))

(println entry)
```

### Update a Ledger

Update a Ledger by passing its id to change its rules, tags or metadata.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger :as ledger]))

(def entry (ledger/update "5155165527080960" {:tags ["account/123" "updated"]}))

(println entry)
```

### Query Ledger logs

You can query Ledger logs to better understand Ledger life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger.log :as log]))

(def logs (log/query {:limit 50
                      :after "2020-01-01"
                      :before "2020-03-01"}))

(doseq [entry logs]
  (println entry))
```

### Get a Ledger log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger.log :as log]))

(def entry (log/get "5155165527080960"))

(println entry)
```

### Create LedgerTransactions

Send a list of LedgerTransaction maps to move amounts in and out of a Ledger.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger-transaction :as ledger-transaction]))

(def transactions (ledger-transaction/create
                    [{:amount 11234
                      :ledger-id "5656565656565656"
                      :external-id "my-internal-id-123456"
                      :source "bank-transfer/123"
                      :tags ["transfer/123" "savings"]}]))

(doseq [transaction transactions]
  (println transaction))
```

### Query LedgerTransactions

You can query multiple LedgerTransactions according to filters. Either `:ledger-id` or `:ids` must be provided; if both are sent, the query is filtered by both. The other filters are optional.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger-transaction :as ledger-transaction]))

(def transactions (ledger-transaction/query {:ledger-id "5656565656565656"
                                              :after "2020-01-01"
                                              :before "2020-03-01"}))

(doseq [transaction transactions]
  (println transaction))
```

### Get a LedgerTransaction

After its creation, information on a LedgerTransaction may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.ledger-transaction :as ledger-transaction]))

(def transaction (ledger-transaction/get "5155165527080960"))

(println transaction)
```

## Lending

### Create CreditNotes

For lending operations, you can create a CreditNote to generate a CCB contract.

Note that you must have recently created an identity check for that same Tax ID before
being able to create a credit operation for them.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note :as credit-note]))

(def notes (credit-note/create
             [{:template-id "0123456789101112"
               :name "Jamie Lannister"
               :tax-id "012.345.678-90"
               :nominal-amount 100000
               :scheduled "2022-04-28"
               :invoices [{:due "2023-06-25"
                          :amount 120000
                          :fine 10
                          :interest 2
                          :tax-id "012.345.678-90"
                          :name "Jamie Lannister"}]
               :payment {:bank-code "00000000"
                        :branch-code "1234"
                        :account-number "129340-1"
                        :name "Jamie Lannister"
                        :tax-id "012.345.678-90"
                        :amount 100000}
               :payment-type "transfer"
               :signers [{:name "Jamie Lannister"
                         :contact "jamie.lannister@gmail.com"
                         :method "link"}]
               :external-id "1234"
               :street-line-1 "Av. Paulista, 200"
               :street-line-2 "10 andar"
               :district "Bela Vista"
               :city "Sao Paulo"
               :state-code "SP"
               :zip-code "01310-000"
               :rebate-amount 0
               :tags ["War supply"]
               :rules [{:key "invoiceCreationMode" :value "scheduled"}]}]))

(doseq [note notes]
  (println note))
```

### Query CreditNotes

You can query multiple credit notes according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note :as credit-note]))

(def notes (credit-note/query {:limit 10
                               :after "2020-01-01"
                               :before "2020-04-01"
                               :status "success"
                               :tags ["iron" "suit"]}))

(doseq [note notes]
  (println note))
```

### Get a CreditNote

After its creation, information on a credit note may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note :as credit-note]))

(def note (credit-note/get "5155165527080960"))

(println note)
```

### Cancel a CreditNote

You can cancel a credit note if it has not been signed yet.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note :as credit-note]))

(def note (credit-note/cancel "5155165527080960"))

(println note)
```

### Retrieve CCB disbursement pdf file

To retrieve the CCB disbursement pdf file, use the `starkinfra.credit-note/pdf` function with a valid (signed) Credit Note id.

```clojure
(ns my-lib.core
  (:require [clojure.java.io :as io]
            [starkinfra.credit-note :as credit-note]))

(def pdf (credit-note/pdf "5155165527080960"))
(io/copy pdf (io/file "credit_note_receipt.pdf"))
```

### CCB Token Resend

You can resend the CCB token to the signers in case they missed the original email or link.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note :as credit-note]
            [starkinfra.credit-signer :as credit-signer]))

(def note (credit-note/get "5155165527080960"))
(doseq [signer (:signers note)]
  (credit-signer/resend-token (:id signer)))
```

### Query CreditNote logs

You can query credit note logs to better understand CreditNote life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note.log :as log]))

(def logs (log/query {:limit 50
                      :after "2022-01-01"
                      :before "2022-01-20"}))

(doseq [entry logs]
  (println entry))
```

### Get a CreditNote log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-note.log :as log]))

(def entry (log/get "5155165527080960"))

(println entry)
```

### Create CreditPreviews

You can preview a credit operation before creating them (Currently we only have CreditNote / CCB previews):

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-preview :as credit-preview]))

(def previews (credit-preview/create
                [{:type "credit-note"
                  :credit {:initial-amount 2478
                          :initial-due "2022-07-22"
                          :nominal-amount 90583
                          :nominal-interest 3.7
                          :rebate-amount 23
                          :scheduled "2022-06-28"
                          :tax-id "477.954.506-44"
                          :type "sac"}}
                 {:type "credit-note"
                  :credit {:initial-amount 4449
                          :initial-due "2022-07-16"
                          :interval "year"
                          :nominal-amount 96084
                          :nominal-interest 3.1
                          :rebate-amount 239
                          :scheduled "2022-07-02"
                          :tax-id "81.882.684/0001-02"
                          :type "price"}}
                 {:type "credit-note"
                  :credit {:count 8
                          :initial-due "2022-07-18"
                          :nominal-amount 6161
                          :nominal-interest 3.2
                          :scheduled "2022-07-03"
                          :tax-id "59.352.830/0001-20"
                          :type "american"}}
                 {:type "credit-note"
                  :credit {:initial-due "2022-07-13"
                          :nominal-amount 86237
                          :nominal-interest 2.6
                          :scheduled "2022-07-03"
                          :tax-id "37.293.955/0001-94"
                          :type "bullet"}}
                 {:type "credit-note"
                  :credit {:invoices [{:amount 14500 :due "2022-08-19"}
                                     {:amount 14500 :due "2022-09-25"}]
                          :nominal-amount 29000
                          :rebate-amount 900
                          :scheduled "2022-07-31"
                          :tax-id "36.084.400/0001-70"
                          :type "custom"}}]))

(doseq [preview previews]
  (println preview))
```

### Create CreditHolmes

Before you request a credit operation, you may want to check previous credit operations
the credit receiver has taken.

For that, open up a CreditHolmes investigation to receive information on all debts and credit
operations registered for that individual or company inside the Central Bank's SCR.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-holmes :as credit-holmes]))

(def holmes (credit-holmes/create
              [{:tax-id "123.456.789-00" :competence "2022-09"}
               {:tax-id "123.456.789-00" :competence "2022-08"}
               {:tax-id "123.456.789-00" :competence "2022-07"}]))

(doseq [sherlock holmes]
  (println sherlock))
```

### Query CreditHolmes

You can query multiple credit holmes according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-holmes :as credit-holmes]))

(def holmes (credit-holmes/query {:after "2022-06-01"
                                  :before "2022-10-30"
                                  :status "success"}))

(doseq [sherlock holmes]
  (println sherlock))
```

### Get a CreditHolmes

After its creation, information on a credit holmes may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-holmes :as credit-holmes]))

(def holmes (credit-holmes/get "5657818854064128"))

(println holmes)
```

### Query CreditHolmes logs

You can query credit holmes logs to better understand their life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-holmes.log :as log]))

(def logs (log/query {:limit 50
                      :ids ["5729405850615808"]
                      :after "2022-01-01"
                      :before "2022-01-20"
                      :types ["created"]}))

(doseq [entry logs]
  (println entry))
```

### Get a CreditHolmes log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.credit-holmes.log :as log]))

(def entry (log/get "5155165527080960"))

(println entry)
```

## Identity

### Create IndividualIdentities

You can create an IndividualIdentity to validate a document of a natural person

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity :as individual-identity]))

(def identities (individual-identity/create
                  [{:name "Walter White"
                    :tax-id "012.345.678-90"
                    :birth-date "1965-09-07"
                    :tags ["breaking" "bad"]}]))

(doseq [identity identities]
  (println identity))
```

### Query IndividualIdentity

You can query multiple individual identities according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity :as individual-identity]))

(def identities (individual-identity/query {:limit 10
                                            :after "2020-01-01"
                                            :before "2020-04-01"
                                            :status "success"
                                            :tags ["breaking" "bad"]}))

(doseq [identity identities]
  (println identity))
```

### Get an IndividualIdentity

After its creation, information on an individual identity may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity :as individual-identity]))

(def identity (individual-identity/get "5155165527080960"))

(println identity)
```

### Update an IndividualIdentity

You can update a specific identity status to "processing" for send it to validation.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity :as individual-identity]))

(def identity (individual-identity/update "5155165527080960" {:status "processing"}))

(println identity)
```

### Cancel an IndividualIdentity

You can cancel an individual identity before updating its status to processing.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity :as individual-identity]))

(def identity (individual-identity/cancel "5155165527080960"))

(println identity)
```

### Query IndividualIdentity logs

You can query individual identity logs to better understand individual identity life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity.log :as log]))

(def logs (log/query {:limit 50
                      :after "2022-01-01"
                      :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get an IndividualIdentity log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-identity.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create IndividualAccountRequests

You can create an IndividualAccountRequest to request the opening of an account for a specific individual.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-request :as individual-account-request]))

(def requests (individual-account-request/create
                [{:name "Walter White"
                  :tax-id "012.345.678-90"
                  :address {:street "Rua do Estilo Barroco"
                            :number "648"
                            :neighborhood "Santo Amaro"
                            :city "Sao Paulo"
                            :state "SP"
                            :zip-code "05724005"}
                  :income 1000000
                  :birth-date "1965-09-07"
                  :tags ["breaking" "bad"]}]))

(doseq [request requests]
  (println request))
```

### Query IndividualAccountRequests

You can query multiple individual account requests according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-request :as individual-account-request]))

(def requests (individual-account-request/query {:limit 10
                                                  :after "2020-01-01"
                                                  :before "2020-04-01"
                                                  :status "success"
                                                  :tags ["breaking" "bad"]}))

(doseq [request requests]
  (println request))
```

### Get an IndividualAccountRequest

After its creation, information on an individual account request may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-request :as individual-account-request]))

(def request (individual-account-request/get "5155165527080960"))

(println request)
```

### Update an IndividualAccountRequest

You can update a specific individual account request by passing its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-request :as individual-account-request]))

(def request (individual-account-request/update "5155165527080960" {:status "processing"}))

(println request)
```

### Query IndividualAccountRequest logs

You can query individual account request logs to better understand individual account request life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-request.log :as log]))

(def logs (log/query {:limit 50
                      :after "2022-01-01"
                      :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get an IndividualAccountRequest log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-request.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create IndividualAccountAttachments

You can create an IndividualAccountAttachment to attach images of documents to a specific IndividualAccountRequest.
You must reference the desired IndividualAccountRequest by its id. Pass the raw image bytes and a MIME content type;
the SDK encodes them as a data url before sending.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-attachment :as individual-account-attachment])
  (:import (java.nio.file Files Paths)))

(def attachments (individual-account-attachment/create
                   [{:type "identity-front"
                     :content (Files/readAllBytes (Paths/get "identity-front.png" (into-array String [])))
                     :content-type "image/png"
                     :account-request-id "5155165527080960"
                     :tags ["breaking" "bad"]}]))

(doseq [attachment attachments]
  (println attachment))
```

**Note**: The API accepts a single attachment per create call. You may also pass an already-built Base64 data url as `:content` and omit `:content-type`.

### Query IndividualAccountAttachments

You can query multiple individual account attachments according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-attachment :as individual-account-attachment]))

(def attachments (individual-account-attachment/query {:limit 10
                                                       :after "2020-01-01"
                                                       :before "2020-04-01"
                                                       :status "success"
                                                       :tags ["breaking" "bad"]}))

(doseq [attachment attachments]
  (println attachment))
```

### Get an IndividualAccountAttachment

After its creation, information on an individual account attachment may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-attachment :as individual-account-attachment]))

(def attachment (individual-account-attachment/get "5155165527080960"))

(println attachment)
```

### Cancel an IndividualAccountAttachment

You can delete an individual account attachment by passing its id. The returned map has status "deleted".

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-attachment :as individual-account-attachment]))

(def attachment (individual-account-attachment/cancel "5155165527080960"))

(println attachment)
```

### Query IndividualAccountAttachment logs

You can query individual account attachment logs to better understand individual account attachment life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-attachment.log :as log]))

(def logs (log/query {:limit 50
                      :after "2022-01-01"
                      :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get an IndividualAccountAttachment log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.individual-account-attachment.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create BusinessIdentities

You can create a BusinessIdentity to verify the identity of a company (PJ) by its tax ID (CNPJ).

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity :as business-identity]))

(def identities (business-identity/create
                  [{:tax-id "20.018.183/0001-80"
                    :tags ["onboarding-123"]}]))

(doseq [identity identities]
  (println identity))
```

### Query BusinessIdentities

You can query multiple business identities according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity :as business-identity]))

(def identities (business-identity/query {:limit 10
                                          :after "2020-01-01"
                                          :before "2020-04-01"
                                          :status "success"
                                          :tags ["onboarding-123"]}))

(doseq [identity identities]
  (println identity))
```

### Get a BusinessIdentity

After its creation, information on a business identity may be retrieved by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity :as business-identity]))

(def identity (business-identity/get "5155165527080960"))

(println identity)
```

### Update a BusinessIdentity

You can update a specific business identity by passing its id. Send it to processing by passing "processing" in the status (the identity must have attachments).

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity :as business-identity]))

(def identity (business-identity/update "5155165527080960" {:status "processing"}))

(println identity)
```

### Cancel a BusinessIdentity

You can cancel a business identity by passing its id, while it is in the 'created' or 'pending' status.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity :as business-identity]))

(def identity (business-identity/cancel "5155165527080960"))

(println identity)
```

### Query BusinessIdentity logs

You can query business identity logs to better understand business identity life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity.log :as log]))

(def logs (log/query {:limit 50
                      :after "2022-01-01"
                      :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get a BusinessIdentity log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-identity.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create BusinessAttachments

You can create a BusinessAttachment to attach a document (e.g. articles of incorporation) to a specific BusinessIdentity.
You must reference the desired business identity by its id. A BusinessIdentity accepts at most 2 attachments.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-attachment :as business-attachment]))

(def attachments (business-attachment/create
                   [{:name "articles-of-incorporation.pdf"
                     :content "data:application/pdf;base64,JVBERi0xLjQ..."
                     :business-identity-id "5155165527080960"
                     :tags ["doc-principal"]}]))

(doseq [attachment attachments]
  (println attachment))
```

### Query BusinessAttachments

You can query multiple business attachments according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-attachment :as business-attachment]))

(def attachments (business-attachment/query {:limit 10
                                             :after "2020-01-01"
                                             :before "2020-04-01"
                                             :status "approved"
                                             :tags ["doc-principal"]}))

(doseq [attachment attachments]
  (println attachment))
```

### Get a BusinessAttachment

After its creation, information on a business attachment may be retrieved by its id. Pass `:expand ["content"]` to also retrieve the document content.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-attachment :as business-attachment]))

(def attachment (business-attachment/get "5155165527080960" {:expand ["content"]}))

(println attachment)
```

### Cancel a BusinessAttachment

You can cancel a business attachment by passing its id, while it is in the 'created' status.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-attachment :as business-attachment]))

(def attachment (business-attachment/cancel "5155165527080960"))

(println attachment)
```

### Query BusinessAttachment logs

You can query business attachment logs to better understand business attachment life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-attachment.log :as log]))

(def logs (log/query {:limit 50
                      :after "2022-01-01"
                      :before "2022-01-20"}))

(doseq [log logs]
  (println log))
```

### Get a BusinessAttachment log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-attachment.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

### Create BusinessAccountRequests

You can create a BusinessAccountRequest to request an account for a specific company, opening the account with
identity verification by webview for each of its owners.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-account-request :as business-account-request]))

(def requests (business-account-request/create
                [{:name "Stark Bank S.A."
                  :tax-id "20.018.183/0001-80"
                  :address {:street "Av. Faria Lima"
                            :number "2000"
                            :neighborhood "Itaim Bibi"
                            :city "Sao Paulo"
                            :state "SP"
                            :zip-code "04538-132"
                            :complement "Sala 42"}
                  :revenue 100000000
                  :owners [{:tax-id "012.345.678-90"
                            :name "Jamie Lannister"
                            :role "partner"}
                           {:tax-id "812.531.960-36"
                            :name "Cersei Lannister"
                            :role "representative"}]}]))

(doseq [request requests]
  (println request))
```

### Query BusinessAccountRequests

You can query multiple business account requests according to filters.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-account-request :as business-account-request]))

(def requests (business-account-request/query {:limit 10
                                                :after "2020-01-01"
                                                :before "2020-04-01"
                                                :status "approved"
                                                :tags ["breaking" "bad"]}))

(doseq [request requests]
  (println request))
```

### Get a BusinessAccountRequest

After its creation, information on a business account request may be retrieved by its id. Use it to read the
per-owner verification status.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-account-request :as business-account-request]))

(def request (business-account-request/get "5155165527080960"))

(doseq [owner (:owners request)]
  (println (:name owner) (:status owner)))
```

Each owner also carries a `:validator-link`, the webview where that owner completes biometrics and document
capture. Treat it as a credential: deliver it to its owner through a secure channel, and never log it or write
it to disk.

### Query BusinessAccountRequest logs

You can query business account request logs to better understand business account request life cycles.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-account-request.log :as log]))

(def logs (log/query {:limit 50
                      :after "2020-01-01"
                      :before "2020-01-20"}))

(doseq [log logs]
  (println log))
```

### Get a BusinessAccountRequest log

You can also get a specific log by its id.

```clojure
(ns my-lib.core
  (:require [starkinfra.business-account-request.log :as log]))

(def log (log/get "5155165527080960"))

(println log)
```

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

The SDK signals errors by throwing `clojure.lang.ExceptionInfo`. When the API
refused a request, the `ex-data` of the thrown exception carries the HTTP
`:status` and a list of `:errors`, each with a `:code` and a `:message`, so you
can branch on either.

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
speed. That one surfaces as a single `internalServerError` error; any other
unexpected status, and a connection that never reached us at all (`:status 0`),
surfaces as a single `unknownError` error carrying what came back:

```clojure
(ns my-lib.core
  (:require [starkinfra.pix-request :as pix-request]))

(try
  (pix-request/get "0")
  (catch clojure.lang.ExceptionInfo exception
    (println (:status (ex-data exception)))
    (println (:code (first (:errors (ex-data exception)))))))
```

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
failed response instead, whatever the status. `:content` is the parsed body with
kebab-case keys, or the raw string when the body is not JSON.

# Help and Feedback

If you have any questions about our SDK, just send us an email.
We will respond you quickly, pinky promise. We are here to help you integrate with us ASAP.
We also love feedback, so don't be shy about sharing your thoughts with us.

Email: help@starkinfra.com
