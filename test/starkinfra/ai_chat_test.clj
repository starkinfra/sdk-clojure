(ns starkinfra.ai-chat-test
  "Mirrors sdk-python tests/sdk/testAiChat.py. Needs SANDBOX_* credentials; the agent and the chat of the
  namespace are created and deleted by the with-chat fixture."
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [starkinfra.ai-chat :as ai-chat]
            [starkinfra.utils.ai-fixtures :refer [*test-agent* *chat* with-chat]]
            [starkinfra.utils.user :refer [set-project]]))

(use-fixtures :once with-chat)


(deftest ^:sandbox create-returns-the-chat
  (set-project)
  (let [chat *chat*]
    (is (string? (:id chat)))
    (is (= (:id *test-agent*) (:agent-id chat)))
    (is (string? (:title chat)))
    (is (string? (:updated chat)))
    (is (= [] (:tags chat)))
    (is (= {} (:context chat)))))

(deftest ^:sandbox create-update-and-delete-with-tags-and-context
  (set-project)
  (let [chat (ai-chat/create {:agent-id (:id *test-agent*)
                              :title "with-context"
                              :tags ["sdk-clojure" "a"]
                              :context {:order-id "123" :is-vip true}})]
    (try
      (is (= ["sdk-clojure" "a"] (:tags chat)))
      (is (= {:order-id "123" :is-vip true} (:context chat)))
      (let [kept (ai-chat/update (:id chat) {:title "renamed-by-sdk"})]
        (is (= "renamed-by-sdk" (:title kept)))
        (is (= ["sdk-clojure" "a"] (:tags kept)))
        (is (= {:order-id "123" :is-vip true} (:context kept))))
      (let [cleared (ai-chat/update (:id chat) {:tags [] :context {}})]
        (is (= [] (:tags cleared)))
        (is (= {} (:context cleared))))
      (finally
        (ai-chat/delete [(:id chat)])))))

(deftest ^:sandbox get-and-expand-agent-name
  (set-project)
  (let [chat *chat*
        plain (ai-chat/get (:id chat))
        expanded (ai-chat/get (:id chat) {:expand [:agent-name]})]
    (is (= (:id chat) (:id plain)))
    (is (nil? (:agent-name plain)))
    (is (= (:name *test-agent*) (:agent-name expanded)))))

(deftest ^:sandbox query-and-page-list-the-chat-and-filter-by-tags
  (set-project)
  (let [tagged (ai-chat/create {:agent-id (:id *test-agent*) :title "tagged" :tags ["sdk-clojure-a" "sdk-clojure-b"]})]
    (try
      (is (some #{(:id *chat*)} (map :id (ai-chat/query))))
      (is (= [(:id tagged)] (map :id (ai-chat/query {:tags ["sdk-clojure-a" "sdk-clojure-b"]}))))
      (let [page (ai-chat/page {:limit 100 :tags ["sdk-clojure-a"] :expand [:agent-name]})]
        (is (= [(:id tagged)] (map :id (:content page))))
        (is (= (:name *test-agent*) (:agent-name (first (:content page)))))
        (is (contains? page :cursor)))
      (finally
        (ai-chat/delete [(:id tagged)])))))

(deftest ^:sandbox update-changes-the-title-only
  (set-project)
  (let [chat *chat*
        original (ai-chat/get (:id chat))]
    (try
      (let [renamed (ai-chat/update (:id chat) {:title "renamed-by-sdk"})]
        (is (= "renamed-by-sdk" (:title renamed)))
        (is (= (:agent-id chat) (:agent-id renamed))))
      (finally
        (ai-chat/update (:id chat) {:title (:title original)})))))

(deftest ^:sandbox delete-returns-the-deleted-chats
  (set-project)
  (let [chat (ai-chat/create {:agent-id (:id *test-agent*) :title "to-delete"})]
    (is (= [(:id chat)] (map :id (ai-chat/delete [(:id chat)]))))))

(deftest ^:sandbox create-in-an-unknown-agent-raises-input-errors
  (set-project)
  (let [error (try (ai-chat/create {:agent-id "0000000000000000"})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (seq (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [error (try (ai-chat/get "0000000000000000")
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (seq (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [error (try (ai-chat/page {:limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-chat/query {:limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
