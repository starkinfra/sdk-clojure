(ns starkinfra.pix-key-holmes-test
  "Mirrors sdk-python tests/sdk/testPixKeyHolmes.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-key-holmes :as pix-key-holmes]
            [starkinfra.pix-key-holmes.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- random-key-id []
  (rand-nth [(str (java.util.UUID/randomUUID) "@sandbox.com")
             (str "+55" (+ 10000000000 (long (rand 89999999999))))]))

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

(deftest ^:sandbox get-pix-key-holmes
  (set-project)
  (testing "a queried holmes can be retrieved by its id"
    (let [queried (first (pix-key-holmes/query {:limit 1}))
          fetched (pix-key-holmes/get (:id queried))]
      (is (= (:id queried) (:id fetched))))))

(deftest ^:sandbox query-and-get-pix-key-holmes-logs
  (set-project)
  (testing "every log carries an id, a type and its holmes"
    (let [logs (doall (log/query {:limit 10}))]
      (is (pos? (count logs)))
      (doseq [entry logs]
        (is (some? (:id entry)))
        (is (some? (:type entry)))
        (is (some? (:id (:holmes entry)))))))

  (testing "a log can be retrieved by its id and carries its holmes"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (some? (:id (:holmes fetched))))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
