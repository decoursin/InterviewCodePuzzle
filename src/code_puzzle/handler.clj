(ns code-puzzle.handler
  (:use net.cgrand.moustache)
  (:require [ring.util.response :refer (response)]
            [ring.middleware.reload :refer (wrap-reload)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defn handler [req] (response (str "how about this work?" req)))

;; now define my-app
(def my-app (app ["runatic" "report"] #'handler))
