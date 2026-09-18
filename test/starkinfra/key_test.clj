(ns starkinfra.key-test
  "Mirrors sdk-python tests/sdk/testKey.py. Offline except for the file write,
  which lands in the gitignored temp/ directory."
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing]]
            [starkinfra.key :as key]))


(deftest create-keys
  (testing "the pair is made of pem strings"
    (let [pair (key/create)]
      (is (map? pair))
      (is (string/includes? (:private-pem pair) "PRIVATE KEY"))
      (is (string/includes? (:public-pem pair) "PUBLIC KEY")))))

(deftest create-and-save-keys
  (testing "both pem files land in the given path"
    (let [path "temp/keys"]
      (key/create path)
      (is (.exists (io/file path "privateKey.pem")))
      (is (.exists (io/file path "publicKey.pem"))))))
