(ns code-puzzle.core
  (:use net.cgrand.moustache)
  (:use [ring.adapter.jetty :only [run-jetty]])
  (:require [ring.util.response :refer (response)]
            [ring.middleware.reload :refer (wrap-reload)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

;; set up a server that serves my-app
(declare my-app)
(declare server)

(defn restart []
  (do
    (.stop server)
    (refresh)))

(defonce server (run-jetty #'my-app {:port 8194 :join? false}))

;(def server (doto (Thread. #(run-jetty #'my-app {:port 8080})) .start))
;; #'my-app is a trick to allow to redefine my-app without restarting the server thread.

(defn handler [req] (response (str "will this work?" req)))

;; now define my-app
(def my-app (app ["runatic" "report"] #'handler))
