(ns starkinfra.brcode-preview-test
  "Mirrors sdk-python tests/sdk/testBrcodePreview.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.brcode-preview :as brcode-preview]
            [starkinfra.dynamic-brcode :as dynamic-brcode]
            [starkinfra.static-brcode :as static-brcode]
            [starkinfra.utils.user :refer [set-project]]))

(deftest ^:sandbox create-brcode-previews-for-existing-brcodes
  (set-project)
  (testing "previewing existing static and dynamic brcodes returns the same id"
    (let [statics (take 2 (static-brcode/query {:limit 2}))
          dynamics (take 2 (dynamic-brcode/query {:limit 2}))
          brcodes (concat statics dynamics)]
      (when (seq brcodes)
        (let [previews (brcode-preview/create
                        (mapv (fn [brcode] {:id (:id brcode) :payer-id "20018183000180"}) brcodes))]
          (is (= (count brcodes) (count previews)))
          (doseq [[brcode preview] (map vector brcodes previews)]
            (is (= (:id brcode) (:id preview)))))))))

(deftest ^:sandbox create-brcode-preview-for-a-subscription-brcode
  (set-project)
  (testing "a subscription DynamicBrcode preview carries a Subscription map"
    (let [created (dynamic-brcode/create [{:name "Jamie Lannister"
                                           :city "Rio de Janeiro"
                                           :external-id (str "clojure-sdk-" (rand-int 1000000000))
                                           :type "subscription"}])
          brcode (first created)
          previews (brcode-preview/create [{:id (:id brcode) :payer-id "20018183000180"}])
          preview (first previews)]
      (is (= (:id brcode) (:id preview)))
      (is (map? (:subscription preview)))
      (is (= "qrcode" (:type (:subscription preview)))))))
