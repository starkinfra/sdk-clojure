(ns starkinfra.ai-speech-test
  "Mirrors sdk-python tests/sdk/testAiSpeech.py. The live tests use a finished voice of the workspace and
  are skipped when there is none; the HTTP boundary tests replace only clj-http's request."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.ai-speech :as ai-speech]
            [starkinfra.utils.ai-fixtures :refer [answer error-codes finished-speech-cell finished-voice-cell
                                                  project replying sent-body sent-params sent-path thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private created-speech
  {:id "5646488461901824"
   :voiceId "5632499082330112"
   :text "Short test."
   :status "success"
   :audio "SUQzBAAAAAAA"
   :errors []
   :created "2026-10-01T14:28:06.942491+00:00"
   :updated "2026-10-01T14:28:07.605185+00:00"})


(deftest ^:sandbox create-then-get-query-and-page
  (set-project)
  (when-let [voice @finished-voice-cell]
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
    (is (every? #(nil? (:audio %)) (:content page)))
    (is (contains? page :cursor))))

(deftest ^:sandbox get-returns-the-audio-of-a-finished-speech
  (set-project)
  (when-let [finished @finished-speech-cell]
    (is (string? (:audio (ai-speech/get (:id finished)))))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-speech/get "0000000000000000"))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [exception (thrown #(ai-speech/page {:limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

(deftest create-sends-voice-id-and-text-only-and-reads-the-speech-key
  (let [returned (-> created-speech
                     (assoc :voice-id "5632499082330112" :voice-name "Helena")
                     (dissoc :voiceId))
        result (atom nil)
        sent (answer {:speech created-speech}
                     #(reset! result (ai-speech/create returned @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-speech" (:url sent)))
    (is (= {:voiceId "5632499082330112" :text "Short test."} (sent-body sent)))
    (is (= "5646488461901824" (:id @result)))
    (is (= "5632499082330112" (:voice-id @result)))
    (is (= "success" (:status @result)))
    (is (= "SUQzBAAAAAAA" (:audio @result)))))

(deftest get-sends-expand-and-reads-the-speech-key
  (let [result (atom nil)
        sent (answer {:speech created-speech}
                     #(reset! result (ai-speech/get "5646488461901824" {:expand [:voice-name]} @project)))]
    (testing "the id goes in the path and expand in the query"
      (is (= "https://sandbox.api.starkinfra.com/v2/ai-speech/5646488461901824" (sent-path sent)))
      (is (= {"expand" "voiceName"} (sent-params sent)))
      (is (= :get (:method sent))))
    (is (= "SUQzBAAAAAAA" (:audio @result)))))

(deftest page-reads-the-plural-speeches-key-and-sends-its-parameters
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :speeches [(dissoc created-speech :audio)]}
                     #(reset! result (ai-speech/page {:cursor "c1" :limit 2 :expand [:voice-name]} @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-speech" (sent-path sent)))
    (is (= {"cursor" "c1" "limit" "2" "expand" "voiceName"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["5646488461901824"] (map :id (:content @result))))
    (is (= "5632499082330112" (:voice-id (first (:content @result)))))))

(deftest page-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)]
    (answer {:cursor nil :speeches [created-speech]}
            #(reset! result (ai-speech/page {} @project)))
    (is (nil? (:cursor @result)))))

(deftest query-follows-the-cursor-through-empty-pages-and-honours-the-limit
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :speeches (vec (repeat 100 created-speech))}
                        {:cursor "c2" :speeches []}
                        {:cursor "c3" :speeches (vec (repeat 50 created-speech))}]
                       #(reset! result (doall (ai-speech/query {:limit 150 :expand [:voice-name]} @project))))]
    (is (= 150 (count @result)))
    (is (= [{"limit" "100" "expand" "voiceName"}
            {"limit" "50" "expand" "voiceName" "cursor" "c1"}
            {"limit" "50" "expand" "voiceName" "cursor" "c2"}]
           (map sent-params sent)))))
