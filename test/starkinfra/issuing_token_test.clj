(ns starkinfra.issuing-token-test
  "Mirrors sdk-python tests/sdk/testIssuingToken.py. Needs SANDBOX_*
  credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-token :as issuing-token]
            [starkinfra.issuing-token.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private content
  "{\"deviceName\": \"My phone\", \"methodCode\": \"manual\", \"walletName\": \"Google Pay\", \"activationCode\": \"\", \"deviceSerialNumber\": \"2F6D63\", \"deviceImei\": \"352099001761481\", \"deviceType\": \"Phone\", \"walletInstanceId\": \"1b24f24a24ba98e27d43e345b532a245e4723d7a9c4f624e\", \"deviceOsVersion\": \"4.4.4\", \"cardId\": \"5189831499972623\", \"deviceOsName\": \"Android\", \"merchantId\": \"12345678901\", \"walletId\": \"google\"}")

(def ^:private valid-signature
  "MEYCIQC4XbhjxEp9VhowLeg9JbSOo94FCRWE9GI7l7OuHh0bUwIhAJBuLDl5DAT9L4iMI0qYQ+PVmBIG5scxxvkWSsoWmwi4")

(def ^:private invalid-signature
  "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")

(def ^:private malformed-signature
  "something is definitely wrong")


(deftest ^:sandbox query-and-get-issuing-tokens
  (set-project)
  (testing "every queried token can be retrieved by its id"
    (let [tokens (take 5 (issuing-token/query {:limit 5}))]
      (doseq [token tokens]
        (is (= (:id token) (:id (issuing-token/get (:id token)))))))))

(deftest ^:sandbox page-issuing-tokens
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(issuing-token/page %) 2 {:limit 2}))))))

(deftest ^:sandbox update-and-cancel-issuing-token
  (set-project)
  (testing "a blocked token can then be canceled"
    (let [token (first (issuing-token/query {:limit 1}))
          updated (issuing-token/update (:id token) {:status "blocked"})
          canceled (issuing-token/cancel (:id updated))]
      (is (= (:id updated) (:id canceled))))))

(deftest ^:sandbox parse-issuing-tokens
  (set-project)
  (testing "the valid signature parses"
    (let [token (issuing-token/parse content valid-signature)]
      (is (= "5189831499972623" (:card-id token)))))

  (testing "a signature from another payload is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (issuing-token/parse content invalid-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e)))))))

  (testing "a malformed signature is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (issuing-token/parse content malformed-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))

(deftest response-authorization-approved
  (testing "reason defaults to an empty string when absent"
    (is (= (str "{\"authorization\": {\"status\": \"approved\", \"reason\": \"\", "
               "\"activationMethods\": [{\"type\": \"app\", \"value\": \"com.subissuer.android\"}, "
               "{\"type\": \"text\", \"value\": \"** *****-5678\"}], "
               "\"designId\": \"4584031664472031\", \"tags\": [\"tony\", \"stark\"]}}")
           (issuing-token/response-authorization
            "approved"
            {:activation-methods [{:type "app" :value "com.subissuer.android"}
                                  {:type "text" :value "** *****-5678"}]
             :design-id "4584031664472031"
             :tags ["tony" "stark"]})))))

(deftest response-authorization-denied
  (testing "a denial only carries status and reason"
    (is (= "{\"authorization\": {\"status\": \"denied\", \"reason\": \"other\"}}"
           (issuing-token/response-authorization "denied" {:reason "other"})))))

(deftest response-activation-approved
  (testing "reason defaults to an empty string when absent"
    (is (= "{\"authorization\": {\"status\": \"approved\", \"reason\": \"\", \"tags\": [\"tony\", \"stark\"]}}"
           (issuing-token/response-activation "approved" {:tags ["tony" "stark"]})))))

(deftest response-activation-denied
  (testing "an explicit reason is sent as-is"
    (is (= "{\"authorization\": {\"status\": \"denied\", \"reason\": \"other\", \"tags\": [\"tony\", \"stark\"]}}"
           (issuing-token/response-activation "denied" {:reason "other" :tags ["tony" "stark"]})))))

(deftest ^:sandbox query-and-get-issuing-token-logs
  (set-project)
  (testing "a log can be retrieved by its id"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched))))))
