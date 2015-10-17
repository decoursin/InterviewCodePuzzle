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
      (= ch \p) "\\|"
      (= ch \c) ","
      :else (throw (Exception. "filetype is not valid, expects .csv or .psv")))))

;TODO: Test if slurp works instead
; then, try to decouple opening the file from making the dataset
(defn make-dataset
  "Open the file and make into a dataset"
  [filename]
  (with-open [rdr (reader filename)]
    (let [delim (re-pattern (get-delimeter filename))
          cns (split (.readLine rdr) delim)]
      (I/dataset cns
               (doall (for [line (line-seq rdr)]
                        (split line delim)))))))
