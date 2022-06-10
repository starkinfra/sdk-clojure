;; (ns starkinfra.pix-infraction-test
;;     (:use [clojure.test])
;;     (:require [starkinfra.pix-infraction :as infraction]
;;                 [starkinfra.pix-infraction.log :as log]
;;                 [starkinfra.user-test :as user]
;;                 [clojure.java.io :as io]
;;                 [starkinfra.utils.date :as date]
;;                 [starkinfra.utils.page :as page])
;;     (:import [com.starkinfra.utils EndToEndId]))


;; (deftest query-infractions
;;   (testing "query pix infractions"
;;     (user/set-test-project)
;;     (def infractions (take 200 (infraction/query {:limit 10})))
;;     (is (<= 10 (count infractions)))))

;; (deftest page-infractions
;;   (testing "page pix infractions"
;;     (user/set-test-project)
;;     (def get-page (fn [params] (infraction/page params)))
;;     (def ids (page/get-ids get-page 1 {:limit 4}))
;;     (is (= 4 (count ids)))))

;; (deftest query-get-infractions
;;   (testing "query and get pix infractions"
;;     (user/set-test-project)
;;     (def infractions (take 200 (infraction/query {:limit 10})))
;;     (def compare (fn [ch1, ch2] (is (:id ch1) (:id ch2))))
;;     (doseq [ch1 infractions] (compare ch1 (infraction/get (:id ch1))))))

;; (deftest create-get-infraction
;;   (testing "create and get pix infraction"
;;     (user/set-test-project)
;;     (def end-to-end-id (EndToEndId/create "20018183"))
;;     (def created-infraction (infraction/create
;;         [{
;;             :reference-id end-to-end-id
;;             :type "fraud"
;;         }]
;;     ))
;;     (is (not (nil? (:id (first created-infraction)))))))

;; (defn get-infraction-to-patch
;;     []
;;     (def infractions [])
;;     (def check (fn [infraction-to-check] (= (:agent infraction-to-check) "credited")))
;;     (while (<= (count infractions) 0)
;;         (def infraction-list (infraction/query {:limit 10 :status "delivered"}))
;;             (doseq [infraction-test infraction-list]
;;                 (if (check infraction-test)
;;                     (def infractions [infraction-test]))
;;             ))
;;     {:id (:id (first infractions))})

;; (deftest query-update-infraction
;;     (testing "query and update pix infraction"
;;         (user/set-test-project)
;;         (def pix-infraction (get-infraction-to-patch))
;;         (def id (:id  pix-infraction))
;;         (def result (rand-nth ["agreed" "disagreed"]))
;;         (def - `:updated`-infraction (infraction/update id result))
;;         (is (:result (first - `:updated`-infraction)) result)
;;         (is (:id (first - `:updated`-infraction)) id)))

;; (deftest query-get-pix-infraction-logs
;;   (testing "query and get pix infraction logs"
;;     (user/set-test-project)
;;     (def infraction-logs (log/query {:limit 1 :type "solved"}))
;;     (is (= 1 (count infraction-logs)))
;;     (def infraction-log (log/get (:id (first infraction-logs))))
;;     (is (map? (:infraction infraction-log)))
;;     (is (not (nil? (:id infraction-log))))
;;     (is (string? (:created infraction-log)))))

;; (deftest page-pix-infraction-log
;;   (testing "page pix-infraction-log"
;;     (user/set-test-project)
;;     (def get-page (fn [params] (log/page params)))
;;     (def ids (page/get-ids get-page 1 {:limit 4}))
;;     (is (= 4 (count ids)))))
