(ns starkinfra.ai-knowledge-base-test
  "Mirrors sdk-python tests/sdk/testAiKnowledgeBase.py. Needs SANDBOX_* credentials; one knowledge base is
  created for the namespace and deleted when it ends."
  (:require [clojure.test :refer [deftest is testing use-fixtures]]
            [starkinfra.ai-knowledge-base :as ai-knowledge-base]
            [starkinfra.utils.ai-fixtures :refer [random-name]]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-knowledge-base []
  {:name (random-name "sdk-clojure")
   :root-url "https://docs.starkinfra.com"
   :is-recursive false
   :tags ["sdk-clojure" "test"]})

(def ^:private shared (delay (ai-knowledge-base/create (example-knowledge-base))))

(defn- delete-shared [tests]
  (tests)
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

(deftest ^:sandbox delete-returns-the-deleted-knowledge-base
  (set-project)
  (let [created (ai-knowledge-base/create (example-knowledge-base))]
    (is (= [(:id created)] (map :id (ai-knowledge-base/delete [(:id created)]))))))

(deftest ^:sandbox hosts-returns-the-api-answer
  (set-project)
  (is (map? (:hosts (ai-knowledge-base/hosts (:id @shared))))))

(deftest hosts-keep-the-host-names-the-api-sent
  (let [restore-host @#'ai-knowledge-base/restore-host]
    (is (= :docs2.starkinfra.com (restore-host :docs-2.starkinfra.com)))
    (is (= :my-2fa.example.com (restore-host :my--2fa.example.com)))
    (is (= :s33.example.com (restore-host :s-3-3.example.com)))
    (is (= :docs.starkinfra.com (restore-host :docs.starkinfra.com)))))

(deftest ^:sandbox create-with-invalid-root-url-raises-input-errors
  (set-project)
  (let [error (try (ai-knowledge-base/create {:name "invalid" :root-url "not-a-url"})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidRootUrl"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [error (try (ai-knowledge-base/get "0000000000000000")
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidKnowledgeBaseId"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [error (try (ai-knowledge-base/page {:limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-knowledge-base/query {:limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
