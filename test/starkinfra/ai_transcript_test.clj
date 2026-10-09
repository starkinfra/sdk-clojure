(ns starkinfra.ai-transcript-test
  "Mirrors sdk-python tests/sdk/testAiTranscript.py. The live tests transcribe the audio of a finished
  speech of the workspace and are skipped when there is none; the HTTP boundary tests replace only
  clj-http's request."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-transcript :as ai-transcript]
            [starkinfra.utils.ai-fixtures :refer [answer error-codes finished-speech-cell project replying
                                                  sent-body sent-params sent-path thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private created-transcript
  {:id "5147403464212480"
   :text "This is a short recording used to test the transcription service."
   :status "success"
   :errors []
   :created "2026-10-01T14:28:04.482326+00:00"
   :updated "2026-10-01T14:28:05.752389+00:00"})


(deftest ^:sandbox create-then-query-and-page
  (set-project)
  (when-let [speech @finished-speech-cell]
    (let [transcript (ai-transcript/create {:audio (:audio speech)})]
      (is (string? (:id transcript)))
      (is (contains? #{"processing" "success" "failed"} (:status transcript)))
      (is (some #{(:id transcript)} (map :id (:content (ai-transcript/page {:limit 100}))))))))

(deftest ^:sandbox query-and-page-transcripts
  (set-project)
  (let [transcripts (doall (ai-transcript/query {:limit 3}))
        page (ai-transcript/page {:limit 100})]
    (is (<= (count transcripts) 3))
    (is (every? #(contains? #{"processing" "success" "failed"} (:status %)) (:content page)))
    (is (contains? page :cursor))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [exception (thrown #(ai-transcript/page {:limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

(deftest create-sends-only-the-audio-and-reads-the-transcript-key
  (let [result (atom nil)
        sent (answer {:transcript created-transcript}
                     #(reset! result (ai-transcript/create (assoc created-transcript :audio "SUQzBAAAAAAA") @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-transcript" (:url sent)))
    (is (= {:audio "SUQzBAAAAAAA"} (sent-body sent)))
    (is (= "5147403464212480" (:id @result)))
    (is (= "success" (:status @result)))
    (is (= "This is a short recording used to test the transcription service." (:text @result)))))

(deftest page-returns-the-items-and-the-cursor-and-sends-its-parameters
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :transcripts [created-transcript]}
                     #(reset! result (ai-transcript/page {:cursor "c1" :limit 2} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-transcript" (sent-path sent)))
    (is (= {"cursor" "c1" "limit" "2"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["5147403464212480"] (map :id (:content @result))))))

(deftest page-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)]
    (answer {:cursor nil :transcripts [created-transcript]}
            #(reset! result (ai-transcript/page {} @project)))
    (is (nil? (:cursor @result)))))

(deftest query-reads-the-transcripts-key-and-follows-the-cursor
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :transcripts [created-transcript]}
                        {:cursor nil :transcripts [created-transcript]}]
                       #(reset! result (doall (ai-transcript/query {} @project))))]
    (is (= 2 (count @result)))
    (is (= [{} {"cursor" "c1"}] (map sent-params sent)))))
