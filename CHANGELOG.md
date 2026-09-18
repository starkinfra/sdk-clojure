# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to the following versioning pattern:

Given a version number MAJOR.MINOR.PATCH, increment:

- MAJOR version when the **API** version is incremented. This may include backwards incompatible changes;
- MINOR version when **breaking changes** are introduced OR **new functionalities** are added in a backwards compatible manner;
- PATCH version when backwards compatible bug **fixes** are implemented.


## [Unreleased]
### Added
- project skeleton over com.starkinfra/starkcore 0.2.0, with a `:sandbox` test selector so the offline suite is the default
- starkinfra.settings with the Stark Infra host, api version, timeout, default user and error language
- starkinfra.user with project, organization and organization-replace
- starkinfra.key to generate secp256k1 ECDSA key pairs
- starkinfra.utils.rest wrappers, including a delete-id that carries a query and a put-raw that reaches core's PUT
- starkinfra.utils.json, a JSON writer byte-compatible with CPython's json.dumps, plus api_json casting
- starkinfra.utils.parse with verify and parse-and-verify, retrying over the python-canonical content and one public key refresh
- starkinfra.utils.bacen-id, starkinfra.utils.end-to-end-id and starkinfra.utils.return-id
- starkinfra.request for raw GET, POST, PATCH, PUT and DELETE calls to unmapped routes
- pix-request resource with create, get, query, page, parse and response, and its log sub-resource
- pix-balance resource
- pix-key resource with create, get, query, page, update and cancel, and its log sub-resource
- event resource with get, query, page, delete, update and parse, and its attempt sub-resource
- webhook resource with create, get, query, page and delete
- GitHub Actions CI running lein check and the offline lein test suite
- pix-reversal (create, get, query, page, parse, response), pix-statement (csv), pix-director, pix-domain and pix-key-holmes resources, with logs where sdk-python has them
- pix-claim, pix-infraction (create kept as a deprecated function that throws, as sdk-python's does), pix-fraud, pix-chargeback, pix-dispute, pix-user and pix-internal-transaction-report resources, with their logs
- pix-pull-subscription (parse), pix-pull-request, static-brcode, dynamic-brcode (response-due, response-instant, verify) and brcode-preview resources, with their logs
- issuing-holder, issuing-card (expand on create and get), issuing-rule, issuing-product, issuing-design (pdf), issuing-embossing-kit, issuing-embossing-request, issuing-stock, issuing-restock and issuing-stock-rule resources, with their logs
- issuing-purchase (parse, response), issuing-invoice, issuing-withdrawal, issuing-balance, issuing-transaction, issuing-billing-invoice, issuing-billing-transaction, issuing-token (parse, response-authorization, response-activation), issuing-token-activation (parse), issuing-token-design (pdf), issuing-token-request, merchant-category, merchant-country and card-method resources, with their logs
- credit-note (pdf, payment), credit-signer (resend-token), credit-preview, credit-holmes, ledger and ledger-transaction resources, with their logs
- individual-identity, individual-account-request, individual-account-attachment, business-identity, business-attachment and business-account-request resources, with their logs
- README usage sections for every resource, mirroring the sdk-python README
- documentation namespaces for the nested sub-resources that are hydrated as plain maps and have no verbs: brcode-preview.subscription, credit-note.invoice, ledger.rule, pix-dispute.transaction and pix-user.statistics
### Changed
- the SDK owns its HTTP layer: `starkinfra.utils.request` (URL, query encoding, signing, status mapping) and `starkinfra.utils.case` replace `core-clojure.utils.request` and `core-clojure.utils.case`, which are kept only for user validation, key generation and the endpoint/envelope helpers. core-clojure 0.2.0 dropped falsy query members (`{:is-delivered false}` never reached the URL), prefixed the query string with `/?`, raised `IllegalArgumentException: No matching clause` for any status other than 400 or 500, signed an Organization as `project/<id>` so a Workspace scope was lost, returned raw pdf/csv bodies as Strings, split camelCase only on letter boundaries (`displayName1` arrived as `:displayname1`), and paged with arithmetic that sent a `limit=0` request and re-fetched page one on an empty cursor. Each of those is now mirrored from core-python
- `pdf`, `payment` and `csv` return a `byte[]` instead of a String, so binary content survives
- API errors throw `ex-info` whose `ex-data` carries `:status` alongside `:errors`, for every status

### Notes
- mirrors sdk-python at 271c190d, which removed IndividualDocument the day this SDK was derived, so that resource is not included
