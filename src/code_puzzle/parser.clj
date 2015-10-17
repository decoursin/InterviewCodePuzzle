(ns code-puzzle.parser
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [split]]
            [code-puzzle.utils :refer :all]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds]
            :reload-all))

; Utilities
; Left here for convenience
(defn- get-extension
  [filename]
  (.substring filename (inc (.lastIndexOf filename "."))))

(defn- get-delimeter
  "Discovert the delimeter by the file extension"
  [filename]
  (let [ch (first (get-extension filename))]
    (cond
      (= ch \p) "|"
      (= ch \c) ","
      :else (throw (Exception. "filetype is not valid, expects .csv or .psv")))))

(defn- string-to-regex
  [string]
  (java.util.regex.Pattern/compile string java.util.regex.Pattern/LITERAL))


(defn read-in-data
  "Read the file into a dataset"
  [filename]
  (with-open [rdr (reader filename)]
    (let [delim (string-to-regex (get-delimeter filename))
          cns (split (.readLine rdr) delim)]
      (println "delim: " delim "cns: " cns)
      (I/dataset cns
               (doall (for [line (line-seq rdr)]
                        (split line delim)))))))
