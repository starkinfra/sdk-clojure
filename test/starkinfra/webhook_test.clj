(ns starkinfra.webhook-test
  "Mirrors sdk-python tests/sdk/testWebhook.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.utils.page :as page]
            [starkinfra.utils.user :refer [set-project]]
            [starkinfra.webhook :as webhook]))


(deftest ^:sandbox create-get-and-delete-webhooks
  (set-project)
  (testing "a created webhook can be retrieved and then deleted"
    (let [created (webhook/create {:url "https://webhook.site/60e9c18e-4b5c-4369-bda1-ab5fcd8e1b29"
                                   :subscriptions ["credit-note" "pix-request.in" "pix-request.out"]})]
      (is (some? (:id created)))
      (is (= (:id created) (:id (webhook/get (:id created)))))
      (is (= (:id created) (:id (webhook/delete (:id created))))))))

(deftest ^:sandbox query-webhooks
  (set-project)
  (testing "the stream yields maps carrying a url"
    (doseq [subscription (take 10 (webhook/query {:limit 10}))]
      (is (string? (:url subscription))))))

(deftest ^:sandbox page-webhooks
  (set-project)
  (testing "two pages of two ids never repeat an id"
    (is (>= 4 (count (page/get-ids #(webhook/page %) 2 {:limit 2}))))))
