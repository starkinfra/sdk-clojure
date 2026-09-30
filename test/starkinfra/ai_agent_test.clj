(ns starkinfra.ai-agent-test
  "Mirrors sdk-python tests/sdk/testAiAgent.py. The live tests share the knowledge base and agent built
  by starkinfra.utils.ai-fixtures; the HTTP boundary tests replace only clj-http's request."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.ai-agent :as ai-agent]
            [starkinfra.utils.ai-fixtures :refer [agent-cell answer error-codes example-agent
                                                  knowledge-base-cell project replying
                                                  sent-body sent-params sent-path thrown]]
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

(deftest ^:sandbox query-and-page-list-the-agent
  (set-project)
  (let [agent @agent-cell
        queried (doall (ai-agent/query {:limit 5 :expand [:knowledge-bases]}))
        page (ai-agent/page {:limit 100})]
    (is (some #{(:id agent)} (map :id (ai-agent/query))))
    (is (<= (count queried) 5))
    (is (vector? (:content page)))
    (is (contains? page :cursor))))

(deftest ^:sandbox page-follows-the-cursor-to-the-next-page
  (set-project)
  @agent-cell
  (let [first-page (ai-agent/page {:limit 1})]
    (when (:cursor first-page)
      (let [second-page (ai-agent/page {:limit 1 :cursor (:cursor first-page)})]
        (is (not= (map :id (:content first-page)) (map :id (:content second-page))))))))

(deftest ^:sandbox update-changes-what-is-given-and-keeps-what-is-not
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

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [exception (thrown #(ai-agent/page {:limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

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

(deftest create-drops-the-optional-fields-it-was-not-given
  (let [sent (answer {:agent documented-agent}
                     #(ai-agent/create {:name "a" :model "bender-1.0"} @project))]
    (is (= {:name "a" :model "bender-1.0"} (sent-body sent)))))

(deftest update-names-all-six-keys-sends-absent-ones-as-null-and-reads-nothing-first
  (let [sent (replying [{:agent documented-agent}]
                       #(ai-agent/update "5740688905863168" {:name "Renamed"} @project))
        written (first sent)]
    (is (= 1 (count sent)))
    (is (= :patch (:method written)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent/5740688905863168" (:url written)))
    (is (= {:name "Renamed"
            :model nil
            :systemPrompt nil
            :voiceId nil
            :knowledgeBaseIds nil
            :metadataSchema nil}
           (sent-body written)))))

(deftest update-sends-empty-values-so-the-api-clears-the-fields
  (let [written (answer {:agent documented-agent}
                        #(ai-agent/update "5740688905863168"
                                          {:system-prompt "" :knowledge-base-ids [] :metadata-schema {}}
                                          @project))]
    (is (= {:name nil
            :model nil
            :systemPrompt ""
            :voiceId nil
            :knowledgeBaseIds []
            :metadataSchema {}}
           (sent-body written)))))

(deftest update-sends-the-schema-keys-as-written
  (let [written (answer {:agent documented-agent}
                        #(ai-agent/update "5740688905863168"
                                          {:metadata-schema {:order_id {:type "string"} "order-id" {:type "string"} :orderId {:type "string"}}}
                                          @project))]
    (is (= {:order_id {:type "string"} :order-id {:type "string"} :orderId {:type "string"}}
           (:metadataSchema (sent-body written))))))

(deftest get-sends-expand-and-reads-the-agent-key
  (let [result (atom nil)
        sent (answer {:agent documented-agent}
                     #(reset! result (ai-agent/get "5740688905863168" {:expand [:knowledge-bases]} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent/5740688905863168" (sent-path sent)))
    (is (= {"expand" "knowledgeBases"} (sent-params sent)))
    (is (= {:order_id {:type "string"} :isUrgent {:type "boolean"}} (:metadata-schema @result)))))

(deftest page-returns-the-items-and-the-cursor-and-sends-its-parameters
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :agents [documented-agent]}
                     #(reset! result (ai-agent/page {:cursor "c1" :limit 2 :expand [:knowledge-bases]} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent" (sent-path sent)))
    (is (= {"cursor" "c1" "limit" "2" "expand" "knowledgeBases"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["5740688905863168"] (map :id (:content @result))))
    (is (= {:order_id {:type "string"} :isUrgent {:type "boolean"}} (:metadata-schema (first (:content @result)))))))

(deftest page-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)]
    (answer {:cursor nil :agents [documented-agent]}
            #(reset! result (ai-agent/page {} @project)))
    (is (nil? (:cursor @result)))))

(deftest query-follows-the-cursor-through-empty-pages-and-honours-the-limit
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :agents (vec (repeat 100 documented-agent))}
                        {:cursor "c2" :agents []}
                        {:cursor "c3" :agents (vec (repeat 50 documented-agent))}]
                       #(reset! result (doall (ai-agent/query {:limit 150} @project))))]
    (is (= 150 (count @result)))
    (is (= [{"limit" "100"} {"limit" "50" "cursor" "c1"} {"limit" "50" "cursor" "c2"}]
           (map sent-params sent)))))

(deftest query-without-limit-ends-when-the-cursor-is-nil
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :agents [documented-agent]}
                        {:cursor nil :agents [documented-agent]}]
                       #(reset! result (doall (ai-agent/query {:expand [:knowledge-bases]} @project))))]
    (is (= 2 (count @result)))
    (is (= [{"expand" "knowledgeBases"} {"expand" "knowledgeBases" "cursor" "c1"}]
           (map sent-params sent)))))

(deftest query-stops-at-an-empty-string-cursor
  (let [result (atom nil)
        sent (replying [{:cursor "" :agents [documented-agent]}
                        {:cursor nil :agents [documented-agent]}]
                       #(reset! result (doall (ai-agent/query {} @project))))]
    (is (= 1 (count sent)))
    (is (= 1 (count @result)))))

(deftest query-stops-at-a-nil-cursor-on-a-non-empty-page
  (let [result (atom nil)
        sent (replying [{:cursor nil :agents [documented-agent documented-agent]}
                        {:cursor nil :agents [documented-agent]}]
                       #(reset! result (doall (ai-agent/query {} @project))))]
    (is (= 1 (count sent)))
    (is (= 2 (count @result)))))

(deftest query-keeps-following-an-empty-page-that-carries-a-cursor
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :agents []}
                        {:cursor nil :agents [documented-agent]}]
                       #(reset! result (doall (ai-agent/query {} @project))))]
    (is (= 2 (count sent)))
    (is (= 1 (count @result)))))

(deftest delete-sends-ids-in-the-query-string-and-returns-the-deleted-objects
  (let [result (atom nil)
        sent (answer {:agents [documented-agent]}
                     #(reset! result (ai-agent/delete ["5740688905863168" "5740688905863169"] @project)))]
    (is (= :delete (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-agent?ids=5740688905863168%2C5740688905863169" (:url sent)))
    (is (= "" (:body sent)))
    (is (= ["5740688905863168"] (map :id @result)))))
