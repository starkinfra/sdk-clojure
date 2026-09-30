(ns starkinfra.ai-chat-test
  "Mirrors sdk-python tests/sdk/testAiChat.py. The live tests share the chat built by
  starkinfra.utils.ai-fixtures; the HTTP boundary tests replace only clj-http's request."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-chat :as ai-chat]
            [starkinfra.utils.ai-fixtures :refer [agent-cell answer chat-cell error-codes project replying
                                                  sent-body thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private documented-chat
  {:id "5632499082330112"
   :agentId "5740688905863168"
   :title "Greeting"
   :updated "2026-10-01T14:28:02.652375+00:00"})


(deftest ^:sandbox create-returns-the-chat
  (set-project)
  (let [chat @chat-cell]
    (is (string? (:id chat)))
    (is (= (:id @agent-cell) (:agent-id chat)))
    (is (string? (:title chat)))
    (is (string? (:updated chat)))))

(deftest ^:sandbox get-and-expand-agent-name
  (set-project)
  (let [chat @chat-cell
        plain (ai-chat/get (:id chat))
        expanded (ai-chat/get (:id chat) {:expand [:agent-name]})]
    (is (= (:id chat) (:id plain)))
    (is (nil? (:agent-name plain)))
    (is (= (:name @agent-cell) (:agent-name expanded)))))

(deftest ^:sandbox query-with-fields
  (set-project)
  (let [chat @chat-cell
        found (first (filter #(= (:id chat) (:id %)) (ai-chat/query {:fields [:id :title]})))]
    (is (= (:title chat) (:title found)))
    (is (nil? (:agent-id found)))))

(deftest ^:sandbox update-changes-the-title-only
  (set-project)
  (let [chat @chat-cell
        original (ai-chat/get (:id chat))]
    (try
      (let [renamed (ai-chat/update (:id chat) {:title "renamed-by-sdk"})]
        (is (= "renamed-by-sdk" (:title renamed)))
        (is (= (:agent-id chat) (:agent-id renamed))))
      (finally
        (ai-chat/update (:id chat) {:title (:title original)})))))

(deftest ^:sandbox delete-returns-the-deleted-chats
  (set-project)
  (let [chat (ai-chat/create {:agent-id (:id @agent-cell) :title "to-delete"})]
    (is (= [(:id chat)] (map :id (ai-chat/delete [(:id chat)]))))))

(deftest ^:sandbox create-in-an-unknown-agent-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-chat/create {:agent-id "0000000000000000"}))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-chat/get "0000000000000000"))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

(deftest create-sends-only-the-creatable-fields
  (let [result (atom nil)
        sent (answer {:chat documented-chat}
                     #(reset! result (ai-chat/create {:agent-id "5740688905863168"
                                                      :title "Greeting"
                                                      :id "5632499082330112"
                                                      :agent-name "Support assistant"
                                                      :updated "2026-10-01T14:28:02.652375+00:00"}
                                                     @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat" (:url sent)))
    (is (= {:agentId "5740688905863168" :title "Greeting"} (sent-body sent)))
    (is (= "5632499082330112" (:id @result)))
    (is (= "5740688905863168" (:agent-id @result)))))

(deftest update-sends-title-and-agent-id-only
  (let [sent (answer {:chat documented-chat}
                     #(ai-chat/update "5632499082330112" {:title "Greeting" :agent-id "5740688905863168" :id "x"} @project))]
    (is (= :patch (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat/5632499082330112" (:url sent)))
    (is (= {:title "Greeting" :agentId "5740688905863168"} (sent-body sent)))))

(deftest update-with-no-fields-sends-an-empty-json-object
  (let [sent (answer {:chat documented-chat}
                     #(ai-chat/update "5632499082330112" {} @project))]
    (is (= "{}" (:body sent)))))

(deftest query-and-get-send-fields-and-expand-only
  (let [[listed fetched] (replying [{:chats [documented-chat]} {:chat documented-chat}]
                                   #(do (doall (ai-chat/query {:fields [:id :agent-name] :expand [:agent-name]} @project))
                                        (ai-chat/get "5632499082330112" {:expand [:agent-name]} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat?fields=id%2CagentName&expand=agentName" (:url listed)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat/5632499082330112?expand=agentName" (:url fetched)))))

(deftest delete-sends-ids-in-the-query-string-and-returns-the-deleted-objects
  (let [result (atom nil)
        sent (answer {:chats [documented-chat]}
                     #(reset! result (ai-chat/delete ["5632499082330112"] @project)))]
    (is (= :delete (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat?ids=5632499082330112" (:url sent)))
    (is (= "" (:body sent)))
    (is (= ["5632499082330112"] (map :id @result)))))
