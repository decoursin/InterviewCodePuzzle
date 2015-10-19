(ns code-puzzle.parser
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [split]]
            [code-puzzle.utils :refer :all]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds]))

(defn- get-extension
  [filename]
  (.substring filename (inc (.lastIndexOf filename "."))))

(defn- get-delimeter
  "Determine the delimeter by the file extension of the filename"
  [filename]
  (let [ch (first (get-extension filename))]
    (cond
      (= ch \p) "\\|"
      (= ch \c) ","
      :else (throw (Exception. "filetype is not valid, expects .csv or .psv")))))

(defn make-dataset
  "Open the file and make it into and return a dataset"
  [filename]
  (with-open [rdr (reader filename)]
    (let [delim (re-pattern (get-delimeter filename))
          cns (split (.readLine rdr) delim)]
      (I/dataset cns
               (doall (for [line (line-seq rdr)]
                        (split line delim)))))))
