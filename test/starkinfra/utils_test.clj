(ns starkinfra.utils-test
  "Offline suite: everything that can be proven without sandbox credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.key :as key]
            [starkinfra.pix-request :as pix-request]
            [starkinfra.settings :as settings]
            [starkinfra.user :as user]
            [starkinfra.utils.case :as casing]
            [starkinfra.utils.end-to-end-id :as end-to-end-id]
            [starkinfra.utils.json :as json]
            [starkinfra.utils.rest]
            [starkinfra.utils.return-id :as return-id])
  (:import (com.starkbank.ellipticcurve PrivateKey)))

(def ^:private bacen-id-pattern #"^\d{8}\d{12}[A-Za-z0-9]{11}$")


(deftest end-to-end-id-shape
  (testing "an end-to-end id is E, the bank code, the UTC minute and 11 random characters"
    (let [id (end-to-end-id/create "20018183")]
      (is (= 32 (count id)))
      (is (re-matches #"^E\d{8}\d{12}[A-Za-z0-9]{11}$" id))
      (is (re-matches bacen-id-pattern (subs id 1))))))

(deftest return-id-shape
  (testing "a return id is the same thing behind a D"
    (let [id (return-id/create "20018183")]
      (is (= 32 (count id)))
      (is (re-matches #"^D\d{8}\d{12}[A-Za-z0-9]{11}$" id)))))

(deftest bacen-ids-are-random
  (testing "two ids created in the same minute differ"
    (is (not= (end-to-end-id/create "20018183") (end-to-end-id/create "20018183")))))

(deftest canonical-json-matches-python-dumps
  (testing "separators, key order and escaping"
    (is (= "{\"a\": 1, \"b\": 2}" (json/dumps (array-map :a 1 :b 2))))
    (is (= "{\"a\": 1, \"b\": 2}" (json/dumps (array-map :b 2 :a 1) true)))
    (is (= "{\"C\": 3, \"a\": 2, \"b\": 1}" (json/dumps (array-map "b" 1 "a" 2 "C" 3) true)))
    (is (= "[1, 2.5, null, true, false]" (json/dumps [1 2.5 nil true false])))
    (is (= "{}" (json/dumps {})))
    (is (= "[]" (json/dumps []))))

  (testing "ensure_ascii escaping, including surrogate pairs above U+FFFF"
    (is (= "\"\\u00e9\"" (json/dumps "\u00e9")))
    (is (= "\"\\u65e5\\u672c\"" (json/dumps "\u65e5\u672c")))
    (is (= "\"\\ud83d\\ude00\"" (json/dumps "\uD83D\uDE00")))
    (is (= "\"\\u007f\"" (json/dumps "\u007f")))
    (is (= "\"\\u0001\"" (json/dumps "\u0001")))
    (is (= "\"a\\\"b\\\\c\\n\\t/\"" (json/dumps "a\"b\\c\n\t/"))))

  (testing "integers verbatim"
    (is (= "0" (json/dumps 0)))
    (is (= "-42" (json/dumps -42)))
    (is (= "123456789012345678901234567890" (json/dumps 123456789012345678901234567890N))))

  (testing "floats as python's repr: shortest round-trip, fixed or exponential"
    (is (= "1.0" (json/dumps 1.0)))
    (is (= "100.0" (json/dumps 100.0)))
    (is (= "2.5" (json/dumps 2.5)))
    (is (= "123.456" (json/dumps 123.456)))
    (is (= "0.001" (json/dumps 0.001)))
    (is (= "0.0001" (json/dumps 0.0001)))
    (is (= "1e-05" (json/dumps 1e-5)))
    (is (= "-2.5e-07" (json/dumps -2.5e-7)))
    (is (= "1000000000000000.0" (json/dumps 1e15)))
    (is (= "1e+16" (json/dumps 1e16)))
    (is (= "1.5e+16" (json/dumps 1.5e16)))
    (is (= "1e+21" (json/dumps 1e21)))
    (is (= "1e+100" (json/dumps 1e100)))
    (is (= "1e-100" (json/dumps 1e-100)))
    (is (= "-0.0" (json/dumps -0.0)))
    (is (= "0.0" (json/dumps 0.0)))
    (is (= "1.7976931348623157e+308" (json/dumps Double/MAX_VALUE)))
    (is (= "0.3333333333333333" (json/dumps 0.3333333333333333)))))

(deftest api-json-casting
  (testing "nil members dropped, keys camelCased, nils inside lists kept"
    (is (= {"endToEndId" "E1" "senderName" "Edward"}
           (json/api-json {:end-to-end-id "E1" :sender-name "Edward" :description nil})))
    (is (= {"authorization" {"status" "denied"}}
           (json/api-json {:authorization {:status "denied" :reason nil}})))
    (is (= {"tags" ["a" nil]} (json/api-json {:tags ["a" nil]})))
    (is (= {} (json/api-json {:reason nil})))))

(deftest pix-request-response-output
  (testing "an approved response carries no null members"
    (is (= "{\"authorization\": {\"status\": \"approved\"}}"
           (pix-request/response "approved"))))

  (testing "a denied response carries the reason"
    (is (= "{\"authorization\": {\"status\": \"denied\", \"reason\": \"taxIdMismatch\"}}"
           (pix-request/response "denied" "taxIdMismatch"))))

  (testing "the two-arity call with a nil reason equals the one-arity call"
    (is (= (pix-request/response "approved") (pix-request/response "approved" nil)))))

(deftest malformed-signature-is-rejected-without-a-request
  (testing "the signature is decoded before the public key is fetched, so a
           malformed one fails offline, exactly as sdk-python does"
    (let [thrown (try
                   (pix-request/parse "{}" "something is definitely wrong" nil)
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
      (is (some? thrown))
      (is (= "invalidSignature" (:code (ex-data thrown)))))))

(deftest key-creation-round-trip
  (testing "the generated private key parses back as secp256k1"
    (let [pair (key/create)
          parsed (PrivateKey/fromPem (:private-pem pair))]
      (is (string? (:private-pem pair)))
      (is (string? (:public-pem pair)))
      (is (= "secp256k1" (.name (.curve parsed))))
      (is (= (:public-pem pair) (.toPem (.publicKey parsed)))
          "the returned public pem must belong to the returned private key"))))

(deftest user-construction
  (testing "a project built from a generated key carries the project access id"
    (let [pair (key/create)
          project (user/project "sandbox" "5656565656565656" (:private-pem pair))]
      (is (= "sandbox" (:environment project)))
      (is (= "project" (:type project)))
      (is (= "project/5656565656565656" (:access-id project)))))

  (testing "an organization access id names the workspace when one is given"
    (let [pair (key/create)
          organization (user/organization "sandbox" "5656565656565656" (:private-pem pair))
          scoped (user/organization-replace organization "4848484848484848")]
      (is (= "organization" (:type organization)))
      (is (= "organization/5656565656565656" (:access-id organization)))
      (is (nil? (:workspace-id organization)))
      (is (= "4848484848484848" (:workspace-id scoped)))
      (is (= "organization/5656565656565656/workspace/4848484848484848" (:access-id scoped)))
      (is (= "organization/5656565656565656/workspace/4848484848484848"
             (:access-id (user/organization "sandbox" "5656565656565656"
                                            (:private-pem pair) "4848484848484848"))))))

  (testing "both user shapes keep the pem, which is what signs a request"
    (let [pair (key/create)]
      (is (= (:private-pem pair) (:private-key (user/project "sandbox" "1" (:private-pem pair)))))
      (is (= (:private-pem pair) (:private-key (user/organization "sandbox" "1" (:private-pem pair)))))))

  (testing "a malformed private key is refused at the boundary"
    (is (thrown? IllegalArgumentException (user/project "sandbox" "1" "not a pem")))
    (is (thrown? IllegalArgumentException
                 (user/project "staging" "1" (:private-pem (key/create)))))))

(deftest settings-defaults-and-setters
  (testing "the shipped defaults"
    (is (= "infra" settings/host))
    (is (= "v2" settings/api-version))
    (is (= 15 settings/timeout)))

  (testing "the version string lives in settings.clj and project.clj agrees with it"
    (is (= settings/sdk-version (nth (read-string (slurp "project.clj")) 2))))

  (testing "language and user are settable and readable"
    (let [language @settings/error-lang
          credentials @settings/credentials]
      (try
        (is (= "en-US" language))
        (settings/language "pt-BR")
        (is (= "pt-BR" @settings/error-lang))
        (settings/user {:type "project" :id "1"})
        (is (= {:type "project" :id "1"} @settings/credentials))
        (finally
          (settings/language language)
          (settings/user credentials))))))

(deftest write-payload-casting
  (testing "nil members are dropped from write payloads, as python's api_json does"
    (is (= {"reason" "fraud"} (json/api-json {:reason "fraud" :name nil})))
    (is (= [{"amount" 1} {"amount" 2}] (json/api-json [{:amount 1 :tags nil} {:amount 2}])))
    (is (= {"isSent" false} (json/api-json {:is-sent false}))
        "false is a value, not an absence")
    (is (= {"rule" {"key" "k"}} (json/api-json {:rule {:key "k" :value nil}}))))

  (testing "raw bodies keep their nil members and are still camelCased"
    (is (= {"reason" "fraud" "name" nil}
           (casing/cast-keys-to-camel {:reason "fraud" :name nil})))
    (is (= {"isSent" false} (casing/cast-keys-to-camel {:is-sent false})))
    (is (nil? (casing/cast-keys-to-camel nil)))))
