(ns starkinfra.ai-knowledge-base
  "An AiKnowledgeBase turns a website into material an AiAgent can read. You give it a root URL;
  Stark Infra crawls the page, follows its links, converts everything to Markdown and indexes it for retrieval.
  When you initialize an AiKnowledgeBase, the entity will not be automatically
  created in the Stark Infra API. The 'create' function sends the map
  to the Stark Infra API and returns the created map.

  ## Parameters (required):
    - `:name` [string]: name of the knowledge base. Between 1 and 100 characters. ex: \"Product Documentation\"
    - `:root-url` [string]: absolute http or https URL the crawl starts from. ex: \"https://docs.starkinfra.com\"

  ## Parameters (optional):
    - `:is-recursive` [boolean, default nil]: whether the crawl may follow links into other subdomains of the root URL's registered domain. The API defaults to true. ex: false
    - `:tags` [list of strings, default nil]: list of up to 100 strings for reference when searching for AiKnowledgeBases. ex: [\"support\" \"public\"]

  ## Attributes (return-only):
    - `:id` [string]: unique id returned when the AiKnowledgeBase is created. ex: \"5656565656565656\"
    - `:status` [string]: current status of the knowledge base. Options: \"processing\", \"success\", \"failed\". An agent retrieves from a base only once it reaches \"success\".
    - `:created` [string]: creation datetime for the AiKnowledgeBase. ex: \"2020-03-10T10:30:00.000000+00:00\"
    - `:updated` [string]: latest update datetime for the AiKnowledgeBase. ex: \"2020-03-10T10:30:00.000000+00:00\""
  (:refer-clojure :exclude [get update])
  (:require [cheshire.core :as cheshire]
            [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.request :refer [fetch]]))

;; starkinfra.utils.rest reads the response key from the last word of the path
;; (:base), but this API answers under :knowledge-base and :knowledge-bases, so
;; the functions below call fetch and pick the key themselves.
(defn- resource []
  "ai-knowledge-base")

(defn- content [user method path options]
  (:content (fetch user method path options)))

(defn- page-keys [page]
  {:original-url (clojure.core/get page "originalUrl")
   :storage-url (clojure.core/get page "storageUrl")
   :status (clojure.core/get page "status")})

(defn- id-path [id]
  (str (resource) "/" id))


(defn create
  "Send an AiKnowledgeBase map for creation at the Stark Infra API and start crawling it.
  The call returns immediately with the knowledge base in \"processing\" status.

  ## Parameters (required):
    - `knowledge-base` [map]: AiKnowledgeBase map to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiKnowledgeBase map with updated attributes"
  ([knowledge-base]
   (create knowledge-base @credentials))

  ([knowledge-base user]
   (:knowledge-base (content user :post (resource) {:payload (json/api-json (select-keys knowledge-base [:name :root-url :is-recursive :tags]))}))))

(defn get
  "Receive a single AiKnowledgeBase map previously created in the Stark Infra API by its id.
  This is the call to poll while the crawl runs.

  ## Parameters (required):
    - `id` [string]: map unique id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiKnowledgeBase map that corresponds to the given id"
  ([id]
   (get id @credentials))

  ([id user]
   (:knowledge-base (content user :get (id-path id) {}))))

(defn query
  "Receive a stream of AiKnowledgeBase maps previously created in the Stark Infra API.

  ## Options:
    - `:ids` [list of strings, default nil]: list of ids to filter retrieved maps. ex: [\"5656565656565656\" \"4545454545454545\"]
    - `:name` [string, default nil]: case-insensitive substring of the name to filter retrieved maps. ex: \"docs\"
    - `:status` [string, default nil]: filter for status of retrieved maps. Options: \"processing\", \"success\", \"failed\"
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - stream of AiKnowledgeBase maps with updated attributes"
  ([]
   (query {} @credentials))

  ([params]
   (query params @credentials))

  ([params user]
   ;; this route is not paginated: it answers with every base and rejects limit and cursor (invalidQueryString)
   (:knowledge-bases (content user :get (resource) {:query (select-keys params [:ids :name :status])}))))

(defn update
  "Rename a knowledge base, retag it or change whether its crawl is recursive. The root URL cannot be changed.

  ## Parameters (required):
    - `id` [string]: AiKnowledgeBase id. ex: \"5656565656565656\"

  ## Options:
    - `:name` [string, default nil]: new name of the knowledge base. Between 1 and 100 characters.
    - `:is-recursive` [boolean, default nil]: whether the next crawl may follow links into other subdomains of the root URL's registered domain.
    - `:tags` [list of strings, default nil]: new list of up to 100 strings. Replaces the current list.
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - AiKnowledgeBase map with updated attributes"
  ([id params]
   (update id params @credentials))

  ([id params user]
   (:knowledge-base (content user :patch (id-path id)
                             {:payload (json/api-json (select-keys params [:name :is-recursive :tags]))}))))

(defn hosts
  "Receive every page the crawler has seen, grouped by host. While a crawl is running this is the live picture,
  merged with the last finished one.

  ## Parameters (required):
    - `id` [string]: AiKnowledgeBase id. ex: \"5656565656565656\"

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - map from each host keyword to its list of pages. Each page has `:original-url`, `:storage-url` and `:status` (\"pending\", \"success\" or \"failed\")"
  ([id]
   (hosts id @credentials))

  ([id user]
   ;; fetch kebab-cases every key it parses, which would rewrite a host such as docs2.starkinfra.com
   ;; into docs-2.starkinfra.com, so the body is read raw and only the page keys are renamed
   (let [body (content user :get (str (id-path id) "/hosts") {:as :byte-array})
         hosts (clojure.core/get (cheshire/parse-string (String. ^bytes body "UTF-8")) "hosts")]
     (into {} (map (fn [[host pages]] [(keyword host) (mapv page-keys pages)])) hosts))))

(defn delete
  "Delete up to 100 AiKnowledgeBases at once. Agents still referencing a deleted base simply retrieve nothing from it.

  ## Parameters (required):
    - `ids` [list of strings]: ids of the AiKnowledgeBases to be deleted. Up to 100 ids. ex: [\"5656565656565656\" \"4545454545454545\"]

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of deleted AiKnowledgeBase maps"
  ([ids]
   (delete ids @credentials))

  ([ids user]
   (:knowledge-bases (content user :delete (resource) {:query {:ids ids}}))))
