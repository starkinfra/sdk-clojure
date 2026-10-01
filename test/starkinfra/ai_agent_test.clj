(ns starkinfra.ai-agent-test
  "Mirrors sdk-python tests/sdk/testAiAgent.py. The live tests share the knowledge base and agent built
  by starkinfra.utils.ai-fixtures; the HTTP boundary tests replace only clj-http's request."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.ai-agent :as ai-agent]
            [starkinfra.utils.ai-fixtures :as fixtures :refer [agent-cell answer error-codes example-agent
                                                               knowledge-base-cell project replying
                                                               sent-body thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private documented-agent
  {:id "5740688905863168"
   :name "Support assistant"
   :model "bender-1.0"
   :systemPrompt "Answer in one short sentence."
   :voiceId ""
   :knowledgeBaseIds ["5083538508480512"]
   :metadataSchema {:order_id {:type "string"} :isUrgent {:type "boolean"}}
   :created "2026-09-30T15:42:56.879325+00:00"
   :updated "2026-09-30T15:42:56.879334+00:00"})


(deftest ^:sandbox create-keeps-the-schema-keys-as-written
  (set-project)
  (let [agent @agent-cell]
    (is (string? (:id agent)))
    (is (= "bender-1.0" (:model agent)))
    (is (= [(:id @knowledge-base-cell)] (:knowledge-base-ids agent)))
    (is (= [:order_id] (keys (:metadata-schema agent))))
    (is (= "string" (get-in agent [:metadata-schema :order_id :type])))
    (is (string? (:created agent)))))

(deftest ^:sandbox get-and-expand-knowledge-bases
  (set-project)
  (let [agent @agent-cell
        plain (ai-agent/get (:id agent))
        expanded (ai-agent/get (:id agent) {:expand [:knowledge-bases]})]
    (is (= (:id agent) (:id plain)))
    (is (nil? (:knowledge-bases plain)))
    (is (= [(:id @knowledge-base-cell)] (map :id (:knowledge-bases expanded))))))

(deftest ^:sandbox query-with-fields
  (set-project)
  (let [agent @agent-cell
        found (first (filter #(= (:id agent) (:id %)) (ai-agent/query {:fields [:id :name]})))]
    (is (= (:name agent) (:name found)))
    (is (nil? (:model found)))))

(deftest ^:sandbox update-keeps-the-knowledge-bases-it-was-not-asked-to-change
  (set-project)
  (let [agent @agent-cell
        original (ai-agent/get (:id agent))]
    (try
      (let [renamed (ai-agent/update (:id agent) {:name "renamed-by-sdk"})]
        (is (= "renamed-by-sdk" (:name renamed)))
        (is (= [(:id @knowledge-base-cell)] (:knowledge-base-ids renamed)))
        (is (= [:order_id] (keys (:metadata-schema renamed)))))
      (finally
        (ai-agent/update (:id agent) {:name (:name original)})))))

(deftest ^:sandbox update-with-an-empty-list-clears-the-knowledge-bases
  (set-project)
  (let [agent (ai-agent/create (example-agent [(:id @knowledge-base-cell)]))]
    (try
      (is (= [] (:knowledge-base-ids (ai-agent/update (:id agent) {:knowledge-base-ids []}))))
      (finally
        (ai-agent/delete [(:id agent)])))))

(deftest ^:sandbox delete-returns-the-deleted-agents
  (set-project)
  (let [agent (ai-agent/create (example-agent))]
    (is (= [(:id agent)] (map :id (ai-agent/delete [(:id agent)]))))))

(deftest ^:sandbox create-with-an-invalid-model-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-agent/create {:name "invalid" :model "gpt"}))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-agent/get "0000000000000000"))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

(deftest create-sends-only-the-creatable-fields-and-does-not-touch-the-schema-keys
  (let [returned {:name "Support assistant"
                  :model "bender-1.0"
                  :system-prompt "Be brief."
                  :voice-id "5632499082330112"
                  :knowledge-base-ids ["5083538508480512"]
                  :metadata-schema {:order_id {:type "string"} :isUrgent {:type "boolean"} "snake-key" {:type "string"}}
                  :id "5740688905863168"
                  :created "2026-09-30T15:42:56+00:00"
                  :updated "2026-09-30T15:42:56+00:00"}
        result (atom nil)
        sent (answer {:agent documented-agent}
                     #(reset! result (ai-agent/create returned @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent" (:url sent)))
    (is (= {:name "Support assistant"
            :model "bender-1.0"
            :systemPrompt "Be brief."
            :voiceId "5632499082330112"
            :knowledgeBaseIds ["5083538508480512"]
            :metadataSchema {:order_id {:type "string"} :isUrgent {:type "boolean"} :snake-key {:type "string"}}}
           (sent-body sent)))
    (testing "the answer keeps the schema keys as the API wrote them and renames the fixed names"
      (is (= {:order_id {:type "string"} :isUrgent {:type "boolean"}} (:metadata-schema @result)))
      (is (= ["5083538508480512"] (:knowledge-base-ids @result)))
      (is (= "Answer in one short sentence." (:system-prompt @result))))))

(deftest an-agent-fetched-without-a-voice-can-be-created-again
  (let [sent (answer {:agent documented-agent}
                     #(ai-agent/create {:name "a" :model "bender-1.0" :voice-id ""} @project))]
    (is (= {:name "a" :model "bender-1.0"} (sent-body sent)))))

(deftest update-without-knowledge-base-ids-reads-them-first-and-sends-them-back
  (let [[read written] (replying [{:agent {:id "5740688905863168" :knowledgeBaseIds ["5083538508480512"]}}
                                  {:agent documented-agent}]
                                 #(ai-agent/update "5740688905863168" {:name "Renamed"} @project))]
    (is (= :get (:method read)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent/5740688905863168?fields=knowledgeBaseIds" (:url read)))
    (is (= :patch (:method written)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent/5740688905863168" (:url written)))
    (is (= {:name "Renamed" :knowledgeBaseIds ["5083538508480512"]} (sent-body written)))))

(deftest update-with-knowledge-base-ids-does-not-read-the-agent
  (let [sent (replying [{:agent documented-agent}]
                       #(ai-agent/update "5740688905863168" {:knowledge-base-ids []} @project))]
    (is (= 1 (count sent)))
    (is (= :patch (:method (first sent))))
    (is (= {:knowledgeBaseIds []} (sent-body (first sent))))))

(deftest query-and-get-send-fields-and-expand-only
  (let [[listed fetched] (replying [{:agents [documented-agent]} {:agent documented-agent}]
                                   #(do (doall (ai-agent/query {:fields [:id :knowledge-bases] :expand [:knowledge-bases]} @project))
                                        (ai-agent/get "5740688905863168" {:expand [:knowledge-bases]} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent?fields=id%2CknowledgeBases&expand=knowledgeBases" (:url listed)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent/5740688905863168?expand=knowledgeBases" (:url fetched)))))

(deftest delete-sends-ids-in-the-query-string-and-returns-the-deleted-objects
  (let [result (atom nil)
        sent (answer {:agents [documented-agent]}
                     #(reset! result (ai-agent/delete ["5740688905863168" "5740688905863169"] @project)))]
    (is (= :delete (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent?ids=5740688905863168%2C5740688905863169" (:url sent)))
    (is (= "" (:body sent)))
    (is (= ["5740688905863168"] (map :id @result)))))
