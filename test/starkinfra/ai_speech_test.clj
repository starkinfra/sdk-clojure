(ns starkinfra.ai-speech-test
  "Mirrors sdk-python tests/sdk/testAiSpeech.py. Needs SANDBOX_* credentials. The create test uses a finished
  voice of the workspace and is skipped when there is none."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-speech :as ai-speech]
            [starkinfra.utils.ai-fixtures :refer [finished-speech finished-voice]]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox create-then-get-query-and-page
  (set-project)
  (when-let [voice (finished-voice)]
    (let [speech (ai-speech/create {:voice-id (:id voice) :text "Short test."})]
      (is (string? (:id speech)))
      (is (= (:id voice) (:voice-id speech)))
      (is (= (:id speech) (:id (ai-speech/get (:id speech)))))
      (is (some #{(:id speech)} (map :id (:content (ai-speech/page {:limit 100})))))
      (is (some #{(:id speech)} (map :id (ai-speech/query {:limit 100})))))))

(deftest ^:sandbox query-and-page-speeches-leave-the-audio-out-and-expand-the-voice-name
  (set-project)
  (let [speeches (doall (ai-speech/query {:limit 3 :expand [:voice-name]}))
        page (ai-speech/page {:limit 100})]
    (is (<= (count speeches) 3))
    (is (seq (:content page)))
    (is (every? #(nil? (:audio %)) (:content page)))
    (is (contains? page :cursor))))

(deftest ^:sandbox get-returns-the-audio-of-a-finished-speech
  (set-project)
  (when-let [finished (finished-speech)]
    (is (string? (:audio finished)))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [error (try (ai-speech/get "0000000000000000")
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (seq (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [error (try (ai-speech/page {:limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-speech/query {:limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
