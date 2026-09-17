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
