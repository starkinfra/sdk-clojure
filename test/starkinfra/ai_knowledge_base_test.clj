(ns starkinfra.ai-knowledge-base-test
  "Mirrors sdk-python tests/sdk/testAiKnowledgeBase.py. The ^:sandbox tests need
  SANDBOX_* credentials and share ONE knowledge base, because the sandbox cannot
  delete what it creates. hosts and delete answer HTTP 500 there, so they are
  checked at the HTTP boundary: only clj-http's request is replaced."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [starkinfra.ai-knowledge-base :as ai-knowledge-base]
            [starkinfra.key :as key]
            [starkinfra.user :as user]
            [starkinfra.utils.user :refer [set-project]]))

(defn- random-name []
  (str "sdk-clojure-" (apply str (repeatedly 12 #(format "%x" (rand-int 16))))))

(defn- example-knowledge-base []
  {:name (random-name)
   :root-url "https://docs.starkinfra.com"
   :is-recursive false
   :tags ["sdk-clojure" "test"]})

;; Built on first use, so the offline suite never reaches the sandbox.
(def ^:private shared (delay (ai-knowledge-base/create (example-knowledge-base))))

(defn- delete-shared [fixtures]
  (fixtures)
  (when (realized? shared)
    (try
      (ai-knowledge-base/delete [(:id @shared)])
      (catch clojure.lang.ExceptionInfo exception
        (when-not (= 500 (:status (ex-data exception)))
          (throw exception))
        (binding [*out* *err*]
          (println (str "AiKnowledgeBase " (:id @shared) " was not deleted: the API answered 500")))))))

(use-fixtures :once delete-shared)


(defn- error-codes [exception]
  (map :code (:errors (ex-data exception))))

(defn- thrown [f]
  (try
    (f)
    nil
    (catch clojure.lang.ExceptionInfo exception exception)))


(deftest ^:sandbox create-returns-processing-knowledge-base
  (set-project)
  (let [created @shared]
    (is (string? (:id created)))
    (is (= "processing" (:status created)))
    (is (= "https://docs.starkinfra.com" (:root-url created)))
    (is (= false (:is-recursive created)))
    (is (= ["sdk-clojure" "test"] (:tags created)))
    (is (string? (:created created)))))

(deftest ^:sandbox get-knowledge-base
  (set-project)
  (let [fetched (ai-knowledge-base/get (:id @shared))]
    (is (= (:id @shared) (:id fetched)))
    (is (= (:name @shared) (:name fetched)))))

(deftest ^:sandbox query-knowledge-bases
  (set-project)
  (testing "filters by ids"
    (is (= [(:id @shared)]
           (map :id (ai-knowledge-base/query {:ids [(:id @shared)]})))))

  (testing "filters by name and status"
    ;; the crawl may have finished since create, so the status is read back
    (let [current (ai-knowledge-base/get (:id @shared))]
      (is (some #{(:id @shared)}
                (map :id (ai-knowledge-base/query {:name (:name @shared) :status (:status current)}))))))

  (testing "a name that matches nothing returns nothing"
    (is (empty? (ai-knowledge-base/query {:name "no-knowledge-base-has-this-name"})))))

(deftest ^:sandbox update-changes-name-and-tags-only
  (set-project)
  (let [updated (ai-knowledge-base/update (:id @shared) {:name "renamed-by-sdk" :tags ["renamed"]})]
    (is (= "renamed-by-sdk" (:name updated)))
    (is (= ["renamed"] (:tags updated)))
    (is (= (:root-url @shared) (:root-url updated))))
  ;; tests run in no fixed order and share one base, so put it back
  (ai-knowledge-base/update (:id @shared) {:name (:name @shared) :tags (:tags @shared)}))

(deftest ^:sandbox create-with-invalid-root-url-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-knowledge-base/create {:name "invalid" :root-url "not-a-url"}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidRootUrl"] (error-codes exception)))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [exception (thrown #(ai-knowledge-base/get "0000000000000000"))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidKnowledgeBaseId"] (error-codes exception)))))


(def ^:private project
  (delay (user/project "sandbox" "5656565656565656" (:private-pem (key/create)))))

(def ^:private documented-base
  {:id "6767676767676767"
   :name "Public Documentation"
   :rootUrl "https://docs.starkinfra.com"
   :isRecursive true
   :status "success"
   :tags ["support"]
   :created "2022-01-01T00:00:00.000000+00:00"
   :updated "2022-01-02T00:00:00.000000+00:00"})

(defn- answer
  "Replaces clj-http's request with a stand-in that records what was sent and
  replies 200 with `body`. Returns the recorded request."
  [body f]
  (let [sent (atom nil)]
    (with-redefs [client/request (fn [request]
                                   (reset! sent request)
                                   (let [json (cheshire/generate-string body)]
                                     {:status 200
                                      :body (if (= :byte-array (:as request)) (.getBytes json "UTF-8") json)}))]
      (f))
    @sent))

(deftest hosts-groups-pages-by-host
  (let [pages [{:originalUrl "https://docs.starkinfra.com/get-started"
                :status "success"
                :storageUrl "https://storage.googleapis.com/ai-knowledge/6767676767676767/get-started.md"}]
        result (atom nil)
        sent (answer {:hosts {"docs2.starkinfra.com" pages}}
                     #(reset! result (ai-knowledge-base/hosts "6767676767676767" @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-knowledge-base/6767676767676767/hosts" (:url sent)))
    (is (= :get (:method sent)))
    (is (= [{:original-url "https://docs.starkinfra.com/get-started"
             :status "success"
             :storage-url "https://storage.googleapis.com/ai-knowledge/6767676767676767/get-started.md"}]
           (get @result :docs2.starkinfra.com)))))

(deftest delete-sends-ids-in-the-query-string-and-returns-the-deleted-objects
  (let [result (atom nil)
        sent (answer {:knowledgeBases [documented-base]}
                     #(reset! result (ai-knowledge-base/delete ["6767676767676767" "6767676767676768"] @project)))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-knowledge-base?ids=6767676767676767%2C6767676767676768"
           (:url sent)))
    (is (= :delete (:method sent)))
    (is (= "" (:body sent)))
    (is (= ["6767676767676767"] (map :id @result)))
    (is (= "Public Documentation" (:name (first @result))))
    (is (= "https://docs.starkinfra.com" (:root-url (first @result))))))

(deftest update-body-keeps-false-and-drops-absent-fields
  (let [sent (answer {:knowledgeBase documented-base}
                     #(ai-knowledge-base/update "6767676767676767" {:is-recursive false} @project))]
    (is (= :patch (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-knowledge-base/6767676767676767" (:url sent)))
    (is (= {:isRecursive false} (cheshire/parse-string (:body sent) true)))))

(deftest create-sends-one-object-and-reads-the-knowledge-base-key
  (let [result (atom nil)
        sent (answer {:knowledgeBase documented-base}
                     #(reset! result (ai-knowledge-base/create {:name "Public Documentation"
                                                                :root-url "https://docs.starkinfra.com"}
                                                               @project)))]
    (is (= :post (:method sent)))
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-knowledge-base" (:url sent)))
    (is (= {:name "Public Documentation" :rootUrl "https://docs.starkinfra.com"}
           (cheshire/parse-string (:body sent) true)))
    (is (= "6767676767676767" (:id @result)))))

(deftest create-sends-only-the-creatable-fields
  (let [returned {:id "6767676767676767"
                  :name "Public Documentation"
                  :root-url "https://docs.starkinfra.com"
                  :is-recursive false
                  :tags ["support"]
                  :status "success"
                  :created "2022-01-01T00:00:00.000000+00:00"
                  :updated "2022-01-02T00:00:00.000000+00:00"}
        sent (answer {:knowledgeBase documented-base}
                     #(ai-knowledge-base/create returned @project))]
    (is (= {:name "Public Documentation"
            :rootUrl "https://docs.starkinfra.com"
            :isRecursive false
            :tags ["support"]}
           (cheshire/parse-string (:body sent) true)))))

(deftest query-is-not-paginated
  (let [result (atom nil)
        sent (answer {:knowledgeBases [documented-base]}
                     #(reset! result (doall (ai-knowledge-base/query {:ids ["6767676767676767"] :name "docs"} @project))))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-knowledge-base?ids=6767676767676767&name=docs" (:url sent)))
    (is (not (string/includes? (:url sent) "limit")))
    (is (= ["6767676767676767"] (map :id @result)))))
