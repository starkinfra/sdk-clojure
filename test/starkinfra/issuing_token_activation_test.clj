(ns starkinfra.issuing-token-activation-test
  "Mirrors sdk-python tests/sdk/testIssuingTokenActivation.py. Needs
  SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-token-activation :as issuing-token-activation]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:private content
  "{\"activationMethod\": {\"type\": \"text\", \"value\": \"** *****-5678\"}, \"tokenId\": \"5585821789122165\", \"tags\": [\"token\", \"user/1234\"], \"cardId\": \"5189831499972623\"}")

(def ^:private valid-signature
  "MEUCIAxn0FmsPWI4r3Y7Nq8xFNQHYZgo0QAGDQ4/7CajKoVuAiEA09kXWrPMhsw4JbgC3pmNccCWr+hidfop/KsSNqza0yE=")

(def ^:private invalid-signature
  "MEUCIQDOpo1j+V40DNZK2URL2786UQK/8mDXon9ayEd8U0/l7AIgYXtIZJBTs8zCRR3vmted6Ehz/qfw1GRut/eYyvf1yOk=")


(deftest ^:sandbox parse-issuing-token-activations
  (set-project)
  (testing "the valid signature parses"
    (let [activation (issuing-token-activation/parse content valid-signature)]
      (is (= "5585821789122165" (:token-id activation)))
      (is (map? (:activation-method activation)))))

  (testing "a signature from another payload is rejected"
    (is (= "invalidSignature"
           (:code (ex-data (try (issuing-token-activation/parse content invalid-signature)
                                nil
                                (catch clojure.lang.ExceptionInfo e e))))))))
