(ns starkinfra.ai-transcript-test
  "Mirrors sdk-python tests/sdk/testAiTranscript.py. Needs SANDBOX_* credentials. The create test transcribes
  the audio of a finished speech of the workspace and is skipped when there is none."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-transcript :as ai-transcript]
            [starkinfra.utils.ai-fixtures :refer [finished-speech]]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox create-then-query-and-page
  (set-project)
  (when-let [speech (finished-speech)]
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
  (let [error (try (ai-transcript/page {:limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-transcript/query {:limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
