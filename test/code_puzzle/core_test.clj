(ns code-puzzle.core-test
  (:use clojure.test
        code-puzzle.core)
  (:require [clj-http.client :as client]
            [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [clojure.pprint :refer [pprint]]
            [code-puzzle.handler :refer [staples-app]])
  (:import  [org.skyscreamer.jsonassert JSONAssert])) ;TODO: remove some of these


(def ^:const port 3334)

(use-fixtures
  :each
  (fn [f]
    (let [server (jetty/run-jetty #'staples-app {:port port :join? false})]
      ;; (println "server: " (str server))
      (try
        (f)
        (finally
          (.stop server))))))

(deftest session-type-desc
  (let [order-by "session-type-desc"
        url (str "http://localhost:" port "/runatic/report?order_by=" order-by)
        actual (json/read-str (:body (client/get url {:accepts :json})))
        expected (json/read-str (slurp (str "expected." order-by ".json")))]
    (is (= expected actual))))

(deftest order-id-asc
  (let [order-by "order-id-asc"
        url (str "http://localhost:" port "/runatic/report?order_by=" order-by)
        actual (json/read-str (:body (client/get url {:accepts :json})))
        expected (json/read-str (slurp (str "expected." order-by ".json")))]
    (is (= expected actual))))

(deftest unit-price-dollars-asc
  (let [order-by "unit-price-dollars-asc"
        url (str "http://localhost:" port "/runatic/report?order_by=" order-by)
        actual (json/read-str (:body (client/get url {:accepts :json})))
        expected (json/read-str (slurp (str "expected." order-by ".json")))]
    (is (= expected actual))))
