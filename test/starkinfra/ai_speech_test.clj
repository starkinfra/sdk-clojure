(ns starkinfra.ai-speech-test
  "Mirrors sdk-python tests/sdk/testAiSpeech.py. A speech cannot be deleted in the sandbox, so the live
  tests only read and creation is checked at the HTTP boundary."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.ai-speech :as ai-speech]
            [starkinfra.utils.ai-fixtures :refer [answer error-codes project sent-body thrown]]
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


(deftest ^:sandbox query-speeches-leaves-the-audio-out
  (set-project)
  (let [speeches (doall (ai-speech/query))]
    (is (every? #(string? (:id %)) speeches))
    (is (every? #(nil? (:audio %)) speeches))))

(deftest ^:sandbox query-with-fields-and-expand
  (set-project)
  (let [speeches (doall (ai-speech/query {:fields [:id :voice-id :voice-name] :expand [:voice-name]}))]
    (is (every? #(nil? (:status %)) speeches))
    (is (every? #(contains? % :voice-name) speeches))))

(deftest ^:sandbox get-returns-the-audio-of-a-finished-speech
  (set-project)
  (let [finished (first (filter #(= "success" (:status %)) (ai-speech/query)))]
    (when finished
      (let [fetched (ai-speech/get (:id finished))]
        (is (= (:id finished) (:id fetched)))
        (is (string? (:audio fetched)))
        (is (nil? (:audio (ai-speech/get (:id finished) {:fields [:id :status]}))))))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-speech/get "0000000000000000"))]
    (is (= 400 (:status (ex-data exception))))
    (is (seq (error-codes exception)))))

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

(deftest query-reads-the-plural-speeches-key-and-sends-fields-and-expand-only
  (let [result (atom nil)
        sent (answer {:speeches [(dissoc created-speech :audio)]}
                     #(reset! result (doall (ai-speech/query {:fields [:id :voice-id :voice-name]
                                                              :expand [:voice-name]}
                                                             @project))))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-speech?fields=id%2CvoiceId%2CvoiceName&expand=voiceName"
           (:url sent)))
    (is (= ["5646488461901824"] (map :id @result)))
    (is (= "5632499082330112" (:voice-id (first @result))))))

(deftest get-reads-the-speech-key
  (let [result (atom nil)
        sent (answer {:speech created-speech}
                     #(reset! result (ai-speech/get "5646488461901824" {:expand [:voice-name]} @project)))]
    (testing "the id goes in the path and expand in the query"
      (is (= "https://sandbox.api.starkinfra.com/v2/ai-speech/5646488461901824?expand=voiceName" (:url sent)))
      (is (= :get (:method sent))))
    (is (= "SUQzBAAAAAAA" (:audio @result)))))
