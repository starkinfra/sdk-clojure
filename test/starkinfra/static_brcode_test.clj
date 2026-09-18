(ns starkinfra.static-brcode-test
  "Mirrors sdk-python tests/sdk/testStaticBrcode.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.static-brcode :as static-brcode]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-brcode []
  {:name "Jamie Lannister"
   :key-id (str "+55" (+ 10 (rand-int 90)) (+ 100000000 (rand-int 899999999)))
   :cashier-bank-code "20018183"
   :amount (rand-int 10)
   :reconciliation-id (str (+ 100 (rand-int 900)))
   :city "Sao Paulo"
   :description "A Static Brcode"})


(deftest ^:sandbox create-and-get-static-brcodes
  (set-project)
  (testing "a created static brcode can be retrieved by its uuid"
    (let [brcodes (static-brcode/create [(example-brcode)])]
      (doseq [brcode brcodes]
        (is (some? (:id brcode)))
        (is (= (:uuid brcode) (:uuid (static-brcode/get (:uuid brcode)))))))))

(deftest ^:sandbox query-static-brcodes
  (set-project)
  (testing "the stream honours the limit"
    (is (<= (count (take 5 (static-brcode/query {:limit 5}))) 5))))

(deftest ^:sandbox page-static-brcodes
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(static-brcode/page %) 2 {:limit 2}))))))
