(ns starkinfra.user
  "Used to define API user."
  (:require [core-clojure.user.organization :as core-organization]
            [core-clojure.user.project :as core-project]
            [starkinfra.utils.request :refer [access-id]]))


(defn- signable
  "core-clojure 0.2.0 labels every access id `project/...`, even an
  Organization's, and stores the *parsed* private key for an Organization while
  a Project keeps the pem. The request layer signs with the pem and the real
  access id, so both are restored here, at the only boundary that builds a
  user."
  [user private-key]
  (assoc user :private-key private-key :access-id (access-id user)))


(defn project
  "The Project map is an authentication entity for the SDK that is permanently
  linked to a specific Workspace.
  All requests to the Stark Infra API must be authenticated via an SDK user,
  which must have been previously created at the Stark Infra website
  [https://web.sandbox.starkinfra.com] or [https://web.starkinfra.com]
  before you can use it in this SDK. Projects may be passed as the user parameter on
  each request or may be defined as the default user at the start (See README).

  ## Parameters (required):
    - `environment` [string]: environment where the project is being used. ex: \"sandbox\" or \"production\"
    - `id` [string]: unique id required to identify project. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the project. ex: \"-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIMCwW74H6egQkTiz87WDvLNm7fK/cA+ctA2vg/bbHx3woAcGBSuBBAAK\noUQDQgAE0iaeEHEgr3oTbCfh8U2L+r7zoaeOX964xaAnND5jATGpD/tHec6Oe9U1\nIF16ZoTVt1FzZ8WkYQ3XomRD4HS13A==\n-----END EC PRIVATE KEY-----\"

  ## Return:
    - Project map"
  [environment id private-key]
  (signable (core-project/project environment id private-key) private-key))

(defn organization
  "The Organization map is an authentication entity for the SDK that
  represents your entire Organization, being able to access any Workspace
  underneath it and even create new Workspaces. Only a legal representative
  of your organization can register or change the Organization credentials.
  All requests to the Stark Infra API must be authenticated via an SDK user,
  which must have been previously created at the Stark Infra website
  [https://web.sandbox.starkinfra.com] or [https://web.starkinfra.com]
  before you can use it in this SDK. Organizations may be passed as the user parameter on
  each request or may be defined as the default user at the start (See README).
  If you are accessing a specific Workspace using Organization credentials, you should
  specify the workspace ID when building the Organization map or by request, using
  the starkinfra.user/organization-replace function, which creates a copy of the
  organization map with the altered workspace ID. If you are listing or creating new
  Workspaces, the workspace-id should be nil.

  ## Parameters (required):
    - `environment` [string]: environment where the organization is being used. ex: \"sandbox\" or \"production\"
    - `id` [string]: unique id required to identify organization. ex: \"5656565656565656\"
    - `private-key` [string]: PEM string of the private key linked to the organization. ex: \"-----BEGIN EC PRIVATE KEY-----\nMHQCAQEEIMCwW74H6egQkTiz87WDvLNm7fK/cA+ctA2vg/bbHx3woAcGBSuBBAAK\noUQDQgAE0iaeEHEgr3oTbCfh8U2L+r7zoaeOX964xaAnND5jATGpD/tHec6Oe9U1\nIF16ZoTVt1FzZ8WkYQ3XomRD4HS13A==\n-----END EC PRIVATE KEY-----\"

  ## Parameters (optional):
    - `workspace-id` [string, default nil]: unique id of the accessed Workspace, if any. ex: nil or \"4848484848484848\"

  ## Return:
    - Organization map"
  ([environment id private-key]
   (organization environment id private-key nil))

  ([environment id private-key workspace-id]
   (signable (core-organization/organization environment id private-key workspace-id)
             private-key)))

(defn organization-replace
  "Creates a copy of the Organization map with the altered workspace ID.

  ## Parameters (required):
    - `organization` [map]: Organization map returned from starkinfra.user/organization
    - `workspace-id` [string]: unique id of the Workspace to be accessed. ex: \"4848484848484848\"

  ## Return:
    - Organization map pointing at the given Workspace"
  [organization workspace-id]
  (let [scoped (core-organization/orgaization-replace organization workspace-id)]
    (assoc scoped :access-id (access-id scoped))))
