(ns starkinfra.ai-chat-test
  "Mirrors sdk-python tests/sdk/testAiChat.py. The live tests share the chat built by
  starkinfra.utils.ai-fixtures; the HTTP boundary tests replace only clj-http's request."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-chat :as ai-chat]
            [starkinfra.utils.ai-fixtures :refer [agent-cell answer chat-cell error-codes project replying
                                                  sent-body sent-params sent-path thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private documented-chat
  {:id "5632499082330112"
   :agentId "5740688905863168"
   :title "Greeting"
   :tags ["support" "vip"]
   :context {:order_id "123" :isVip true}
   :updated "2026-10-01T14:28:02.652375+00:00"})


(deftest ^:sandbox create-returns-the-chat
  (set-project)
  (let [chat @chat-cell]
    (is (string? (:id chat)))
    (is (= (:id @agent-cell) (:agent-id chat)))
    (is (string? (:title chat)))
    (is (string? (:updated chat)))
    (is (= [] (:tags chat)))
    (is (= {} (:context chat)))))

(deftest ^:sandbox create-update-and-delete-with-tags-and-context
  (set-project)
  (let [chat (ai-chat/create {:agent-id (:id @agent-cell)
                              :title "with-context"
                              :tags ["sdk-clojure" "a"]
                              :context {:order_id "123" :isVip true}})]
    (try
      (is (= ["sdk-clojure" "a"] (:tags chat)))
      (is (= {:order_id "123" :isVip true} (:context chat)))
      (let [kept (ai-chat/update (:id chat) {:title "renamed-by-sdk"})]
        (is (= "renamed-by-sdk" (:title kept)))
        (is (= ["sdk-clojure" "a"] (:tags kept)))
        (is (= {:order_id "123" :isVip true} (:context kept))))
      (let [cleared (ai-chat/update (:id chat) {:tags [] :context {}})]
        (is (= [] (:tags cleared)))
        (is (= {} (:context cleared))))
      (finally
        (ai-chat/delete [(:id chat)])))))

(deftest ^:sandbox get-and-expand-agent-name
  (set-project)
  (let [chat @chat-cell
        plain (ai-chat/get (:id chat))
        expanded (ai-chat/get (:id chat) {:expand [:agent-name]})]
    (is (= (:id chat) (:id plain)))
    (is (nil? (:agent-name plain)))
    (is (= (:name @agent-cell) (:agent-name expanded)))))

(deftest ^:sandbox query-and-page-list-the-chat-and-filter-by-tags
  (set-project)
  (let [tagged (ai-chat/create {:agent-id (:id @agent-cell) :title "tagged" :tags ["sdk-clojure-a" "sdk-clojure-b"]})]
    (try
      (is (some #{(:id @chat-cell)} (map :id (ai-chat/query))))
      (is (= [(:id tagged)] (map :id (ai-chat/query {:tags ["sdk-clojure-a" "sdk-clojure-b"]}))))
      (let [page (ai-chat/page {:limit 100 :tags ["sdk-clojure-a"] :expand [:agent-name]})]
        (is (= [(:id tagged)] (map :id (:content page))))
        (is (= (:name @agent-cell) (:agent-name (first (:content page)))))
        (is (contains? page :cursor)))
      (finally
        (ai-chat/delete [(:id tagged)])))))

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

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [exception (thrown #(ai-chat/page {:limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

(deftest create-sends-only-the-creatable-fields-with-tags-and-context-as-written
  (let [result (atom nil)
        sent (answer {:chat documented-chat}
                     #(reset! result (ai-chat/create {:agent-id "5740688905863168"
                                                      :title "Greeting"
                                                      :tags ["support" "vip"]
                                                      :context {:order_id "123" :isVip true "snake-key" 1}
                                                      :id "5632499082330112"
                                                      :agent-name "Support assistant"
                                                      :updated "2026-10-01T14:28:02.652375+00:00"}
                                                     @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat" (:url sent)))
    (is (= {:agentId "5740688905863168"
            :title "Greeting"
            :tags ["support" "vip"]
            :context {:order_id "123" :isVip true :snake-key 1}}
           (sent-body sent)))
    (is (= "5632499082330112" (:id @result)))
    (is (= "5740688905863168" (:agent-id @result)))
    (is (= ["support" "vip"] (:tags @result)))
    (is (= {:order_id "123" :isVip true} (:context @result)))))

(deftest create-drops-the-optional-fields-it-was-not-given
  (let [sent (answer {:chat documented-chat}
                     #(ai-chat/create {:agent-id "5740688905863168"} @project))]
    (is (= {:agentId "5740688905863168"} (sent-body sent)))))

(deftest update-names-every-key-and-sends-absent-ones-as-null
  (let [sent (answer {:chat documented-chat}
                     #(ai-chat/update "5632499082330112" {:title "Greeting" :id "x"} @project))]
    (is (= :patch (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat/5632499082330112" (:url sent)))
    (is (= {:title "Greeting" :agentId nil :tags nil :context nil} (sent-body sent)))))

(deftest update-sends-tags-and-context-and-the-empty-values-that-clear-them
  (let [filled (answer {:chat documented-chat}
                       #(ai-chat/update "5632499082330112"
                                        {:tags ["a"] :context {:order_id "1" "order-id" 2}}
                                        @project))
        cleared (answer {:chat documented-chat}
                        #(ai-chat/update "5632499082330112" {:title "" :tags [] :context {}} @project))]
    (is (= {:title nil :agentId nil :tags ["a"] :context {:order_id "1" :order-id 2}} (sent-body filled)))
    (is (= {:title "" :agentId nil :tags [] :context {}} (sent-body cleared)))))

(deftest get-sends-expand-and-reads-the-chat-key
  (let [result (atom nil)
        sent (answer {:chat documented-chat}
                     #(reset! result (ai-chat/get "5632499082330112" {:expand [:agent-name]} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat/5632499082330112" (sent-path sent)))
    (is (= {"expand" "agentName"} (sent-params sent)))
    (is (= {:order_id "123" :isVip true} (:context @result)))))

(deftest page-sends-tags-comma-separated-and-returns-the-items-and-the-cursor
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :chats [documented-chat]}
                     #(reset! result (ai-chat/page {:cursor "c1"
                                                    :limit 2
                                                    :expand [:agent-name]
                                                    :tags ["support" "vip"]}
                                                   @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat" (sent-path sent)))
    (is (= {"cursor" "c1" "limit" "2" "expand" "agentName" "tags" "support,vip"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["5632499082330112"] (map :id (:content @result))))))

(deftest page-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)]
    (answer {:cursor nil :chats [documented-chat]}
            #(reset! result (ai-chat/page {} @project)))
    (is (nil? (:cursor @result)))))

(deftest query-follows-the-cursor-through-empty-pages-and-honours-the-limit
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :chats (vec (repeat 100 documented-chat))}
                        {:cursor "c2" :chats []}
                        {:cursor "c3" :chats (vec (repeat 50 documented-chat))}]
                       #(reset! result (doall (ai-chat/query {:limit 150 :tags ["support"]} @project))))]
    (is (= 150 (count @result)))
    (is (= [{"limit" "100" "tags" "support"}
            {"limit" "50" "tags" "support" "cursor" "c1"}
            {"limit" "50" "tags" "support" "cursor" "c2"}]
           (map sent-params sent)))))

(deftest delete-sends-ids-in-the-query-string-and-returns-the-deleted-objects
  (let [result (atom nil)
        sent (answer {:chats [documented-chat]}
                     #(reset! result (ai-chat/delete ["5632499082330112"] @project)))]
    (is (= :delete (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-chat?ids=5632499082330112" (:url sent)))
    (is (= "" (:body sent)))
    (is (= ["5632499082330112"] (map :id @result)))))

(deftest create-and-page-round-trip-non-ascii-title-and-tags-as-utf-8
  (let [title "olá 日本 \"q\" \uD83D\uDE00"
        tags ["olá" "日本" "\uD83D\uDE00"]
        created (answer {:chat documented-chat}
                        #(ai-chat/create {:agent-id "5740688905863168" :title title :tags tags} @project))
        listed (answer {:cursor nil :chats []}
                       #(ai-chat/page {:tags tags} @project))]
    (is (= title (:title (sent-body created))))
    (is (= tags (:tags (sent-body created))))
    (is (= "olá,日本,\uD83D\uDE00" (get (sent-params listed) "tags")))))
