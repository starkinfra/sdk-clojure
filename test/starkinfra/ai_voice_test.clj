(ns starkinfra.ai-voice-test
  "Mirrors sdk-python tests/sdk/testAiVoice.py. The live tests clone a voice from the audio of a finished
  speech of the workspace and are skipped when there is none; the HTTP boundary tests replace only
  clj-http's request."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-voice :as ai-voice]
            [starkinfra.utils.ai-fixtures :refer [answer error-codes finished-speech-cell project random-name
                                                  replying sent-body sent-params sent-path thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private created-voice
  {:id "5631671361601536"
   :name "Helena"
   :description "Calm voice"
   :language "portuguese"
   :gender "female"
   :status "processing"
   :errors []
   :created "2026-10-01T14:28:24.566332+00:00"
   :updated "2026-10-01T14:28:24.566342+00:00"})


(deftest ^:sandbox create-query-page-and-delete
  (set-project)
  (when-let [speech @finished-speech-cell]
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
  (let [exception (thrown #(ai-voice/page {:limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

(deftest create-sends-only-the-creatable-fields-and-reads-the-voice-key
  (let [result (atom nil)
        sent (answer {:voice created-voice}
                     #(reset! result (ai-voice/create (assoc created-voice :audio "SUQzBAAAAAAA") @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-voice" (:url sent)))
    (is (= {:audio "SUQzBAAAAAAA"
            :name "Helena"
            :description "Calm voice"
            :language "portuguese"
            :gender "female"}
           (sent-body sent)))
    (is (= "5631671361601536" (:id @result)))
    (is (= "processing" (:status @result)))
    (is (= [] (:errors @result)))
    (is (= "2026-10-01T14:28:24.566332+00:00" (:created @result)))))

(deftest create-drops-absent-optional-fields
  (let [sent (answer {:voice created-voice}
                     #(ai-voice/create {:audio "SUQzBAAAAAAA"} @project))]
    (is (= {:audio "SUQzBAAAAAAA"} (sent-body sent)))))

(deftest page-returns-the-items-and-the-cursor-and-sends-its-parameters
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :voices [created-voice]}
                     #(reset! result (ai-voice/page {:cursor "c1" :limit 2} @project)))]
    (is (= :get (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-voice" (sent-path sent)))
    (is (= {"cursor" "c1" "limit" "2"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["5631671361601536"] (map :id (:content @result))))))

(deftest page-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)]
    (answer {:cursor nil :voices [created-voice]}
            #(reset! result (ai-voice/page {} @project)))
    (is (nil? (:cursor @result)))))

(deftest query-reads-the-voices-key-and-follows-the-cursor
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :voices [created-voice]}
                        {:cursor nil :voices [created-voice]}]
                       #(reset! result (doall (ai-voice/query {} @project))))]
    (is (= ["5631671361601536" "5631671361601536"] (map :id @result)))
    (is (= [{} {"cursor" "c1"}] (map sent-params sent)))))

(deftest delete-sends-ids-in-the-query-string-and-no-body
  (let [result (atom nil)
        sent (answer {:voices [created-voice]}
                     #(reset! result (ai-voice/delete ["5631671361601536" "5631671361601537"] @project)))]
    (is (= :delete (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-voice?ids=5631671361601536%2C5631671361601537" (:url sent)))
    (is (= "" (:body sent)))
    (is (= ["5631671361601536"] (map :id @result)))))
