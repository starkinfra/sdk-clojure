(ns starkinfra.pix-infraction-test
  "Mirrors sdk-python tests/sdk/testPixInfraction.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-infraction :as pix-infraction]
            [starkinfra.pix-infraction.log :as log]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(deftest create-is-deprecated
  (testing "create throws the same deprecated error payload sdk-python raises, no network required"
    (let [error (try (pix-infraction/create [{:reference-id "E20018183202201201450u34sDGd19lz"
                                              :type "reversal"
                                              :method "scam"
                                              :operator-email "fraud@company.com"
                                              :operator-phone "+5511989898989"}])
                     nil
                     (catch clojure.lang.ExceptionInfo e e))]
      (is (some? error))
      (is (= "deprecated" (:code (first (:errors (ex-data error))))))))

  (testing "the two-argument arity also throws"
    (is (thrown? clojure.lang.ExceptionInfo (pix-infraction/create [] nil)))))

(deftest ^:sandbox query-pix-infractions
  (set-project)
  (testing "the stream honours the limit"
    (is (= 4 (count (take 200 (pix-infraction/query {:limit 4})))))))

(deftest ^:sandbox page-pix-infractions
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(pix-infraction/page %) 2 {:limit 2}))))))

(deftest ^:sandbox get-pix-infraction
  (set-project)
  (testing "a queried infraction can be retrieved by its id"
    (let [queried (first (pix-infraction/query {:limit 1}))
          fetched (pix-infraction/get (:id queried))]
      (is (= (:id queried) (:id fetched))))))

(deftest ^:sandbox update-pix-infraction
  (set-project)
  (testing "an existing infraction can be patched"
    (let [infraction (first (pix-infraction/query {:limit 1}))
          updated (pix-infraction/update (:id infraction) {:result "agreed"})]
      (is (some? (:id updated))))))

(deftest ^:sandbox cancel-pix-infraction
  (set-project)
  (testing "an existing infraction can be canceled"
    (let [infraction (first (pix-infraction/query {:limit 1}))
          canceled (pix-infraction/cancel (:id infraction))]
      (is (some? (:id canceled))))))

(deftest ^:sandbox query-and-get-pix-infraction-logs
  (set-project)
  (testing "a log carries its infraction"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:infraction fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
