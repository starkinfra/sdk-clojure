(ns starkinfra.ai-voice-test
  "Mirrors sdk-python tests/sdk/testAiVoice.py. Needs SANDBOX_* credentials. The create test clones a voice
  from the audio of a finished speech of the workspace and is skipped when there is none."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-voice :as ai-voice]
            [starkinfra.utils.ai-fixtures :refer [finished-speech random-name]]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox create-query-page-and-delete
  (set-project)
  (when-let [speech (finished-speech)]
    (let [voice (ai-voice/create {:audio (:audio speech) :name (random-name "sdk-clojure-voice") :language "portuguese"})]
      (try
        (is (string? (:id voice)))
        (is (contains? #{"processing" "success" "failed"} (:status voice)))
        (is (some #{(:id voice)} (map :id (ai-voice/query))))
        (is (some #{(:id voice)} (map :id (:content (ai-voice/page {:limit 100})))))
        (finally
          (is (= [(:id voice)] (map :id (ai-voice/delete [(:id voice)])))))))))

(deftest ^:sandbox query-and-page-voices
  (set-project)
  (let [voices (doall (ai-voice/query {:limit 3}))
        page (ai-voice/page {:limit 100})]
    (is (<= (count voices) 3))
    (is (every? #(contains? #{"processing" "success" "failed"} (:status %)) (:content page)))
    (is (contains? page :cursor))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [error (try (ai-voice/page {:limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-voice/query {:limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
