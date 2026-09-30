(ns starkinfra.ai-knowledge-base-test
  "Mirrors sdk-python tests/sdk/testAiKnowledgeBase.py. The ^:sandbox tests need
  SANDBOX_* credentials and share one knowledge base, deleted when the namespace
  ends. The HTTP boundary tests replace only clj-http's request."
  (:require [cheshire.core :as cheshire]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [starkinfra.ai-knowledge-base :as ai-knowledge-base]
            [starkinfra.utils.ai-fixtures :refer [answer error-codes project random-name replying
                                                  sent-params sent-path thrown]]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-knowledge-base []
  {:name (random-name "sdk-clojure")
   :root-url "https://docs.starkinfra.com"
   :is-recursive false
   :tags ["sdk-clojure" "test"]})

(def ^:private shared (delay (ai-knowledge-base/create (example-knowledge-base))))

(defn- delete-shared [fixtures]
  (fixtures)
  (when (realized? shared)
    (ai-knowledge-base/delete [(:id @shared)])))

(use-fixtures :once delete-shared)


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
  (ai-knowledge-base/update (:id @shared) {:name (:name @shared) :tags (:tags @shared)}))

(deftest ^:sandbox page-lists-the-knowledge-base
  (set-project)
  (let [page (ai-knowledge-base/page {:limit 100 :ids [(:id @shared)]})]
    (is (= [(:id @shared)] (map :id (:content page))))
    (is (contains? page :cursor))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [exception (thrown #(ai-knowledge-base/page {:limit 101}))]
    (is (= 400 (:status (ex-data exception))))
    (is (= ["invalidLimit"] (error-codes exception)))))

(deftest ^:sandbox delete-returns-the-deleted-knowledge-base
  (set-project)
  (let [created (ai-knowledge-base/create (example-knowledge-base))]
    (is (= [(:id created)] (map :id (ai-knowledge-base/delete [(:id created)]))))))

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


(def ^:private documented-base
  {:id "6767676767676767"
   :name "Public Documentation"
   :rootUrl "https://docs.starkinfra.com"
   :isRecursive true
   :status "success"
   :tags ["support"]
   :created "2022-01-01T00:00:00.000000+00:00"
   :updated "2022-01-02T00:00:00.000000+00:00"})

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

(deftest query-sends-the-filters-and-the-ids-comma-separated
  (let [result (atom nil)
        sent (answer {:cursor nil :knowledgeBases [documented-base]}
                     #(reset! result (doall (ai-knowledge-base/query {:ids ["6767676767676767" "6767676767676768"]
                                                                      :name "docs"
                                                                      :status "success"}
                                                                     @project))))]
    (is (= "https://sandbox.api.starkinfra.com/v2/ai-knowledge-base" (sent-path sent)))
    (is (= {"ids" "6767676767676767,6767676767676768" "name" "docs" "status" "success"} (sent-params sent)))
    (is (= ["6767676767676767"] (map :id @result)))))

(deftest query-follows-the-cursor-through-empty-pages-and-honours-the-limit
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :knowledgeBases (vec (repeat 100 documented-base))}
                        {:cursor "c2" :knowledgeBases []}
                        {:cursor "c3" :knowledgeBases (vec (repeat 50 documented-base))}]
                       #(reset! result (doall (ai-knowledge-base/query {:limit 150 :name "docs"} @project))))]
    (is (= 150 (count @result)))
    (is (= [{"limit" "100" "name" "docs"}
            {"limit" "50" "name" "docs" "cursor" "c1"}
            {"limit" "50" "name" "docs" "cursor" "c2"}]
           (map sent-params sent)))))

(deftest query-without-limit-ends-when-the-cursor-is-nil
  (let [result (atom nil)
        sent (replying [{:cursor "c1" :knowledgeBases [documented-base]}
                        {:cursor nil :knowledgeBases [documented-base]}]
                       #(reset! result (doall (ai-knowledge-base/query {} @project))))]
    (is (= 2 (count @result)))
    (is (= [{} {"cursor" "c1"}] (map sent-params sent)))))

(deftest page-returns-the-items-and-the-cursor-and-sends-its-parameters
  (let [result (atom nil)
        sent (answer {:cursor "next-page" :knowledgeBases [documented-base]}
                     #(reset! result (ai-knowledge-base/page {:cursor "c1" :limit 2 :ids ["6767676767676767"]} @project)))]
    (is (= {"cursor" "c1" "limit" "2" "ids" "6767676767676767"} (sent-params sent)))
    (is (= "next-page" (:cursor @result)))
    (is (= ["6767676767676767"] (map :id (:content @result))))
    (is (= "https://docs.starkinfra.com" (:root-url (first (:content @result)))))))

(deftest page-has-a-nil-cursor-on-the-last-page
  (let [result (atom nil)]
    (answer {:cursor nil :knowledgeBases [documented-base]}
            #(reset! result (ai-knowledge-base/page {} @project)))
    (is (nil? (:cursor @result)))))

(deftest create-and-query-round-trip-a-non-ascii-name-as-utf-8
  (let [name "olá 日本 \"q\" \uD83D\uDE00"
        created (answer {:knowledgeBase documented-base}
                        #(ai-knowledge-base/create {:name name :root-url "https://docs.starkinfra.com"} @project))
        listed (answer {:cursor nil :knowledgeBases []}
                       #(doall (ai-knowledge-base/query {:name name} @project)))]
    (is (= name (:name (cheshire/parse-string (:body created) true))))
    (is (= name (get (sent-params listed) "name")))))
