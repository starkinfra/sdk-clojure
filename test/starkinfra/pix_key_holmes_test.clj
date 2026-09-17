(ns starkinfra.pix-key-holmes-test
  "Mirrors sdk-python tests/sdk/testPixKeyHolmes.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-key-holmes :as pix-key-holmes]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- random-key-id []
  (rand-nth [(str (java.util.UUID/randomUUID) "@sandbox.com")
             (str "+55" (+ 10000000000 (rand-int 89999999999)))]))

(defn- example-holmes []
  {:key-id (random-key-id)
   :tags ["test"]})


(deftest ^:sandbox create-pix-key-holmes
  (set-project)
  (testing "every created holmes carries an id"
    (let [holmes (pix-key-holmes/create [(example-holmes) (example-holmes)])]
      (doseq [sherlock holmes]
        (is (some? (:id sherlock)))))))

(deftest ^:sandbox query-pix-key-holmes
  (set-project)
  (testing "the stream honours the limit"
    (is (= 10 (count (take 200 (pix-key-holmes/query {:limit 10})))))))

(deftest ^:sandbox page-pix-key-holmes
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-key-holmes/page %) 2 {:limit 2}))))))
