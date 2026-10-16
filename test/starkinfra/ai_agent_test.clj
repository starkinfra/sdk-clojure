(ns starkinfra.ai-agent-test
  "Mirrors sdk-python tests/sdk/testAiAgent.py. Needs SANDBOX_* credentials; the knowledge base and the agent
  of the namespace are created and deleted by the with-agent fixture."
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [starkinfra.ai-agent :as ai-agent]
            [starkinfra.ai-knowledge-base :as ai-knowledge-base]
            [starkinfra.utils.ai-fixtures :refer [example-agent random-name]]
            [starkinfra.utils.user :refer [set-project]]))

(def ^:dynamic *knowledge-base* nil)

(def ^:dynamic *test-agent* nil)

(defn- with-agent [tests]
  (set-project)
  (let [knowledge-base (ai-knowledge-base/create {:name (random-name "sdk-clojure-kb")
                                                  :root-url "https://docs.starkinfra.com"
                                                  :is-recursive false})
        agent (ai-agent/create (example-agent [(:id knowledge-base)]))]
    (try
      (binding [*knowledge-base* knowledge-base
                *test-agent* agent]
        (tests))
      (finally
        (ai-agent/delete [(:id agent)])
        (ai-knowledge-base/delete [(:id knowledge-base)])))))

(use-fixtures :once with-agent)


(deftest ^:sandbox create-converts-the-schema-keys-to-camel-case
  (set-project)
  (let [agent *test-agent*]
    (is (string? (:id agent)))
    (is (= "bender-1.0" (:model agent)))
    (is (= [(:id *knowledge-base*)] (:knowledge-base-ids agent)))
    (is (= [:order-id] (keys (:metadata-schema agent))))
    (is (= "string" (get-in agent [:metadata-schema :order-id :type])))
    (is (string? (:created agent)))))

(deftest ^:sandbox get-and-expand-knowledge-bases
  (set-project)
  (let [agent *test-agent*
        plain (ai-agent/get (:id agent))
        expanded (ai-agent/get (:id agent) {:expand [:knowledge-bases]})]
    (is (= (:id agent) (:id plain)))
    (is (nil? (:knowledge-bases plain)))
    (is (= [(:id *knowledge-base*)] (map :id (:knowledge-bases expanded))))))

(deftest ^:sandbox query-and-page-list-the-agent
  (set-project)
  (let [agent *test-agent*
        queried (doall (ai-agent/query {:limit 5 :expand [:knowledge-bases]}))
        page (ai-agent/page {:limit 100})]
    (is (some #{(:id agent)} (map :id (ai-agent/query))))
    (is (<= (count queried) 5))
    (is (vector? (:content page)))
    (is (contains? page :cursor))))

(deftest ^:sandbox page-follows-the-cursor-to-the-next-page
  (set-project)
  (let [first-page (ai-agent/page {:limit 1})]
    (when (:cursor first-page)
      (let [second-page (ai-agent/page {:limit 1 :cursor (:cursor first-page)})]
        (is (not= (map :id (:content first-page)) (map :id (:content second-page))))))))

(deftest ^:sandbox update-changes-what-is-given-and-keeps-what-is-not
  (set-project)
  (let [agent *test-agent*
        original (ai-agent/get (:id agent))]
    (try
      (let [renamed (ai-agent/update (:id agent) {:name "renamed-by-sdk"})]
        (is (= "renamed-by-sdk" (:name renamed)))
        (is (= [(:id *knowledge-base*)] (:knowledge-base-ids renamed)))
        (is (= [:order-id] (keys (:metadata-schema renamed)))))
      (finally
        (ai-agent/update (:id agent) {:name (:name original)})))))

(deftest ^:sandbox update-with-an-empty-list-clears-the-knowledge-bases
  (set-project)
  (let [agent (ai-agent/create (example-agent [(:id *knowledge-base*)]))]
    (try
      (is (= [] (:knowledge-base-ids (ai-agent/update (:id agent) {:knowledge-base-ids []}))))
      (finally
        (ai-agent/delete [(:id agent)])))))

(deftest ^:sandbox delete-returns-the-deleted-agents
  (set-project)
  (let [agent (ai-agent/create (example-agent))]
    (is (= [(:id agent)] (map :id (ai-agent/delete [(:id agent)]))))))

(deftest ^:sandbox create-with-an-invalid-model-raises-input-errors
  (set-project)
  (let [error (try (ai-agent/create {:name "invalid" :model "gpt"})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (seq (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox get-unknown-id-raises-input-errors
  (set-project)
  (let [error (try (ai-agent/get "0000000000000000")
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (seq (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox page-with-limit-101-raises-the-api-error
  (set-project)
  (let [error (try (ai-agent/page {:limit 101})
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))

(deftest ^:sandbox query-with-limit-minus-1-raises-the-api-error
  (set-project)
  (let [error (try (doall (ai-agent/query {:limit -1}))
                   nil
                   (catch clojure.lang.ExceptionInfo e e))]
    (is (= 400 (:status (ex-data error))))
    (is (= ["invalidLimit"] (map :code (:errors (ex-data error)))))))
