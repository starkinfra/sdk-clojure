# Stark Infra Python SDK - Beta

Welcome to the Stark Infra Python SDK! This tool is made for Python 
developers who want to easily integrate with our API.
This SDK version is compatible with the Stark Infra API v2.

# Introduction

## Index

- [Introduction](#introduction)
    - [Supported Python versions](#supported-python-versions)
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
    - [Pix](#pix)
        - [PixRequests](#create-pixrequests): Create Pix transactions
        - [PixReversals](#create-pixreversals): Reverse Pix transactions
        - [PixBalance](#get-your-pixbalance): View your account balance
        - [PixStatement](#create-a-pixstatement): Request your account statement
        - [PixKey](#create-a-pixkey): Create a Pix Key
        - [PixClaim](#create-a-pixclaim): Claim a Pix Key
        - [PixDirector](#create-a-pixdirector): Create a Pix Director
        - [PixInfraction](#create-pixinfractions): Create Pix Infraction reports
        - [PixChargeback](#create-pixchargebacks): Create Pix Chargeback requests
        - [PixDomain](#query-pixdomains): View registered SPI participants certificates
    - [Credit Note](#credit-note)
        - [CreditNote](#create-creditnotes): Create credit notes
    - [Webhook](#webhook):
        - [Webhook](#create-a-webhook-subscription): Configure your webhook endpoints and subscriptions
    - [Webhook Events](#webhook-events):
        - [WebhookEvents](#process-webhook-events): Manage Webhook events
        - [WebhookEventAttempts](#query-failed-webhook-event-delivery-attempts-information): Query failed webhook event deliveries
- [Handling errors](#handling-errors)
- [Help and Feedback](#help-and-feedback)

## Supported Python Versions

This library supports the following Python versions:

* Python 2.7
* Python 3.4+

## Stark Infra API documentation

Feel free to take a look at our [API docs](https://www.starkinfra.com/docs/api).

## Versioning

This project adheres to the following versioning pattern:

Given a version number MAJOR.MINOR.PATCH, increment:

- MAJOR version when the **API** version is incremented. This may include backwards incompatible changes;
- MINOR version when **breaking changes** are introduced OR **new functionalities** are added in a backwards compatible manner;
- PATCH version when backwards compatible bug **fixes** are implemented.

# Setup

## 1. Install our SDK

1.1 Manually download the desired SDK version JARs found in our
[GitHub page](https://github.com/starkinfra/sdk-clojure/releases/latest)
and add it to your `project.clj` as `:resource-paths ["resources/starkinfra-0.0.3.jar"]`.

1.2 Using Leiningen/Boot:
```sh
[starkinfra/sdk "0.0.3"]
```

1.3 Using Clojure CLI/deps.edn:
```sh
starkinfra/sdk {:mvn/version "0.0.3"}
```

1.4 Using Gradle:
```sh
compile 'starkinfra:sdk:0.0.3'
```

1.5 Using Maven:
```xml
<dependency>
  <groupId>starkinfra</groupId>
  <artifactId>sdk</artifactId>
  <version>0.0.3</version>
</dependency>
```

## 2. Create your Private and Public Keys

We use ECDSA. That means you need to generate a secp256k1 private
key to sign your requests to our API, and register your public key
with us so we can validate those requests.

You can use one of following methods:

2.1. Check out the options in our [tutorial](https://starkinfra.com/faq/how-to-create-ecdsa-keys).

2.2. Use our SDK:

```clojure
(ns my-lib.core
  (:use starkinfra.core))

(def key-pair (starkinfra.key/create))
(def private-key (:private-key key-pair))
(def public-key (:public-key key-pair))

;or, to also save .pem files in a specific path
(def key-pair (starkinfra.key/create "file/keys/"))
(def private-key (:private-key key-pair))
(def public-key (:public-key key-pair))
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

3.1.6. Use the Project ID and private key to create the object below:

```clojure
(ns my-lib.core
  (:use starkinfra.core))

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

(def project (starkinfra.user/project
    "sandbox"
    "5671398416568321"
    private-key-content
  ))
```

3.2. To create Organization credentials in Sandbox:

3.2.1. Log into [Starkinfra Sandbox](https://web.sandbox.starkinfra.com)

3.2.2. Go to Menu > Integrations

3.2.3. Click on the "Organization public key" button

3.2.4. Upload the public key you created in section 2 (only a legal representative of the organization can upload the public key)

3.2.5. Click on your profile picture and then on the "Organization" menu to get the Organization ID

3.2.6. Use the Organization ID and private key to create the object below:

```clojure
(ns my-lib.core
  (:use starkinfra.core))

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

(def organization (starkinfra.user/organization
    "sandbox"
    "5671398416568321"
    private-key-content
    nil; You only need to set the workspace-id when you are operating a specific workspace-id
  ))

;To dynamically use your organization credentials in a specific workspace-id,
;you can use the user/organization-replace function:
(starkinfra.balance/get (starkinfra.user/organization-replace organization "4848484848484848"))
```

NOTE 1: Never hard-code your private key. Get it from an environment variable or an encrypted database.

NOTE 2: We support `'sandbox'` and `'production'` as environments.

NOTE 3: The credentials you registered in `sandbox` do not exist in `production` and vice versa.


## 4. Setting up the user

There are three kinds of users that can access our API: **Organization**, **Project**, and **Member**.

- `Project` and `Organization` are designed for integrations and are the ones meant for our SDKs.
- `Member` is the one you use when you log into our webpage with your e-mail.

There are two ways to inform the user to the SDK:

4.1 Passing the user as an argument in all functions:

```clojure
(ns my-lib.core
  (:use starkinfra.core))

(def user (starkinfra.user/project; or organization
    "sandbox"
    "5671398416568321"
    private-key-content))
    
(def balance (starkinfra.pix-balance/get user))
```

4.2 Set it as a default user in the SDK:

```clojure
(ns my-lib.core
  (:use starkinfra.core))

(def user (starkinfra.user/project; or organization
    "sandbox"
    "5671398416568321"
    private-key-content))

(starkinfra.settings/user user)

(def balance (starkinfra.pix-balance/get))
```

Just select the way of passing the user that is more convenient to you.
On all following examples, we will assume a default user has been set.

## 5. Setting up the error language

The error language can also be set in the same way as the default user:

```clojure
(:require [starkinfra.settings :as settings])

(settings/language "pt-BR")
```

Language options are "en-US" for English and "pt-BR" for Brazilian Portuguese. English is the default.

# Resource listing and manual pagination

Almost all SDK resources provide a `query` and a `page` function.

- The `query` function provides a straightforward way to efficiently iterate through all results that match the filters you inform,
seamlessly retrieving the next batch of elements from the API only when you reach the end of the current batch.
If you are not worried about data volume or processing time, this is the way to go.

```clojure
(def requests
  (starkinfra.pix-request/query
    {
      :after "2020-03-20"
      :before "2020-03-30"
      :limit 10
    }))

(println requests)
```

- The `page` function gives you full control over the API pagination. With each function call, you receive up to
100 results and the cursor to retrieve the next batch of elements. This allows you to stop your queries and
pick up from where you left off whenever it is convenient. When there are no more elements to be retrieved, the returned cursor will be `None`.

```clojure
(defn get-page
  [iterations, cursor]
    (when (> iterations 0)
      (def page (pix-request/page {:limit 2}))
      (def new-cursor (get page :cursor))
      (def new-entities (get page :requests))
      (concat new-entities (get-page (- iterations 1) new-cursor))))

(println (get-page 3 nil))
```

To simplify the following SDK examples, we will only use the `query` function, but feel free to use `page` instead.

# Testing in Sandbox

Your initial balance is zero. For many operations in Stark Infra, you'll need funds
in your account, which can be added to your balance by creating a starkbank.Invoice. 

In the Sandbox environment, most of the created starkbank.Invoices will be automatically paid,
so there's nothing else you need to do to add funds to your account. Just create
a few starkbank.Invoice and wait around a bit.

In Production, you (or one of your clients) will need to actually pay this Pix Request
for the value to be credited to your account.


# Usage

Here are a few examples on how to use the SDK. If you have any doubts, use the built-in
`help()` function to get more info on the desired functionality
(for example: `help(starkinfra.issuinginvoice.create)`)

## Pix

### Create PixRequests

You can create a Pix request to transfer money from one of your users to anyone else:

```clojure
(def requests (starkinfra.pix-request/create
    [
        {
            :amount 100
            :external-id "141234121"
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
            :end-to-end-id (starkinfra.endtoendid/create "20018183")
            :description "For saving my life"}
        {
            :amount 200
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
            :end-to-end-id (starkinfra.endtoendid/create "20018183")
            :tags ["Needle", "sword"]}]))

(println requests)
```

**Note**: Instead of using PixRequest objects, you can also pass each element in dictionary format

### Query PixRequests

You can query multiple Pix requests according to filters.

```clojure

(def requests (starkinfra.pix-request/query 
    {
        :limit 10
        :after "2020-03-20"
        :before "2020-03-30"
        :status "success"
        :tags ["iron", "suit"]
        :end_to_end_ids ["E79457883202101262140HHX553UPqeq"]
    }
)

(println requests)
```

### Get a PixRequest

After its creation, information on a Pix request may be retrieved by its id. Its status indicates whether it has been paid.

```clojure
(def request (starkinfra.pix-request/get "5155165527080960"))

(println request)
```

### Process inbound PixRequest authorizations

It's easy to process authorization requests that arrived at your endpoint.
Remember to pass the signature header so the SDK can make sure it's StarkInfra that sent you the event.
If you do not approve or decline the authorization within 1 second, the authorization will be denied.

```clojure
(def response (listen)); this is your handler to listen for authorization requests

(def pix-request (starkinfra.pix-request/parse
    (:content response)
    (:Digital-Signature (:headers response))))

(println pix-request)
```

### Query PixRequest logs

You can query Pix request logs to better understand Pix request life cycles. 

```clojure
(def logs (starkinfra.pix-request.log/query
    {:limit 50
     :after "2022-01-01"
     :before "2022-01-20"}))

(println logs)
```

### Get a PixRequest log

You can also get a specific log by its id.

```clojure
(def log (starkinfra.pix-request.log/get "5155165527080960"))

(println log)
```

### Create PixReversals

You can reverse a PixRequest either partially or totally using a PixReversal.

```clojure
(def reversal (starkinfra.pix-reversal/create
    [
        {:amount 100
         :end-to-end-id "E00000000202201060100rzsJzG9PzMg"
         :external-id "17238435823958934"
         :reason "bankError"}]))

(println reversal)
```

### Query PixReversals 

You can query multiple Pix reversals according to filters. 

```clojure
(def reversals (starkinfra.pix-reversal/query
    {
        :limit 10
        :after "2020-03-20"
        :before "2020-03-30"
        :status "success"
        :tags ["iron", "suit"]
        :return-ids ["D20018183202202030109X3OoBHG74wo"]
    }
))

(println reversals)
```

### Get a PixReversal

After its creation, information on a Pix reversal may be retrieved by its id.
Its status indicates whether it has been successfully processed.

```clojure
(def reversal (starkinfra.pix-reversal/get "5155165527080960"))

(println reversal)
```

### Process inbound PixReversal authorizations

It's easy to process authorization requests that arrived at your endpoint.
Remember to pass the signature header so the SDK can make sure it's StarkInfra that sent you the event.
If you do not approve or decline the authorization within 1 second, the authorization will be denied.

```clojure
(def reversal (starkinfra.pix-reversal/parse
    (:content response)
    (:Digital-Signature (:headers response))))

(println reversal)
```

### Query PixReversal logs

You can query Pix reversal logs to better understand their life cycles. 

```clojure
(def logs (starkinfra.pix-reversal.log/query
    {:limit 50
     :after "2022-01-01"
     :before "2022-01-20"}))

(println logs)
```

### Get a PixReversal log

You can also get a specific log by its id.

```clojure
(def log (starkinfra.pix-reversal.log/get "5155165527080960"))

(println log)
```

### Get your PixBalance 

To see how much money you have in your account, run:

```clojure
(def balance (starkinfra.pix-balance/get))

(println balance)
```

### Create a PixStatement

Statements are generated directly by the Central Bank and are only available for direct participants.
To create a statement of all the transactions that happened on your account during a specific day, run:

```clojure
(def statement 
    (starkinfra.pix-statement/create
        {
            :after "2022-01-01"
            :before "2022-01-01"
            :type "transaction"}))

(println statement)
```

### Query PixStatements

You can query multiple Pix statements according to filters. 

```clojure
(def statements (starkinfra.pix-statement/query
    {:limit 50}))

(println statements)
```

### Get a PixStatement

Statements are only available for direct participants. To get a Pix statement by its id:

```clojure
(def statement (starkinfra.pix-statement/get "5155165527080960"))

(println statement)
```

### Get a PixStatement .csv file

To get the .csv file corresponding to a Pix statement using its id, run:

```clojure
(require '[clojure.java.io :as io])

(def zip (starkinfra.pix-statement/csv "5155165527080960"))

(def file-name "temp/pix-statement.zip")
(io/make-parents file-name)
(io/copy zip (io/file file-name))
```

### Create a PixKey

You can create a Pix Key to link a bank account information to a key id:

```clojure
(def key (starkinfra.pix-key/create
    {:account-created "2022-02-01T00:00:00.00"
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
(def keys (starkinfra.pix-key/query
    {:limit 50
     :after "2022-01-01"
     :before "2022-01-20"
     :status "registered"
     :tags ["iron", "bank"]
     :ids ["+5511989898989"]
     :type "phone"}))

(println keys)
```

### Get a PixKey

Information on a Pix key may be retrieved by its id and the tax ID of the consulting agent.
An endToEndId must be informed so you can link any resulting purchases to this query,
avoiding sweep blocks by the Central Bank.

```clojure
(import [com.starkinfra.utils EndToEndId])

(def key (starkinfra.pix-key/get
    "5155165527080960"
    {
        :payer-id "012.345.678-90"
        :end-to-end-id (EndToEndId/create "20018183")
    }))

(println key)
```

### Patch a PixKey

Update the account information linked to a Pix Key.

```clojure
(def key (starkinfra.pix-key/update
    "5155165527080960"
    {
        :reason "branchTransfer"
        :name "Jamie Lannister"
    }))

(println key)
```

### Cancel a PixKey

Cancel a specific Pix Key using its id.

```clojure
(def key (starkinfra.pix-key/cancel
    "5155165527080960"))

(println key)
```

### Query PixKey logs

You can query Pix key logs to better understand a Pix key life cycle. 

```clojure
(def logs (starkinfra.pix-key.log/query
    {
        :limit 50
        :after "2022-01-01"
        :before "2022-01-20"
        :ids ["5729405850615808"]
        :types ["created"]
        :key-ids ["+5511989898989"]
    }))

(println logs)
```

### Get a PixKey log

You can also get a specific log by its id.

```clojure
(def log (starkinfra.pix-key.log/get "5155165527080960"))

(println log)
```

### Create a PixClaim

You can create a Pix claim to request the transfer of a Pix key from another bank to one of your accounts:

```clojure
(def claim (starkinfra.pix-claim/create
    {
        :account-created "2022-02-01T00:00:00.00"
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
(def claims (starkinfra.pix-claim/query
    {:limit 50
     :after "2022-01-01"
     :before "2022-01-12"
     :status "registered"
     :ids ["5729405850615808"]
     :type "ownership"
     :agent "claimed"
     :key-type "phone"
     :key-id "+5511989898989"}))

(println claims)
```

### Get a PixClaim

After its creation, information on a Pix claim may be retrieved by its id.

```clojure
(def claim (starkinfra.pix-claim/get "5155165527080960"))

(println claim)
```

### Patch a PixClaim

A Pix Claim can be confirmed or canceled by patching its status.
A received Pix Claim must be confirmed by the donor to be completed.
Ownership Pix Claims can only be canceled by the donor if the reason is "fraud".
A sent Pix Claim can also be canceled.

```clojure
(def claim (starkinfra.pix-claim/update
    "5155165527080960"
    {
        :status "confirmed"
    }))

(println claim)
```

### Query PixClaim logs

You can query Pix claim logs to better understand Pix claim life cycles.

```clojure
(def logs (starkinfra.pix-claim.log/query
    {
        :limit 50
        :after "2022-01-01"
        :before "2022-01-20"
        :ids ["5729405850615808"]
        :types ["registered"]
        :claim-ids ["5719405850615809"]
    }))

(println logs)
```

### Get a PixClaim log

You can also get a specific log by its id.

```clojure
(def log (starkinfra.pix-claim.log/get "5155165527080960"))

(println log)
```
### Create a PixDirector

To register the Pix director contact information at the Central Bank, run the following:

```clojure
(def director (starkinfra.pix-director/create
    {
        :name "Edward Stark"
        :tax-id "03.300.300/0001-00"
        :phone "+5511999999999"
        :email "ned.stark@company.com",
        :password "12345678"
        :team-email "pix.team@company.com"
        :team_phones ["+5511988889999", "+5511988889998"]
    }))

(println director)
```

### Create PixInfractions

Pix Infraction reports are used to report transactions that raise fraud suspicion, to request a refund or to 
reverse a refund. Infraction reports can be created by either participant of a transaction.

```clojure
(def infractions (starkinfra.pix-infraction/create
    [
        {
            :reference-id "E20018183202201201450u34sDGd19lz"
            :type "fraud"
        }
    ]))

(println infractions)
```

### Query PixInfractions

You can query multiple infraction reports according to filters.

```clojure
(def infractions (starkinfra.pix-infraction/query
    {
        :limit 50
        :after "2022-01-01"
        :before "2022-01-20"
        :status "delivered"
        :ids ["5729405850615808"]
    }))

(println infractions)
```

### Get a PixInfraction

After its creation, information on a Pix Infraction may be retrieved by its id.

```clojure
(def infraction (starkinfra.pix-infraction/get "5155165527080960"))

(println infraction)
```

### Patch a PixInfraction

A received Pix Infraction can be confirmed or declined by patching its status.
After a Pix Infraction is patched, its status changes to closed.

```clojure
(def infraction (starkinfra.pix-infraction/update
    "5155165527080960"
    "agreed"))

(println infraction)
```

### Cancel a PixInfraction

Cancel a specific Pix Infraction using its id.

```clojure
(def infraction (starkinfra.pix-infraction/cancel
    "5155165527080960"))

(println infraction)
```

### Query PixInfraction logs

You can query infraction report logs to better understand their life cycles. 

```clojure
(def logs (starkinfra.pix-infraction.log/query
    {
        :limit 50
        :after "2022-01-01"
        :before "2022-01-20"
        :ids ["5729405850615808"]
        :types ["created"]
        :infraction-ids ["5155165527080960"]
    }))

(println logs)
```

### Get a PixInfraction log

You can also get a specific log by its id.

```clojure
(def log (starkinfra.pix-infraction.log/get "5155165527080960"))

(println log)
```

### Create PixChargebacks

A Pix chargeback can be created when fraud is detected on a transaction or a system malfunction 
results in an erroneous transaction.

```clojure
(def chargebacks (starkinfra.pix-chargeback/create
    [
        {
            :reference-id "E20018183202201201450u34sDGd19lz"
            :reason "fraud"
            :amount 100
        }
    ]))

(println chargebacks)
```

### Query PixChargebacks

You can query multiple Pix chargebacks according to filters.

```clojure
(def chargebacks (starkinfra.pix-chargeback/query
    {
        :limit 50
        :after "2022-01-01"
        :before "2022-01-12"
        :status "registered"
        :ids ["5729405850615808"]
    }))

(println chargebacks)
```

### Get a PixChargeback

After its creation, information on a Pix Chargeback may be retrieved by its.

```clojure
(def chargeback (starkinfra.pix-chargeback/get "5155165527080960"))

(println chargeback)
```

### Patch a PixChargeback

A received Pix Chargeback can be accepted or rejected by patching its status.
After a Pix Chargeback is patched, its status changes to closed.

```clojure
(import [com.starkinfra.utils EndToEndId])

(def chargeback (starkinfra.pix-chargeback/update
    "5155165527080960"
    "accepted"
    {
        :reversal-reference-id (EndToEndId/create "20018183")
    }))

(println chargeback)
```

### Cancel a PixChargeback

Cancel a specific Pix Chargeback using its id.

```clojure
(def chargeback (starkinfra.pix-chargeback/cancel
    "5155165527080960"))

(println chargeback)
```

### Query PixChargeback logs

You can query Pix chargeback logs to better understand Pix chargeback life cycles. 

```clojure
(def logs (starkinfra.pix-chargeback.log/query
    {
        :limit 50
        :after "2022-01-01"
        :before "2022-01-20"
        :ids ["5729405850615808"]
        :types ["created"]
        :chargeback-ids ["5155165527080960"]
    }))

(println logs)
```
### Get a PixChargeback log

You can also get a specific log by its id.

```clojure
(def log (starkinfra.pix-chargeback.log/get "5155165527080960"))

(println log)
```

### Query PixDomains

Here you can list all Pix Domains registered at the Brazilian Central Bank. The Pix Domain object displays the domain 
name and the QR Code domain certificates of registered Pix participants able to issue dynamic QR Codes.

```clojure
(def domains (starkinfra.pix-domain/query))

(println domains)
```

## Webhook

### Create a webhook subscription

To create a webhook subscription and be notified whenever an event occurs, run:

```clojure
(def webhook (starkinfra.webhook/create
    {
        :url "https://webhook.site/dd784f26-1d6a-4ca6-81cb-fda0267761ec"
        :subscriptions ["contract", "credit-note", "signer",
                        "issuing-card", "issuing-invoice", "issuing-purchase",
                        "pix-request.in", "pix-request.out", "pix-reversal.in", "pix-reversal.out", "pix-claim", "pix-key", "pix-chargeback", "pix-infraction"]
    }))

(println webhook)
```


### Query webhooks

To search for registered webhooks, run:

```clojure
(def webhooks (starkinfra.webhook/query))

(println webhooks)
```

### Get a webhook

You can get a specific webhook by its id.

```python
import starkinfra

webhook = starkinfra.webhook.get("1082736198236817")

print(webhook)
```
```clojure
(def webhook (starkinfra.webhook/get "1082736198236817"))

(println webhook)
```

### Delete a webhook

You can also delete a specific webhook by its id.

```clojure
(def webhook (starkinfra.webhook/delete "1082736198236817"))

(println webhook)
```

## Webhook Events

### Process webhook events

It's easy to process events delivered to your Webhook endpoint.
Remember to pass the signature header so the SDK can make sure it was StarkInfra that sent you the event.

```clojure
(def response (listen)); this is the function you made to get the events posted to your webhook

(def event
  (starkinfra.event/parse
    (:content response)
    (:Digital-Signature (:headers response))))

(println event)
```

### Query webhook events

To search for webhooks events, run:

```clojure
(def events (starkinfra.event/query
    {
        :after "2020-03-20"
        :is-delivered false
    }))

(println events)
```

### Get a webhook event

You can get a specific webhook event by its id.

```clojure
(def event (starkinfra.event/get "1082736198236817"))

(println event)
```

### Delete a webhook event

You can also delete a specific webhook event by its id.

```clojure
(def event (starkinfra.event/delete "1082736198236817"))

(println event)
```

### Set webhook events as delivered

This can be used in case you've lost events.
With this function, you can manually set events retrieved from the API as
"delivered" to help future event queries with `is_delivered=False`.

```python
import starkinfra

event = starkinfra.event.update(id="1298371982371921", is_delivered=True)

print(event)
```

### Query failed webhook event delivery attempts information

You can also get information on failed webhook event delivery attempts.

```clojure
(def attempts (starkinfra.event.attempt/query
    {
        :after "2020-03-20"
    }))

(println attempts)
```

### Get a failed webhook event delivery attempt information

To retrieve information on a single attempt, use the following function:

```clojure
(def attempt (starkinfra.event.attempt/get "1616161616161616"))

(println attempt)
```

# Handling errors

The SDK may raise one of four types of errors: __InputErrors__, __InternalServerError__, __UnknownError__, __InvalidSignatureError__

__InputErrors__ will be raised whenever the API detects an error in your request (status code 400).
If you catch such an error, you can get its elements to verify each of the
individual errors that were detected in your request by the API.
For example:

```clojure
(try
  (starkinfra.pix-reversal/create [
    {
        :amount 100
        :end_to_end_id "E00000000202201060100rzsJzG9PzMg"
        :external_id "1723843582395893"
        :reason "bankError"
    }
  ])
  (catch com.starkinfra.error.InputErrors e
    (map
      (fn [element] (str "error-code: " (.code element) "\nerror-message: " (.message element)))
      (.errors e))))
```

__InternalServerError__ will be raised if the API runs into an internal error.
If you ever stumble upon this one, rest assured that the development team
is already rushing in to fix the mistake and get you back up to speed.

__UnknownError__ will be raised if a request encounters an error that is
neither __InputErrors__ nor an __InternalServerError__, such as connectivity problems.

__InvalidSignatureError__ will be raised specifically by starkinfra.event.parse()
when the provided content and signature do not check out with the Stark Infra public
key.

# Help and Feedback

If you have any questions about our SDK, just send us an email.
We will respond you quickly, pinky promise. We are here to help you integrate with us ASAP.
We also love feedback, so don't be shy about sharing your thoughts with us.

Email: help@starkbank.com
