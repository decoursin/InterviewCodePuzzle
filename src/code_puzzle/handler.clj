(ns code-puzzle.handler
  (:use net.cgrand.moustache)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.string :as s]
            [code-puzzle.parser :refer [read-in-data]]
            [code-puzzle.utils :refer :all]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds]; :refer [column-names update-column]]
            [camel-snake-kebab.core :refer [->kebab-case-string]]; :refer [column-names update-column]]
            :reload-all))

(defn handler [req] (response (str "how about this work?" req)))
(def my-app (app ["runatic" "report"] #'handler))

;TODO: filenames to merch
;TODO: filenames relative paths
(def ^:const psv-filename "/home/nick/Working/clojure/InterviewCodePuzzle/resources/external_data.psv")
(def ^:const csv-filename "/home/nick/Working/clojure/InterviewCodePuzzle/resources/staples_data.csv")
;; TODO: Upper case?
(def ^:const cents-or-dollars-regex #"(?i)cents|dollars")
(def ^:const cents-regex #"(?i)cents")
(def ^:const dollars-regex #"(?i)dollars")
(def ^:const dollars-string "dollars")

(defn columns-to-replace
  "Produces a map where the key is the old column name
   and the value is the new column name. Can take either a function
   or a pair of regexes, where the latter is turned into a function"
  ([column-names f]
   (zipmap column-names (map f column-names)))
  ([data from to]
   (let [cns (filter-column-names data from)]
     (columns-to-replace cns #(s/replace % from to)))))

(defn rename-columns
  "Renames the column names (i.e. only the headers) of the dataset. Can
   take either a function or a pair of regexes to perform renaming"
  ([data f]
   (let [m (columns-to-replace (ds/column-names data) f)]
     (ds/rename-columns data m)))
  ([data from to]
   (let [m (columns-to-replace data from to)]
     (ds/rename-columns data m))))

;; (defn convert-cents-to-dollars
;;   "Converts columns named cents replaced with dollars"
;;   [data re]
;;   (letfn [(div-by-100 [num] (/ num 100))]
;;     (let [cns (filter-column-names data re)]
;;       (update-columns data cns div-by-100))))

(defn controller
  [filename]
  (let [data (-> (read-in-data filename)
                 (rename-columns s/lower-case)
                 (update-columns cents-or-dollars-regex to-float)
                 (rename-columns cents-regex dollars-string)
                 (update-columns cents-regex #(/ % 100))
                 (rename-columns ->kebab-case-string))]
    (let [not-dollars-cns (filter-column-names data #(if (not (re-find cents-or-dollars-regex %)) %))]
                 (update-columns data not-dollars-cns s/lower-case))))

(defn sum-columns
  "Reduce and return the money columns to the sum of their columns"
  [data column-names]
  (defn sum-column
    [coll]
    (reduce + coll))
  (let [data (ds/select-columns data column-names)]
    (apply assoc {} (flatten (for [[key column] (I/to-map data)]
                               [(get column-names key) (sum-column column)])))))

(defn make-orders
  "Turns the two datasets into the expected output format"
  [csv psv]
  (defn make-order
    "Turns the dataset into it's individually expected output format"
    [data]
    (let [rows (I/to-vect data)
          cns (ds/column-names data)]
      (for [row rows]
        (apply assoc {} (interleave cns row)))))
  (partition-into-hashmap 4 (interleave
                             (cycle [:runa-data :merchant-data])
                             (interleave (make-order csv) (make-order psv)))))

(let [runa (controller csv-filename)
      merch (controller psv-filename)
      summary-columns (filter-column-names runa dollars-regex)
      runa-summary (sum-columns runa summary-columns)
      merch-summary (sum-columns merch summary-columns)
      orders (make-orders runa merch)
      m {:summaries {:runa-summary runa-summary
                     :merchant-summar merch-summary}
         :orders orders}]
  m)
