(ns starkinfra.issuing-token-request-test
  "Mirrors sdk-python tests/sdk/testIssuingTokenRequest.py. Needs SANDBOX_*
  credentials and an active IssuingCard in the Workspace (group D's
  starkinfra.issuing-card is not available in this worktree, so the card id
  below is a placeholder for a real active card's id)."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.issuing-token-request :as issuing-token-request]
            [starkinfra.utils.user :refer [set-project]]))


(deftest ^:sandbox create-issuing-token-request
  (set-project)
  (testing "a token request payload is generated for a card"
    (let [request (issuing-token-request/create {:card-id "5734340247945216"
                                                  :wallet-id "google"
                                                  :method-code "app"})]
      (is (> (count (:content request)) 1000)))))
