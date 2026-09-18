(ns starkinfra.request-layer-test
  "Offline suite for the SDK's own request layer (`starkinfra.utils.request`
  and `starkinfra.utils.rest`). Every test stubs `clj-http.client/request`, so
  nothing here needs credentials or the network: the assertions are on the URL,
  the query string, the headers and the body the SDK *would* have sent, and on
  what it makes of the reply."
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [clojure.test :refer [deftest is testing]]
            [starkinfra.credit-note :as credit-note]
            [starkinfra.credit-signer :as credit-signer]
            [starkinfra.dynamic-brcode :as dynamic-brcode]
            [starkinfra.issuing-embossing-request]
            [starkinfra.key :as key]
            [starkinfra.pix-request :as pix-request]
            [starkinfra.request :as request]
            [starkinfra.user :as user]
            [starkinfra.utils.case :as casing]
            [starkinfra.utils.json :as json]))

;; One throwaway key for the whole suite: signing is exercised, but the key
;; itself never reaches anything.
(def ^:private pem (delay (:private-pem (key/create))))

(def ^:private project (delay (user/project "sandbox" "5656565656565656" @pem)))
(def ^:private organization (delay (user/organization "sandbox" "5656565656565656" @pem)))
(def ^:private scoped (delay (user/organization "sandbox" "5656565656565656" @pem "4848484848484848")))


(defn- page-body [rows cursor]
  (cheshire/generate-string {:requests (vec (repeat rows {:id "5656565656565656"}))
                            :cursor cursor}))

(defn- recorder
  "Returns the requests atom and a `clj-http.client/request` stand-in that
  replies with `responses` in order."
  [responses]
  (let [sent (atom [])
        pending (atom (seq responses))]
    [sent (fn [request]
            (swap! sent conj request)
            (let [response (first @pending)]
              (swap! pending rest)
              response))]))

(defn- thrower [exception]
  (fn [_] (throw exception)))

(defn- query-string [url]
  (second (string/split url #"\?" 2)))

(defn- limit-of [url]
  (second (re-find #"[?&]limit=([^&]*)" url)))

(defn- caught [f]
  (try
    (f)
    nil
    (catch clojure.lang.ExceptionInfo exception exception)))


(deftest query-string-mirrors-python-urlencode
  (testing "false is sent as False, lists are comma-joined, expand values are
           camelCased and only nil members are dropped"
    (let [[sent handler] (recorder [{:status 200 :body (page-body 0 "")}])]
      (with-redefs [client/request handler]
        (pix-request/page {:limit 2
                           :is-delivered false
                           :status ["created" "success"]
                           :expand ["rules" "owner-statistics"]
                           :tags nil
                           :after "2020-03-10"}
                          @project))
      (is (= (str "https://sandbox.api.starkinfra.com/v2/pix-request"
                  "?limit=2&isDelivered=False&status=created%2Csuccess"
                  "&expand=rules%2CownerStatistics&after=2020-03-10")
             (:url (first @sent))))))

  (testing "a request with no filters carries no query string at all"
    (let [[sent handler] (recorder [{:status 200 :body (page-body 0 "")}])]
      (with-redefs [client/request handler]
        (pix-request/page {} @project))
      (is (= "https://sandbox.api.starkinfra.com/v2/pix-request" (:url (first @sent))))))

  (testing "values are percent-encoded the way python's quote_plus is"
    (let [[sent handler] (recorder [{:status 200 :body (page-body 0 "")}])]
      (with-redefs [client/request handler]
        (pix-request/page {:cursor "a b+c/d:e~f_g.h-i"} @project))
      (is (= "cursor=a+b%2Bc%2Fd%3Ae~f_g.h-i" (query-string (:url (first @sent))))))))

(deftest get-stream-request-sequence-matches-python
  (let [limits (fn [limit]
                 (let [[sent handler] (recorder (repeat {:status 200 :body (page-body 100 "next")}))]
                   (with-redefs [client/request handler]
                     (doall (pix-request/query {:limit limit} @project)))
                   (mapv (comp limit-of :url) @sent)))]
    (testing "a limit below one page is one request for exactly that limit"
      (is (= ["5"] (limits 5))))

    (testing "a limit of exactly one page does not ask for a second, empty page"
      (is (= ["100"] (limits 100))))

    (testing "a limit above one page asks for the remainder, not another full page"
      (is (= ["100" "50"] (limits 150)))
      (is (= ["100" "100" "50"] (limits 250))))))

(deftest get-stream-follows-the-cursor-and-stops-when-it-empties
  (testing "no limit means page after page until the cursor comes back empty"
    (let [[sent handler] (recorder [{:status 200 :body (page-body 100 "next")}
                                    {:status 200 :body (page-body 30 "")}])]
      (with-redefs [client/request handler]
        (is (= 130 (count (pix-request/query {} @project)))))
      (is (= 2 (count @sent)))
      (is (nil? (limit-of (:url (first @sent)))))
      (is (= "cursor=next" (query-string (:url (second @sent)))))))

  (testing "an empty cursor stops the stream even while a limit remains, where
           core-clojure keeps paging and re-fetches page one"
    (let [[sent handler] (recorder [{:status 200 :body (page-body 100 "next")}
                                    {:status 200 :body (page-body 0 "")}])]
      (with-redefs [client/request handler]
        (is (= 100 (count (pix-request/query {:limit 250} @project)))))
      (is (= 2 (count @sent))))))

(deftest access-id-names-the-signing-user
  (let [access-id (fn [user]
                    (let [[sent handler] (recorder [{:status 200 :body (page-body 0 "")}])]
                      (with-redefs [client/request handler]
                        (pix-request/page {} user))
                      (get-in (first @sent) [:headers "Access-Id"])))]
    (testing "a project signs as project/{id}"
      (is (= "project/5656565656565656" (access-id @project))))

    (testing "an organization with no workspace signs as organization/{id}"
      (is (= "organization/5656565656565656" (access-id @organization))))

    (testing "an organization scoped to a workspace names it"
      (is (= "organization/5656565656565656/workspace/4848484848484848"
             (access-id @scoped)))
      (is (= "organization/5656565656565656/workspace/4848484848484848"
             (access-id (user/organization-replace @organization "4848484848484848")))))

    (testing "the access id is also readable on the user map itself"
      (is (= "project/5656565656565656" (:access-id @project)))
      (is (= "organization/5656565656565656" (:access-id @organization)))
      (is (= "organization/5656565656565656/workspace/4848484848484848"
             (:access-id @scoped))))))

(deftest signed-request-headers
  (testing "the three Access-* headers and the User-Agent the API expects"
    (let [[sent handler] (recorder [{:status 200 :body (page-body 0 "")}])]
      (with-redefs [client/request handler]
        (pix-request/page {} @project))
      (let [headers (:headers (first @sent))]
        (is (re-matches #"Clojure-\d+\.\d+\.\d+\.-SDK-infra-\d+\.\d+\.\d+"
                        (get headers "User-Agent")))
        (is (= "en-US" (get headers "Accept-Language")))
        (is (= "application/json" (get headers "Content-Type")))
        (is (re-matches #"\d+" (get headers "Access-Time")))
        (is (seq (get headers "Access-Signature")))
        (is (= "project/5656565656565656" (get headers "Access-Id"))))))

  (testing "the raw verbs carry sdk-python's Joker prefix"
    (let [[sent handler] (recorder [{:status 200 :body "{}"}])]
      (with-redefs [client/request handler]
        (request/get "pix-request" {} @project))
      (is (re-matches #"Joker-Clojure-.*-SDK-infra-.*"
                      (get-in (first @sent) [:headers "User-Agent"]))))))

(deftest error-statuses-carry-their-payload
  (testing "400 hands back the API's own error list, plus the status"
    (let [body "{\"errors\": [{\"code\": \"invalidAmount\", \"message\": \"amount must be a positive integer\"}]}"
          exception (with-redefs [client/request (constantly {:status 400 :body body})]
                      (caught #(pix-request/get "1" @project)))]
      (is (some? exception))
      (is (= 400 (:status (ex-data exception))))
      (is (= [{:code "invalidAmount" :message "amount must be a positive integer"}]
             (:errors (ex-data exception))))))

  (testing "404 and any other unmapped status are unknownError, not the
           No-matching-clause IllegalArgumentException core-clojure raises"
    (let [exception (with-redefs [client/request (constantly {:status 404 :body "not found"})]
                      (caught #(pix-request/get "1" @project)))]
      (is (some? exception))
      (is (= 404 (:status (ex-data exception))))
      (is (= [{:code "unknownError" :message "not found"}] (:errors (ex-data exception))))))

  (testing "500 is always the same message"
    (let [exception (with-redefs [client/request (constantly {:status 500 :body "<html>"})]
                      (caught #(pix-request/get "1" @project)))]
      (is (some? exception))
      (is (= 500 (:status (ex-data exception))))
      (is (= [{:code "internalServerError" :message "Houston, we have a problem."}]
             (:errors (ex-data exception))))))

  (testing "a transport failure is status 0 carrying the exception's class"
    (let [exception (with-redefs [client/request (thrower (java.net.ConnectException. "Connection refused"))]
                      (caught #(pix-request/get "1" @project)))]
      (is (some? exception))
      (is (= 0 (:status (ex-data exception))))
      (is (= "java.net.ConnectException: Connection refused"
             (:message (first (:errors (ex-data exception)))))))))

(deftest raw-verbs-return-status-and-content
  (testing "no status throws and a JSON body comes back kebab-keyed"
    (with-redefs [client/request (constantly {:status 404 :body "{\"errors\": [{\"code\": \"invalidId\", \"message\": \"nope\"}]}"})]
      (let [response (request/get "pix-request/0" {} @project)]
        (is (= 404 (:status response)))
        (is (= [{:code "invalidId" :message "nope"}] (:errors (:content response)))))))

  (testing "a 200 body is parsed with kebab keys, digits included"
    (with-redefs [client/request (constantly {:status 200 :body "{\"holder\": {\"displayName1\": \"ANTHONY\", \"streetLine1\": \"Av. Paulista, 200\"}}"})]
      (is (= {:holder {:display-name-1 "ANTHONY" :street-line-1 "Av. Paulista, 200"}}
             (:content (request/get "issuing-holder/1" {} @project))))))

  (testing "a body that is not JSON comes back as the raw string"
    (with-redefs [client/request (constantly {:status 502 :body "<html>bad gateway</html>"})]
      (is (= "<html>bad gateway</html>" (:content (request/get "pix-request" {} @project))))))

  (testing "a raw body keeps its nil members, as python's json.dumps does, and
           is camelCased for the wire"
    (let [[sent handler] (recorder [{:status 200 :body "{}"}])]
      (with-redefs [client/request handler]
        (request/post "issuing-holder"
                      {:holders [{:name "Jaime Lannister" :external-id "my-id" :tax-id nil}]}
                      {}
                      @project))
      (is (= "{\"holders\": [{\"name\": \"Jaime Lannister\", \"externalId\": \"my-id\", \"taxId\": null}]}"
             (:body (first @sent)))))))

(deftest write-payloads-drop-nil-members
  (testing "post-multi wraps the entities in the plural envelope key"
    (let [[sent handler] (recorder [{:status 200 :body "{\"requests\": []}"}])]
      (with-redefs [client/request handler]
        (pix-request/create [{:amount 100 :external-id "1" :tags nil}] @project))
      (is (= "{\"requests\": [{\"amount\": 100, \"externalId\": \"1\"}]}"
             (:body (first @sent))))
      (is (= :post (:method (first @sent))))))

  (testing "patch-id sends the api-cast payload, false included"
    (let [[sent handler] (recorder [{:status 200 :body "{\"signer\": {}}"}])]
      (with-redefs [client/request handler]
        (credit-signer/resend-token "1" @project))
      (is (= "{\"isSent\": false}" (:body (first @sent))))
      (is (= :patch (:method (first @sent))))
      (is (= "https://sandbox.api.starkinfra.com/v2/credit-signer/1"
             (:url (first @sent)))))))

(deftest raw-content-comes-back-as-bytes
  (testing "a pdf survives the trip"
    (let [pdf (byte-array [37 80 68 70 45 49 46 52 0 -1 10])
          [sent handler] (recorder [{:status 200 :body pdf}])
          returned (with-redefs [client/request handler]
                     (credit-note/pdf "5656565656565656" @project))]
      (is (bytes? returned))
      (is (= (seq pdf) (seq returned)))
      (is (= :byte-array (:as (first @sent))))
      ;; python passes "/pdf" verbatim, double slash and all, and only
      ;; `:normalize-uri false` keeps HttpClient from collapsing it on the wire
      (is (= "https://sandbox.api.starkinfra.com/v2/credit-note/5656565656565656//pdf"
             (:url (first @sent))))
      (is (false? (:normalize-uri (first @sent)))))))

(deftest api-json-keeps-insertion-order-past-eight-keys
  (let [params (array-map :version 1
                          :created "2022-07-15T02:32:00.147+00:00"
                          :due "2022-07-20T00:00:00.000+00:00"
                          :key-id "+5511989898989"
                          :status "paid"
                          :reconciliation-id "cd65c78aeb6543eaaa0170f68bd741ee"
                          :nominal-amount 100
                          :sender-name "Anthony Edward Stark"
                          :sender-tax-id "01.001.001/0001-01"
                          :receiver-name "Jamie Lannister"
                          :receiver-tax-id "012.345.678-90"
                          :receiver-street-line "Av. Paulista, 200")]
    (testing "twelve keys keep the caller's order, where assoc would have
             switched to a hash map after the eighth"
      (is (= ["version" "created" "due" "keyId" "status" "reconciliationId"
              "nominalAmount" "senderName" "senderTaxId" "receiverName"
              "receiverTaxId" "receiverStreetLine"]
             (vec (keys (json/api-json params))))))

    (testing "response-due dumps them in that same order"
      (is (= (str "{\"version\": 1, \"created\": \"2022-07-15T02:32:00.147+00:00\", "
                  "\"due\": \"2022-07-20T00:00:00.000+00:00\", \"keyId\": \"+5511989898989\", "
                  "\"status\": \"paid\", \"reconciliationId\": \"cd65c78aeb6543eaaa0170f68bd741ee\", "
                  "\"nominalAmount\": 100, \"senderName\": \"Anthony Edward Stark\", "
                  "\"senderTaxId\": \"01.001.001/0001-01\", \"receiverName\": \"Jamie Lannister\", "
                  "\"receiverTaxId\": \"012.345.678-90\", \"receiverStreetLine\": \"Av. Paulista, 200\"}")
             (dynamic-brcode/response-due params))))))

(deftest casing-splits-on-digit-boundaries
  (testing "core-python's rule: a boundary before every non-initial uppercase
           letter or digit"
    (is (= "display-name-1" (casing/camel-to-kebab "displayName1")))
    (is (= "street-line-1" (casing/camel-to-kebab "streetLine1")))
    (is (= "boleto-v-2" (casing/camel-to-kebab "boletoV2")))
    (is (= "end-to-end-id" (casing/camel-to-kebab "endToEndId")))
    (is (= "id" (casing/camel-to-kebab "id"))))

  (testing "and back again"
    (is (= "displayName1" (casing/kebab-to-camel "display-name-1")))
    (is (= "streetLine1" (casing/kebab-to-camel "street-line-1")))
    (is (= "boletoV2" (casing/kebab-to-camel "boleto-v-2")))
    (is (= "endToEndId" (casing/kebab-to-camel :end-to-end-id))))

  (testing "the digit-suffixed keys the docstrings promise round-trip"
    (let [documented (->> (:doc (meta (find-ns 'starkinfra.issuing-embossing-request)))
                          (re-seq #":[a-z][a-z0-9-]*-[0-9]+")
                          distinct
                          sort)]
      (is (= [":display-name-1" ":display-name-2" ":display-name-3"
              ":shipping-street-line-1" ":shipping-street-line-2"]
             (vec documented)))
      (is (every? #(= % (casing/camel-to-kebab (casing/kebab-to-camel %)))
                  (map #(subs % 1) documented)))))

  (testing "recursive casting of a response, nils kept"
    (is (= {:holder {:display-name-1 "A" :tags nil :rules [{:street-line-1 "x"}]}}
           (casing/cast-keys-to-kebab {:holder {:displayName1 "A"
                                                :tags nil
                                                :rules [{:streetLine1 "x"}]}})))))
