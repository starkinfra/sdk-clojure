(ns starkinfra.ai-message-test
  "Mirrors sdk-python tests/sdk/testAiMessage.py. Needs SANDBOX_* credentials; the chat of the namespace is
  created and deleted by the with-chat fixture, and one message is posted to it once."
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is use-fixtures]]
            [starkinfra.ai-message :as ai-message]
            [starkinfra.utils.ai-fixtures :refer [*chat* with-chat]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:dynamic *posted* nil)

(defn- with-posted [tests]
  (binding [*posted* (ai-message/create {:chat-id (:id *chat*) :text "Say hello and mention order 123."}
                                        {:expand [:chat-name]})]
    (tests)))

(use-fixtures :once with-chat with-posted)


(deftest ^:sandbox create-returns-the-user-message-and-the-answer
  (set-project)
  (let [posted *posted*]
    (is (= ["user" "system"] (map :sender posted)))
    (doseq [message posted]
      (is (= (:id *chat*) (:chat-id message)))
      (is (string? (:created message)))
      (is (not (string/blank? (:chat-name message)))))))

(deftest ^:sandbox the-answer-carries-a-metadata-map
  (set-project)
  (is (map? (:metadata (second *posted*)))))

(deftest ^:sandbox query-returns-the-whole-history
  (set-project)
  (let [found (doall (ai-message/query {:chat-id (:id *chat*)}))]
    (is (= (set (map :id *posted*)) (set (map :id found))))))

(deftest ^:sandbox query-without-a-chat-id-reads-the-workspace-history
  (set-project)
  (let [found (doall (ai-message/query {:limit 5}))]
    (is (seq found))
    (is (<= (count found) 5))))

(deftest ^:sandbox query-with-limit-stops-at-the-limit
  (set-project)
  (is (= 1 (count (doall (ai-message/query {:chat-id (:id *chat*) :limit 1}))))))

(deftest ^:sandbox page-returns-a-cursor-that-leads-to-the-next-page
  (set-project)
  (let [first-page (ai-message/page {:chat-id (:id *chat*) :limit 1})
        second-page (ai-message/page {:chat-id (:id *chat*) :cursor (:cursor first-page) :limit 1})]
    (is (= 1 (count (:content first-page))))
    (is (string? (:cursor first-page)))
    (is (= 1 (count (:content second-page))))
    (is (not= (:id (first (:content first-page))) (:id (first (:content second-page)))))))

(deftest ^:sandbox create-in-an-unknown-chat-raises-input-errors
  (set-project)
  (let [error (try (ai-message/create {:chat-id "0000000000000000" :text "hi"})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (seq (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [error (try (ai-message/page {:chat-id (:id *chat*) :limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-message/query {:chat-id (:id *chat*) :limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
