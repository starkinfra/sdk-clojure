(ns starkinfra.pix-director
  "Mandatory data that must be registered within the Central Bank for emergency contact purposes.
  When you initialize a PixDirector, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:name` [string]: name of the PixDirector. ex: \"Edward Stark\"
    - `:tax-id` [string]: tax ID (CPF) of the PixDirector. ex: \"012.345.678-90\"
    - `:phone` [string]: phone of the PixDirector. ex: \"+551198989898\"
    - `:email` [string]: email of the PixDirector. ex: \"ned.stark@starkbank.com\"
    - `:password` [string]: password of the PixDirector. ex: \"12345678\"
    - `:team-email` [string]: team email. ex: \"pix.team@company.com\"
    - `:team-phones` [list of strings]: list of phones of the team. ex: [\"+5511988889999\", \"+5511988889998\"]

  ## Attributes (return-only):
    - `:status` [string]: current PixDirector status. ex: \"success\""
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [post-single]]))

(defn- resource []
  "pix-director")


(defn create
  "Send a PixDirector map for creation at the Stark Infra API.

  ## Parameters (required):
    - `director` [map]: PixDirector map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - PixDirector map with updated attributes"
  ([director]
   (post-single @credentials (resource) director {}))

  ([director user]
   (post-single user (resource) director {})))
