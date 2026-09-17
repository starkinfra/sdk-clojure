(ns starkinfra.individual-account-attachment-test
  "Mirrors sdk-python tests/sdk/testIndividualAccountAttachment.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.individual-account-attachment :as individual-account-attachment]
            [starkinfra.individual-account-attachment.log :as log]
            [starkinfra.individual-account-request :as individual-account-request]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-address []
  {:street "Rua do Estilo Barroco"
   :number "648"
   :neighborhood "Santo Amaro"
   :city "Sao Paulo"
   :state "SP"
   :zip-code "05724005"})

(defn- example-account-request-id []
  (:id (first (individual-account-request/create
               [{:name "Walter White"
                 :tax-id "012.345.678-90"
                 :address (example-address)
                 :income 1000000
                 :birth-date "1965-09-07"}]))))

(defn- example-attachment [account-request-id]
  {:type "identity-front"
   :content "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
   :account-request-id account-request-id
   :tags ["breaking" "bad"]})


(deftest ^:sandbox create-and-get-individual-account-attachments
  (set-project)
  (testing "every created attachment can be retrieved by its id"
    (let [account-request-id (example-account-request-id)
          attachments (individual-account-attachment/create [(example-attachment account-request-id)])]
      (doseq [attachment attachments]
        (is (some? (:id attachment)))
        (is (= (:id attachment) (:id (individual-account-attachment/get (:id attachment)))))))))

(deftest ^:sandbox create-individual-account-attachment-encodes-raw-bytes
  (set-project)
  (testing "raw bytes with a content-type become a data url and drop content-type"
    (let [account-request-id (example-account-request-id)
          attachment (first (individual-account-attachment/create
                              [{:type "identity-back"
                                :content (.getBytes "not-a-real-image" "UTF-8")
                                :content-type "image/png"
                                :account-request-id account-request-id}]))]
      (is (some? (:id attachment))))))

(deftest ^:sandbox query-individual-account-attachments
  (set-project)
  (testing "the stream honours the limit"
    (is (= 3 (count (take 200 (individual-account-attachment/query {:limit 3})))))))

(deftest ^:sandbox page-individual-account-attachments
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (= 4 (count (page/get-ids #(individual-account-attachment/page %) 2 {:limit 2}))))))

(deftest ^:sandbox cancel-individual-account-attachment
  (set-project)
  (testing "a freshly created attachment can be canceled"
    (let [account-request-id (example-account-request-id)
          created (first (individual-account-attachment/create [(example-attachment account-request-id)]))
          canceled (individual-account-attachment/cancel (:id created))]
      (is (= (:id created) (:id canceled)))
      (is (= "deleted" (:status canceled))))))

(deftest ^:sandbox query-and-get-individual-account-attachment-logs
  (set-project)
  (testing "a log carries its attachment"
    (let [queried (first (log/query {:limit 1}))
          fetched (log/get (:id queried))]
      (is (= (:id queried) (:id fetched)))
      (is (map? (:attachment fetched)))
      (is (string? (:created fetched)))))

  (testing "two pages of two log ids never repeat an id"
    (is (= 4 (count (page/get-ids #(log/page %) 2 {:limit 2}))))))
