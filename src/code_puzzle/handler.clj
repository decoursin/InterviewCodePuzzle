(ns code-puzzle.handler
  (:use net.cgrand.moustache)
  (:require [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [clojure.string :as s]
            ;; [clojure.core ]
            [code-puzzle.parser :refer [parse]]
            [code-puzzle.utils :refer [filter-columns-names-by-regex update-columns]]
            [incanter.core :as I]
            [clojure.core.matrix]
            [clojure.core.matrix.dataset :as ds]; :refer [column-names update-column]]
            [camel-snake-kebab.core :refer [->kebab-case-string]]; :refer [column-names update-column]]
            :reload-all))

(defn handler [req] (response (str "how about this work?" req)))
(def my-app (app ["runatic" "report"] #'handler))

(def ^:const psv-filename "/home/nick/Working/clojure/InterviewCodePuzzle/resources/external_data.psv")
(def ^:const csv-filename "/home/nick/Working/clojure/InterviewCodePuzzle/resources/staples_data.csv")
(def ^:const cents-regex #"Cents")
(def ^:const dollars-regex #"dollars")
(def ^:const dollars-string "Dollars")
(def ^:const lisp-case)

(defn replace-column-names
  ([column-names f]
   (zipmap column-names
           (map f column-names)))
  ([column-names from to]
   (let [column-names (filter-columns-names-by-regex column-names from)]
     (replace-column-names column-names #(s/replace % from to)))))

;; TODO: clean this up
(defn rename-cols
  ([data f]
   (let [column-names (ds/column-names data)
         m (replace-column-names column-names f)]
     (ds/rename-columns data m)))
  ([data from to]
   (let [column-names (ds/column-names data)
         m (replace-column-names column-names from to)]
     (ds/rename-columns data m))))

(defn convert-cents-to-dollars
  [data re]
  (letfn [(div-by-100 [num] (/ num 100))]
    (let [column-names (filter-columns-names-by-regex (ds/column-names data) re)]
      (update-columns data column-names div-by-100))))

(defn controller
  [filename]
  (let [data (-> (parse filename)
                 (convert-cents-to-dollars cents-regex)
                 (rename-cols cents-regex dollars-string)
                 (rename-cols ->kebab-case-string))]
                 ;; (rename-cols s/lower-case))]
       ;summary
       ;ordered

    data))

(defn sum-columns
  "reduce integer column-names to the sum
   of there columns"
  [data column-names]
  (defn sum-column
    [coll]
    (reduce + coll))
  (let [data (ds/select-columns data column-names)]
    (apply assoc {} (flatten (for [[key column] (I/to-map data)]
      [(get column-names key) (sum-column column)])))))

(defn partition-into-hashmap
  [size coll]
  (map #(apply hash-map %) (partition size coll)))

(defn make-orders
  [csv psv]
  (defn make-order
    [data]
    (let [rows (I/to-vect data)
          column-names (ds/column-names data)]
      (for [row rows]
        (apply assoc {} (interleave column-names row)))))
  (partition-into-hashmap 4 (interleave
                             (cycle [:runa-data :merchant-data])
                             (interleave (make-order csv) (make-order psv)))))
    ;; (partition-into-hashmap (I/ncol csv) (interleave (helper csv) (helper psv))))
    ;; (partition-into-hashmap (interleave (helper csv) (helper psv))))

(let [runa (controller csv-filename)
      merch (controller psv-filename)
      summary-columns (filter-columns-names-by-regex (ds/column-names runa) dollars-regex)
      runa-summary (sum-columns runa summary-columns)
      merch-summary (sum-columns merch summary-columns)
      orders (make-orders runa merch)
      m {:summaries {:runa-summary runa-summary
                     :merchant-summar merch-summary}
         :orders orders}]
  m)
