(ns starkinfra.ai-voice-test
  "Mirrors sdk-python tests/sdk/testAiVoice.py. A voice cannot be deleted in the sandbox (DELETE answers
  500), so creation and deletion are checked at the HTTP boundary and the live test only reads."
  (:require [clojure.test :refer [deftest is]]
            [starkinfra.ai-voice :as ai-voice]
            [starkinfra.utils.ai-fixtures :refer [answer project sent-body]]
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


(deftest ^:sandbox query-voices
  (set-project)
  (let [voices (doall (ai-voice/query))]
    (is (every? #(string? (:id %)) voices))
    (is (every? #(contains? #{"processing" "success" "failed"} (:status %)) voices))))

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

(deftest query-reads-the-voices-key-without-parameters
  (let [result (atom nil)
        sent (answer {:voices [created-voice]}
                     #(reset! result (doall (ai-voice/query @project))))]
    (is (= :get (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-voice" (:url sent)))
    (is (= ["5631671361601536"] (map :id @result)))))

(deftest delete-sends-ids-in-the-query-string-and-no-body
  (let [result (atom nil)
        sent (answer {:voices [created-voice]}
                     #(reset! result (ai-voice/delete ["5631671361601536" "5631671361601537"] @project)))]
    (is (= :delete (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-voice?ids=5631671361601536%2C5631671361601537" (:url sent)))
    (is (= "" (:body sent)))
    (is (= ["5631671361601536"] (map :id @result)))))
