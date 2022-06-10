(ns starkinfra.pix-director-test
  (:use [clojure.test])
  (:require [starkinfra.pix-director :as director]
            [starkinfra.user-test :as user]))

(deftest create-director
    (testing "create pix director"
        (user/set-test-project)
        (def director (starkinfra.pix-director/create {
            :name "Edward Stark"
            :tax-id "03.300.300/0001-00"
            :phone "+5511999999999"
            :email "ned.stark@company.com"
            :password "12345678"
            :team-email "pix.team@company.com"
            :team-phones ["+5511988889999" "+5511988889998"]
        }))
        (is (not (nil? director)))))