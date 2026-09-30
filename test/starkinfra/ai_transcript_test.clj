(ns starkinfra.ai-transcript-test
  "Mirrors sdk-python tests/sdk/testAiTranscript.py. A transcript cannot be deleted in the sandbox, so
  the live test only reads and creation is checked at the HTTP boundary."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-transcript :as ai-transcript]
            [starkinfra.utils.ai-fixtures :refer [answer project sent-body]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private created-transcript
  {:id "5147403464212480"
   :text "This is a short recording used to test the transcription service."
   :status "success"
   :errors []
   :created "2026-10-01T14:28:04.482326+00:00"
   :updated "2026-10-01T14:28:05.752389+00:00"})


(deftest ^:sandbox query-transcripts
  (set-project)
  (let [transcripts (doall (ai-transcript/query))]
    (is (every? #(string? (:id %)) transcripts))
    (is (every? #(contains? #{"processing" "success" "failed"} (:status %)) transcripts))))

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

(deftest query-reads-the-transcripts-key-without-parameters
  (let [result (atom nil)
        sent (answer {:transcripts [created-transcript]}
                     #(reset! result (doall (ai-transcript/query @project))))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-transcript" (:url sent)))
    (is (= ["5147403464212480"] (map :id @result)))))
