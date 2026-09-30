(ns starkinfra.utils.ai-fixtures
  "Shared plumbing of the AI test namespaces, mirroring sdk-python tests/utils/aiFixtures.py.
  Voices, speeches and transcripts cannot be deleted in the sandbox, so they are only created at the
  HTTP boundary (`answer`). Knowledge bases, agents and chats can, so one of each is built on first
  use, shared by every namespace in the process and removed by a shutdown hook, because no single
  namespace knows when the last one is done."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [starkinfra.ai-agent :as ai-agent]
            [starkinfra.ai-chat :as ai-chat]
            [starkinfra.ai-knowledge-base :as ai-knowledge-base]
            [starkinfra.ai-message :as ai-message]
            [starkinfra.key :as key]
            [starkinfra.user :as user]))

(defn random-name [prefix]
  (str prefix "-" (apply str (repeatedly 12 #(format "%x" (rand-int 16))))))

(defn thrown [f]
  (try
    (f)
    nil
    (catch clojure.lang.ExceptionInfo exception exception)))

(defn error-codes [exception]
  (map :code (:errors (ex-data exception))))

(def project
  (delay (user/project "sandbox" "5656565656565656" (:private-pem (key/create)))))

(defn replying
  "Replaces clj-http's request with a stand-in that records every request and answers 200 with the next
  of `bodies`. Raw reads (`:as :byte-array`) get bytes, as the real client returns them. Returns the
  recorded requests, in order."
  [bodies f]
  (let [sent (atom [])
        remaining (atom bodies)]
    (with-redefs [client/request (fn [request]
                                   (swap! sent conj request)
                                   (let [json (cheshire/generate-string (first @remaining))]
                                     (swap! remaining rest)
                                     {:status 200
                                      :body (if (= :byte-array (:as request)) (.getBytes json "UTF-8") json)}))]
      (f))
    @sent))

(defn answer [body f]
  (first (replying [body] f)))

(defn sent-body [request]
  (cheshire/parse-string (:body request) true))


(defn- delete-reporting-500 [label delete entity]
  (try
    (delete [(:id entity)])
    (catch clojure.lang.ExceptionInfo exception
      (when-not (= 500 (:status (ex-data exception)))
        (throw exception))
      (binding [*out* *err*]
        (println (str label " " (:id entity) " was not deleted: the API answered 500"))))))

(declare knowledge-base-cell agent-cell chat-cell)

(defn- cleanup []
  (doseq [[cell label delete] [[chat-cell "AiChat" ai-chat/delete]
                               [agent-cell "AiAgent" ai-agent/delete]
                               [knowledge-base-cell "AiKnowledgeBase" ai-knowledge-base/delete]]]
    (when (realized? cell)
      (delete-reporting-500 label delete @cell))))

(def ^:private cleanup-hook
  (delay (.addShutdownHook (Runtime/getRuntime) (Thread. ^Runnable cleanup))))

(defn- built [create]
  @cleanup-hook
  (create))

(def knowledge-base-cell
  (delay (built #(ai-knowledge-base/create {:name (random-name "sdk-clojure-kb")
                                            :root-url "https://docs.starkinfra.com"
                                            :is-recursive false
                                            :tags ["sdk-clojure" "test"]}))))

(defn example-agent
  ([]
   (example-agent nil))
  ([knowledge-base-ids]
   {:name (random-name "sdk-clojure-agent")
    :model "bender-1.0"
    :system-prompt "Answer in one short sentence."
    :knowledge-base-ids knowledge-base-ids
    :metadata-schema {:order_id {:type "string" :description "Order the customer mentions"}}}))

(def agent-cell
  (delay (built #(ai-agent/create (example-agent [(:id @knowledge-base-cell)])))))

(def chat-cell
  (delay (built #(ai-chat/create {:agent-id (:id @agent-cell) :title (random-name "sdk-clojure-chat")}))))

(def posted-cell
  (delay (ai-message/create {:chat-id (:id @chat-cell) :text "Say hello and mention order 123."}
                            {:expand [:chat-name]})))
