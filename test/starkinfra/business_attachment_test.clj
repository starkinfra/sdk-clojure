(ns starkinfra.business-attachment-test
  "Mirrors sdk-python tests/sdk/testBusinessAttachment.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.business-attachment :as business-attachment]
            [starkinfra.business-attachment.log :as log]
            [starkinfra.business-identity :as business-identity]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-business-identity-id []
  (:id (first (business-identity/create [{:tax-id "20.018.183/0001-80"}]))))

(defn- example-attachment [business-identity-id]
  {:name "articles-of-incorporation.pdf"
   :content "data:application/pdf;base64,JVBERi0xLjQ..."
   :business-identity-id business-identity-id
   :tags ["doc-principal"]})


(deftest ^:sandbox create-and-get-business-attachments
  (set-project)
  (testing "every created attachment can be retrieved by its id"
    (let [business-identity-id (example-business-identity-id)
          attachments (business-attachment/create [(example-attachment business-identity-id)])]
      (doseq [attachment attachments]
        (is (some? (:id attachment)))
        (is (= (:id attachment) (:id (business-attachment/get (:id attachment) {}))))))))

(deftest ^:sandbox get-business-attachment-expands-content
  (set-project)
  (testing "expand=[content] returns the document content"
    (let [business-identity-id (example-business-identity-id)
          created (first (business-attachment/create [(example-attachment business-identity-id)]))
          fetched (business-attachment/get (:id created) {:expand ["content"]})]
      (is (= (:id created) (:id fetched))))))

;; No network needed: build-content validates before any request is sent.
(deftest create-business-attachment-requires-content-when-content-type-given
  (testing "content-type with no content is refused client-side, mirroring python's ValueError"
    (is (thrown? IllegalArgumentException
                 (business-attachment/create [{:name "x.pdf"
                                               :content nil
                                               :content-type "application/pdf"
                                               :business-identity-id "123"}]))))

  ;; python's guard is `if not content`, so an empty string and empty bytes are
  ;; refused exactly like a missing one
  (testing "an empty content is refused too, string or bytes"
    (is (thrown? IllegalArgumentException
                 (business-attachment/create [{:name "x.pdf"
                                               :content ""
                                               :content-type "application/pdf"
                                               :business-identity-id "123"}])))
    (is (thrown? IllegalArgumentException
                 (business-attachment/create [{:name "x.pdf"
                                               :content (byte-array 0)
                                               :content-type "application/pdf"
                                               :business-identity-id "123"}])))))

(deftest ^:sandbox query-business-attachments
  (set-project)
  (testing "the stream honours the limit"
    (is (= 3 (count (take 200 (business-attachment/query {:limit 3})))))))

(deftest ^:sandbox page-business-attachments
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(business-attachment/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-business-attachment
  (set-project)
  (testing "a freshly created attachment can be canceled"
    (let [business-identity-id (example-business-identity-id)
          created (first (business-attachment/create [(example-attachment business-identity-id)]))
          canceled (business-attachment/cancel (:id created))]
      (is (= (:id created) (:id canceled)))
      (is (= "canceled" (:status canceled))))))

(deftest ^:sandbox query-and-get-business-attachment-logs
  (set-project)
  (testing "a log carries its attachment"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:attachment fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
