(ns starkinfra.credit-signer-test
  "Mirrors sdk-python tests/sdk/testCreditSigner.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.credit-note :as credit-note]
            [starkinfra.credit-signer :as credit-signer]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.user :refer [set-project template-id]]))

(defn- example-credit-note []
  {:template-id (template-id)
   :name "Jamie Lannister"
   :tax-id "012.345.678-90"
   :nominal-amount (+ 100000 (rand-int 900000))
   :scheduled (date/future-date (+ 10 (rand-int 10)))
   :invoices [{:amount 200000 :due (date/future-date 500)}]
   :payment {:bank-code "60701190"
             :branch-code (str (+ 1000 (rand-int 8999)))
             :account-number (str (+ 1000000 (rand-int 8999999)))
             :name "Jamie Lannister"
             :tax-id "012.345.678-90"}
   :payment-type "transfer"
   :signers [{:name "Jamie Lannister"
              :contact (str "jamie.lannister." (rand-int 1000000000) "@invaliddomain.com")
              :method "link"}]
   :external-id (str "clojure-sdk-" (rand-int 1000000000))
   :street-line-1 "Rua ABC"
   :street-line-2 "Ap 123"
   :district "Jardim Paulista"
   :city "Sao Paulo"
   :state-code "SP"
   :zip-code "01234-567"})


(deftest ^:sandbox resend-token-to-signer
  (set-project)
  (testing "the token is resent to a freshly created note's signer"
    (let [note (first (credit-note/create [(example-credit-note)]))
          signer (first (:signers note))]
      (is (some? (:id (credit-signer/resend-token (:id signer)))))))

  (testing "a signer that does not exist is refused by the API"
    (is (some? (try (credit-signer/resend-token "0")
                    nil
                    (catch clojure.lang.ExceptionInfo e e))))))
