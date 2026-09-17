(ns starkinfra.credit-preview-test
  "Mirrors sdk-python tests/sdk/testCreditPreview.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.credit-preview :as credit-preview]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.user :refer [set-project]]))

(defn- sac-preview []
  {:type "credit-note"
   :credit {:type "sac"
            :tax-id "012.345.678-90"
            :nominal-amount (+ 1000 (rand-int 100000))
            :rebate-amount (rand-int 1000)
            :nominal-interest 4.5
            :scheduled (date/future-date 15)
            :initial-due (date/future-date 35)
            :initial-amount (+ 1 (rand-int 9999))
            :interval "month"}})

(defn- bullet-preview []
  {:type "credit-note"
   :credit {:type "bullet"
            :tax-id "012.345.678-90"
            :nominal-amount (+ 1000 (rand-int 100000))
            :nominal-interest 3.2
            :scheduled (date/future-date 15)
            :initial-due (date/future-date 35)}})

(defn- custom-preview []
  (let [amount (+ 1000 (rand-int 100000))]
    {:type "credit-note"
     :credit {:type "custom"
              :tax-id "012.345.678-90"
              :nominal-amount amount
              :scheduled (date/future-date 30)
              :invoices [{:amount (quot amount 2) :due (date/future-date 60)}
                        {:amount (quot amount 2) :due (date/future-date 90)}]}}))


(deftest ^:sandbox create-credit-previews
  (set-project)
  (testing "each preview echoes back its own type"
    (let [previews (credit-preview/create [(sac-preview) (bullet-preview) (custom-preview)])]
      (is (= 3 (count previews)))
      (is (every? #(= "credit-note" (:type %)) previews))
      (is (= ["sac" "bullet" "custom"] (map #(get-in % [:credit :type]) previews))))))
