(ns starkinfra.utils.ai-fixtures
  (:require [starkinfra.ai-agent :as ai-agent]
            [starkinfra.ai-chat :as ai-chat]
            [starkinfra.ai-speech :as ai-speech]
            [starkinfra.ai-voice :as ai-voice]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:dynamic *test-agent* nil)

(def ^:dynamic *chat* nil)


(defn random-name [prefix]
  (str prefix "-" (apply str (repeatedly 12 #(format "%x" (rand-int 16))))))

(defn example-agent
  ([]
   (example-agent nil))
  ([knowledge-base-ids]
   {:name (random-name "sdk-clojure-agent")
    :model "bender-1.0"
    :system-prompt "Answer in one short sentence."
    :knowledge-base-ids knowledge-base-ids
    :metadata-schema {:order-id {:type "string" :description "Order the customer mentions"}}}))

(defn with-chat
  "A clojure.test :once fixture: an agent and a chat of it for the namespace, deleted when it ends."
  [tests]
  (set-project)
  (let [agent (ai-agent/create (example-agent))
        chat (ai-chat/create {:agent-id (:id agent) :title (random-name "sdk-clojure-chat")})]
    (try
      (binding [*test-agent* agent
                *chat* chat]
        (tests))
      (finally
        (ai-chat/delete [(:id chat)])
        (ai-agent/delete [(:id agent)])))))

(defn finished-speech []
  (when-let [finished (first (filter #(= "success" (:status %)) (ai-speech/query {:limit 100})))]
    (ai-speech/get (:id finished))))

(defn finished-voice []
  (first (filter #(= "success" (:status %)) (ai-voice/query {:limit 100}))))
