(ns starkinfra.request-test
  "Mirrors sdk-python tests/sdk/testRequest.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.request :as request]
            [starkinfra.utils.user :refer [set-project starkinfra-project]]))


(deftest ^:sandbox raw-get
  (set-project)
  (testing "a listing and then the single resource behind the first id"
    (let [listing (request/get "pix-request" {:limit 1})
          id (:id (first (get-in listing [:content :requests])))
          single (request/get "pix-request" {} @starkinfra-project)]
      (is (= 200 (:status listing)))
      (is (some? id))
      (is (= id (get-in (request/get (str "pix-request/" id)) [:content :request :id])))
      (is (= 200 (:status single)))))

  (testing "logs are reachable on the same path shape"
    (is (= 200 (:status (request/get "pix-request/log" {:limit 1}))))))

(deftest ^:sandbox raw-post-patch-and-delete
  (set-project)
  (testing "an issuing holder is created, patched and cancelled through raw verbs"
    (let [external-id (str "clojure-sdk-" (rand-int 1000000000))
          created (request/post "issuing-holder"
                                {:holders [{:name "Jaime Lannister"
                                            :externalId external-id
                                            :taxId "012.345.678-90"}]})
          id (:id (first (get-in created [:content :holders])))]
      (is (= 200 (:status created)))
      (is (= external-id (get-in (request/get (str "issuing-holder/" id))
                                 [:content :holder :external-id])))
      (is (= 200 (:status (request/patch (str "issuing-holder/" id) {:tags ["Arya" "Stark"]}))))
      (is (= "canceled" (get-in (request/delete (str "issuing-holder/" id))
                                [:content :holder :status]))))))

(deftest ^:sandbox raw-errors-do-not-throw
  (set-project)
  (testing "sdk-python passes raiseException=False, so an API error is data"
    (let [response (request/get "pix-request/0")]
      (is (not= 200 (:status response)))
      (is (some? (:content response))))))
