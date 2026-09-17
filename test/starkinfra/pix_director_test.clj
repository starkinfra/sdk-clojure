(ns starkinfra.pix-director-test
  "Mirrors sdk-python tests/sdk/testPixDirector.py. Needs SANDBOX_* credentials."
  (:require [clojure.test :refer [deftest is testing]]
            [starkinfra.pix-director :as pix-director]
            [starkinfra.utils.user :refer [set-project]]))

(defn- example-director []
  (let [suffix (rand-int 1000000000)]
    {:name "Edward Stark"
     :tax-id "012.345.678-90"
     :phone (str "+5511" (+ 100000000 (rand-int 899999999)))
     :email (str "ned.stark+" suffix "@starkbank.com")
     :password (str suffix)
     :team-email (str "pix.team+" suffix "@company.com")
     :team-phones [(str "+5511" (+ 100000000 (rand-int 899999999)))
                   (str "+5511" (+ 100000000 (rand-int 899999999)))]}))


(deftest ^:sandbox create-pix-director
  (set-project)
  (testing "the created director echoes back what was sent"
    (let [director (example-director)
          created (pix-director/create director)]
      (is (= (:name director) (:name created)))
      (is (= (:phone director) (:phone created)))
      (is (= (:tax-id director) (:tax-id created))))))
