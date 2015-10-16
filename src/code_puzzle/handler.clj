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
                 (rename-cols ->kebab-case-string)
                 (rename-cols s/lower-case))]
       ;summary
       ;ordered

    data))

;; TODO: transpose instead?
(defn sum-column
  [data column-names]
  (let [data (I/sel data :cols column-names)]
    (apply assoc {} (flatten (for [column-name column-names
                                    :let [column (get (I/to-map data) column-name)]]
                                [column-name (reduce + column)])))))
  ;; (println "to matrxi: " (I/to-matrix data))
  ;;   (for [column (I/to-matrix data)]
  ;;     ;; (reduce + column))))
  ;;     (do (println "col: " column)
  ;;       (reduce + column)))))

;; (defn make-orders
;;   [data]
;;   (apply merge
;;          (for [[key values] (seq (I/to-map data))]
;;            (interleave (cycle [key]) values))))

(defn partition-into-hashmap
  [size coll]
  (map #(apply hash-map %) (partition size coll)))

(defn make-orders
  [csv psv]
    (defn helper
        [data]
        (let [rows (I/to-vect data)
                column-names (ds/column-names data)]
            (for [row rows]
            (apply assoc {} (interleave column-names row)))))
    ;; (println (helper csv)))
    (interleave (helper csv) (helper psv)))
    ;; (partition-into-hashmap (I/ncol csv) (interleave (helper csv) (helper psv))))
    ;; (partition-into-hashmap (interleave (helper csv) (helper psv))))

;;   (I/to-map data)))
;;   {:runa-data csv, :merchant-data psv})
(def ^:const dollars-regex #"dollars")
(let [csv (controller csv-filename)
      psv (controller psv-filename)
      summary-columns (filter-columns-names-by-regex (ds/column-names csv) dollars-regex)
      csv-summary (sum-column csv summary-columns)
      psv-summary (sum-column psv summary-columns)]
      ;; orders (interleave (make-orders csv) (make-orders psv))]
      ;; orders (make-orders csv psv)]
      ;; orders (partition-into-hashmap 4 (interleave
      ;;                         (cycle [:runa-data :merchant-data])
      ;;                         (make-orders csv psv)))]
      ;; orders (merge {:runa-data csv :merchant-data psv})
      ;; m (interleave {}(make-orders csv) (make-orders psv))]
      ;; m {:summaries {:runa-summary csv-summary
      ;;                 :merchant-summary psv-summary}
      ;;     :order (interleave (make-orders csv) (make-orders psv))}]
      ;; m [{:runa-data csv :merchant-data psv}]
      ;; m (merge {summ})]
  ;; (println "runa: " csv)
  ;; (println "merch: " psv)
  ;; (println "sum: " orders)
  ;; (clojure.pprint/pprint orders)
  (println "sum cols: " summary-columns))

;1) perform 
