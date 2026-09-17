(ns starkinfra.event-test
  "Mirrors sdk-python tests/sdk/testEvent.py. Needs SANDBOX_* credentials."
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer [deftest is testing]]
            [starkinfra.event :as event]
            [starkinfra.event.attempt :as attempt]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private content
  "{\"event\": {\"created\": \"2022-02-15T20:45:09.852878+00:00\", \"id\": \"5015597159022592\", \"log\": {\"created\": \"2022-02-15T20:45:09.436621+00:00\", \"errors\": [{\"code\": \"insufficientFunds\", \"message\": \"Amount of funds available is not sufficient to cover the specified transfer\"}], \"id\": \"5288053467774976\", \"request\": {\"amount\": 1000, \"bankCode\": \"34052649\", \"cashAmount\": 0, \"cashierBankCode\": \"\", \"cashierType\": \"\", \"created\": \"2022-02-15T20:45:08.210009+00:00\", \"description\": \"For saving my life\", \"endToEndId\": \"E34052649202201272111u34srod1a91\", \"externalId\": \"141322efdgber1ecd1s342341321\", \"fee\": 0, \"flow\": \"out\", \"id\": \"5137269514043392\", \"initiatorTaxId\": \"\", \"method\": \"manual\", \"receiverAccountNumber\": \"000001\", \"receiverAccountType\": \"checking\", \"receiverBankCode\": \"00000001\", \"receiverBranchCode\": \"0001\", \"receiverKeyId\": \"\", \"receiverName\": \"Jamie Lennister\", \"receiverTaxId\": \"45.987.245/0001-92\", \"reconciliationId\": \"\", \"senderAccountNumber\": \"000000\", \"senderAccountType\": \"checking\", \"senderBankCode\": \"34052649\", \"senderBranchCode\": \"0000\", \"senderName\": \"tyrion Lennister\", \"senderTaxId\": \"012.345.678-90\", \"status\": \"failed\", \"tags\": [], \"updated\": \"2022-02-15T20:45:09.436661+00:00\"}, \"type\": \"failed\"}, \"subscription\": \"pix-request.out\", \"workspaceId\": \"5692908409716736\"}}")

(def ^:private valid-signature
  "MEYCIQD0oFxFQX0fI6B7oqjwLhkRhkDjrOiD86wguEKWdzkJbgIhAPNGUUdlNpYBe+npOaHa9WJopzy3WJYl8XJG6f4ek2R/")

(def ^:private invalid-signature
  "MEYCIQD0oFxFQX0fI6B7oqjwLhkRhkDjrOiD86wjjEKWdzkJbgIhAPNGUUdlNpYBe+npOaHa9WJopzy3WJYl8XJG6f4ek2R/")


(deftest ^:sandbox query-and-get-events
  (set-project)
  (testing "a queried event can be retrieved by its id"
    (let [queried (first (event/query {:limit 5}))]
      (is (some? (:id queried)))
      (is (= (:id queried) (:id (event/get (:id queried))))))))

(deftest ^:sandbox page-events
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(event/page %) 2 {:limit 2}))))))

(deftest ^:sandbox query-and-get-event-attempts
  (set-project)
  (testing "an attempt points back at its event"
    (let [queried (first (attempt/query {:limit 1}))]
      (when queried
        (is (= (:id queried) (:id (attempt/get (:id queried)))))
        (is (some? (:event-id queried))))))

  (testing "two pages of two attempt ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(attempt/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-and-delete-events
  (set-project)
  (testing "an undelivered event can be marked delivered and then deleted"
    (let [undelivered (first (event/query {:limit 10 :is-delivered false}))]
      (is (false? (:is-delivered undelivered)))
      (event/update (:id undelivered) {:is-delivered true})
      (is (true? (:is-delivered (event/get (:id undelivered)))))
      (is (= (:id undelivered) (:id (event/delete (:id undelivered))))))))

(deftest ^:sandbox parse-events
  (set-project)
  (testing "the valid signature parses and the envelope key is unwrapped"
    (let [parsed (event/parse content valid-signature)]
      (is (= "5015597159022592" (:id parsed)))
      (is (= "pix-request.out" (:subscription parsed)))
      (is (= "5137269514043392" (get-in parsed [:log :request :id])))))

  (testing "an indented rendering of the same JSON still verifies, through the
           python-canonical normalization path"
    (let [indented (cheshire/generate-string (cheshire/parse-string content) {:pretty true})]
      (is (= "5015597159022592" (:id (event/parse indented valid-signature))))))

  (testing "a tampered signature is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (event/parse content invalid-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e)))))))

  (testing "a malformed signature is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (event/parse content "something is definitely wrong")
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))
