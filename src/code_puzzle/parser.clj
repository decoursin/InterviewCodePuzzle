(ns code-puzzle.parser
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [split]]
            [code-puzzle.utils :refer [filter-columns-names-by-regex update-columns]]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds]
            :reload-all))

(def ^:const cents-or-dollars #"Cents|Dollars")

; Utilities
; Some of these are only used once
(defn- getExtension
  [filename]
  (.substring filename (inc (.lastIndexOf filename "."))))

(defn- to-integer [s]
  (when (not (nil? s))
    (Float/parseFloat s)))

(defn- str-to-regex-form
  [string]
  (java.util.regex.Pattern/compile string java.util.regex.Pattern/LITERAL))
(str-to-regex-form "Dollars")

(defn- get-delimeter
  [filename]
  (let [ch (first (getExtension filename))]
    (cond
      (= ch \p) "|"
      (= ch \c) ","
      :else (throw (Exception. "filetype is not valid, expects .csv or .psv")))))


(defn- read-in-data
  [filename]
  (with-open [rdr (reader filename)]
    (let [delim (str-to-regex-form (get-delimeter filename))
          col-names (split (.readLine rdr) delim)]
      (I/dataset col-names
               (doall (for [line (line-seq rdr)]
                        (split line delim)))))))

(defn parse
  [filename]
  (let [data (read-in-data filename)
        column-names (-> (ds/column-names data) (filter-columns-names-by-regex cents-or-dollars))]
    (update-columns data column-names to-integer)))
    ;; (reduce #(ds/update-column %1 %2 to-integer)
    ;;         data
    ;;         (filter-columns-names-by-regex col-names cents-or-dollars))))

;; TODO: remove filename
;; (def filename "/home/nick/Working/clojure/InterviewCodePuzzle/resources/external_data.psv")
;; (parse filename)
