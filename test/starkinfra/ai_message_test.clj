(ns starkinfra.ai-message-test
  "Mirrors sdk-python tests/sdk/testAiMessage.py. The live tests share the chat and the one message
  posted by starkinfra.utils.ai-fixtures; the HTTP boundary tests replace only clj-http's request."
  (:require [clojure.string :as string]
            [clojure.test :refer [deftest is]]
            [starkinfra.ai-message :as ai-message]
            [starkinfra.utils.ai-fixtures :refer [answer chat-cell error-codes posted-cell project replying
                                                  sent-body sent-params sent-path thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private documented-messages
  [{:id "5642368648740864"
    :chatId "5632499082330112"
    :sender "user"
    :text "Say hello."
    :speech "Say hello."
    :metadata {}
    :model "bender-1.0"
    :created "2026-10-01T14:28:02.652375+00:00"}
   {:id "5079418695319552"
    :chatId "5632499082330112"
    :sender "system"
    :text "Hello!"
    :speech "Hello!"
    :metadata {:order_id "123" :isUrgent true}
    :model "bender-1.0"
    :created "2026-10-01T14:28:02.653375+00:00"}])


(deftest ^:sandbox create-returns-the-user-message-and-the-answer
  (set-project)
  (let [posted @posted-cell]
    (is (= ["user" "system"] (map :sender posted)))
    (doseq [message posted]
      (is (= (:id @chat-cell) (:chat-id message)))
      (is (string? (:created message)))
      (is (not (string/blank? (:chat-name message)))))))

(deftest ^:sandbox the-answer-carries-a-metadata-map
  (set-project)
  (is (map? (:metadata (second @posted-cell)))))

(deftest ^:sandbox query-returns-the-whole-history
  (set-project)
  (let [found (doall (ai-message/query {:chat-id (:id @chat-cell)}))]
    (is (= (set (map :id @posted-cell)) (set (map :id found))))))

(deftest ^:sandbox query-without-a-chat-id-reads-the-workspace-history
  (set-project)
  @posted-cell
  (let [found (doall (ai-message/query {:limit 5}))]
    (is (seq found))
    (is (<= (count found) 5))))

(deftest ^:sandbox query-with-limit-stops-at-the-limit
  (set-project)
  @posted-cell
  (is (= 1 (count (doall (ai-message/query {:chat-id (:id @chat-cell) :limit 1}))))))

(deftest ^:sandbox page-returns-a-cursor-that-leads-to-the-next-page
  (set-project)
  @posted-cell
  (let [first-page (ai-message/page {:chat-id (:id @chat-cell) :limit 1})
        second-page (ai-message/page {:chat-id (:id @chat-cell) :cursor (:cursor first-page) :limit 1})]
    (is (= 1 (count (:content first-page))))
    (is (string? (:cursor first-page)))
    (is (= 1 (count (:content second-page))))
    (is (not= (:id (first (:content first-page))) (:id (first (:content second-page)))))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [exception (thrown #(ai-message/page {:chat-id (:id @chat-cell) :limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

(deftest ^:sandbox create-in-an-unknown-chat-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-message/create {:chat-id "0000000000000000" :text "hi"}))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

(deftest create-sends-expand-in-the-query-string-and-not-in-the-body
  (let [result (atom nil)
        sent (answer {:chatName "Greeting" :messages documented-messages}
                     #(reset! result (ai-message/create {:chat-id "5632499082330112"
                                                         :text "Say hello."
                                                         :model "prime-1.0"
                                                         :sender "user"
                                                         :id "x"}
                                                        {:expand [:chat-name]}
                                                        @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-message?expand=chatName" (:url sent)))
    (is (= {:chatId "5632499082330112" :text "Say hello." :model "prime-1.0"} (sent-body sent)))
    (is (= ["user" "system"] (map :sender @result)))
    (is (= #{"Greeting"} (set (map :chat-name @result))))
    (is (= "5632499082330112" (:chat-id (first @result))))))

(deftest create-without-expand-leaves-the-chat-name-out
  (let [result (atom nil)
        sent (answer {:messages documented-messages}
                     #(reset! result (ai-message/create {:chat-id "5632499082330112" :text "Say hello."} {} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-message" (:url sent)))
    (is (every? #(nil? (:chat-name %)) @result))))

(deftest metadata-keys-are-left-as-the-agent-wrote-them
  (let [result (atom nil)]
    (answer {:messages documented-messages}
            #(reset! result (ai-message/create {:chat-id "5632499082330112" :text "Say hello."} {} @project)))
    (is (= {:order_id "123" :isUrgent true} (:metadata (second @result))))))

(deftest query-follows-the-cursor-until-it-runs-out
  (let [result (atom nil)
        [first-request second-request] (replying [{:cursor "next-page" :messages [(first documented-messages)]}
                                                  {:cursor nil :messages [(second documented-messages)]}]
                                                 #(reset! result (doall (ai-message/query {:chat-id "5632499082330112"} @project))))]
    (is (= ["5642368648740864" "5079418695319552"] (map :id @result)))
    (is (= {"chatId" "5632499082330112"} (sent-params first-request)))
    (is (= {"chatId" "5632499082330112" "cursor" "next-page"} (sent-params second-request)))))

(deftest query-without-a-chat-id-leaves-it-out-of-the-url
  (let [sent (replying [{:cursor nil :messages documented-messages}]
                       #(doall (ai-message/query {} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-message" (:url (first sent))))))

(deftest query-stops-at-the-limit-without-asking-for-more
  (let [result (atom nil)
        sent (replying [{:cursor "next-page" :messages [(first documented-messages)]}]
                       #(reset! result (doall (ai-message/query {:chat-id "5632499082330112" :limit 1} @project))))]
    (is (= 1 (count sent)))
    (is (= 1 (count @result)))
    (is (= "1" (get (sent-params (first sent)) "limit")))))

(deftest query-follows-the-cursor-through-empty-pages-and-honours-the-limit
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :messages (vec (repeat 100 (first documented-messages)))}
                        {:cursor "c2" :messages []}
                        {:cursor "c3" :messages (vec (repeat 50 (first documented-messages)))}]
                       #(reset! result (doall (ai-message/query {:chat-id "5632499082330112" :limit 150} @project))))]
    (is (= 150 (count @result)))
    (is (= [{"chatId" "5632499082330112" "limit" "100"}
            {"chatId" "5632499082330112" "limit" "50" "cursor" "c1"}
            {"chatId" "5632499082330112" "limit" "50" "cursor" "c2"}]
           (map sent-params sent)))))

(deftest page-returns-the-items-and-the-cursor
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :messages documented-messages}
                     #(reset! result (ai-message/page {:chat-id "5632499082330112" :cursor "c1" :limit 2} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-message" (sent-path sent)))
    (is (= {"chatId" "5632499082330112" "limit" "2" "cursor" "c1"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["5642368648740864" "5079418695319552"] (map :id (:content @result))))
    (is (= {:order_id "123" :isUrgent true} (:metadata (second (:content @result)))))))

(deftest page-without-a-chat-id-leaves-it-out-of-the-url-and-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)
        sent (answer {:cursor nil :messages documented-messages}
                     #(reset! result (ai-message/page {} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-message" (:url sent)))
    (is (nil? (:cursor @result)))))

(deftest create-round-trips-non-ascii-text-as-utf-8
  (let [text "olá 日本 \"q\" \uD83D\uDE00"
        sent (answer {:messages documented-messages}
                     #(ai-message/create {:chat-id "5632499082330112" :text text} {} @project))]
    (is (= text (:text (sent-body sent))))))
