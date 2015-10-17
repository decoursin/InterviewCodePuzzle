(ns code-puzzle.core-test
  (:use clojure.test
        code-puzzle.core)
  (:require [clj-http.client :as client]
            [ring.adapter.jetty :as jetty]
            [clojure.data.json :as json]
            [code-puzzle.handler :refer [staples-app]])
  (:import  [org.skyscreamer.jsonassert JSONAssert])) ;TODO: remove some of these


(use-fixtures
  :each
  (fn [f]
    (let [server (jetty/run-jetty #'staples-app {:port 3333 :join? false})]
      (try
        (f)
        (finally
          (.stop server))))))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))

(deftest b-test
   (let [actual (:body (client/get "http://localhost:3333/runatic/report"
                                   {:accepts :json}))
         expected (json/read-str (slurp "expected.session-type-desc.json"))]
    (is (= expected actual))))
