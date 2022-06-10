(ns starkinfra.pix-statement-test
  (:use [clojure.test])
  (:require [starkinfra.pix-statement :as statement]
            [starkinfra.user-test :as user]
            [clojure.java.io :as io]
            [starkinfra.utils.date :as date]
            [starkinfra.utils.page :as page]))
(deftest query-statements
    (testing "query pix statements"
        (user/set-test-project)
        (def statements (take 200 (statement/query {:limit 10})))
        (is (<= 10 (count statements)))))

(deftest page-statements
    (testing "page pix statements"
        (user/set-test-project)
        (def get-page (fn [params] (statement/page params)))
        (def ids (page/get-ids get-page 1 {:limit 4}))
        (is (= 4 (count ids)))))


(deftest query-get-statements
    (testing "query and get pix statements"
        (user/set-test-project)
        (def statements (take 200 (statement/query {:limit 10})))
        (def compare (fn [ch1, ch2] (is (:id ch1) (:id ch2))))
        (doseq [ch1 statements] (compare ch1 (statement/get (:id ch1))))))

(deftest create-statement
    (testing "create pix statement"
        (user/set-test-project)
        (def statement (statement/create {
            :after "2022-01-01",
            :before "2022-01-01",
            :type (rand-nth ["interchange", "interchangeTotal", "transaction"]),
        }))
        (def file-name "temp/pix-statement.pdf")
        (io/make-parents file-name)
        (io/copy (statement/csv (:id statement)) (io/file file-name))
        (is (not (nil? statement)))))