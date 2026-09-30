(ns starkinfra.utils.ai-fixtures
  "Shared plumbing of the AI test namespaces, mirroring sdk-python tests/utils/aiFixtures.py.
  One knowledge base, agent and chat are built on first use, shared by every namespace in the process
  and removed by a shutdown hook, because no single namespace knows when the last one is done. The audio
  for voices and transcripts comes from a finished speech of the workspace."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [starkinfra.ai-agent :as ai-agent]
            [starkinfra.ai-chat :as ai-chat]
            [starkinfra.ai-knowledge-base :as ai-knowledge-base]
            [starkinfra.ai-message :as ai-message]
            [starkinfra.key :as key]
            [starkinfra.ai-speech :as ai-speech]
            [starkinfra.ai-voice :as ai-voice]
            [starkinfra.user :as user])
  (:import (java.net URLDecoder)))

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

(defn sent-params
  "The decoded query string of a recorded request, as a map of strings."
  [request]
  (let [query (second (string/split (:url request) #"\?" 2))]
    (into {}
          (map (fn [pair]
                 (let [[name value] (string/split pair #"=" 2)]
                   [name (URLDecoder/decode ^String value "UTF-8")])))
          (when query (string/split query #"&")))))

(defn sent-path [request]
  (first (string/split (:url request) #"\?" 2)))



(declare knowledge-base-cell agent-cell chat-cell)

(defn- cleanup []
  (doseq [[cell delete] [[chat-cell ai-chat/delete]
                         [agent-cell ai-agent/delete]
                         [knowledge-base-cell ai-knowledge-base/delete]]]
    (when (realized? cell)
      (delete [(:id @cell)]))))

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

(def finished-speech-cell
  (delay (let [finished (first (filter #(= "success" (:status %)) (ai-speech/query {:limit 100})))]
           (when finished
             (ai-speech/get (:id finished))))))

(def finished-voice-cell
  (delay (first (filter #(= "success" (:status %)) (ai-voice/query {:limit 100})))))

(def posted-cell
  (delay (ai-message/create {:chat-id (:id @chat-cell) :text "Say hello and mention order 123."}
                            {:expand [:chat-name]})))
