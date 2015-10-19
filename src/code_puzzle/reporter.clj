(ns code-puzzle.reporter
  (:require [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds]
            [code-puzzle.parser :refer [make-dataset]]
            [code-puzzle.formatter :refer [format-dataset dollars-regex]]
            [code-puzzle.utils :refer :all]))

(def ^:const merch-filename "resources/external_data.psv")
(def ^:const runa-filename "resources/staples_data.csv")

(defn- make-orders-format
  "Turns the two datasets into the expected output format"
  [runa merch]
  (partition-into-hashmap 4 (interleave
                             (cycle [:runa-data :merchant-data])
                             (interleave (build-cns-cell-map runa) (build-cns-cell-map merch)))))

(defn- sum-columns
  "Reduces selected columns to the sum of their columns and 
   returns a collection of hashmaps likes [{cn sum-amount}, {cn sum-amount}, ..]"
  [data column-names]
  (let [data (ds/select-columns data column-names)]
    (apply assoc {} (interleave column-names
                (apply mapv
                       (fn [& xs] (round2 1 (apply + xs)))
                       (I/to-vect data))))))

(defn make-report
  "Generates the report as described in the README.md"
  [sort-order]
  (let [merch (format-dataset (make-dataset merch-filename) sort-order)
        runa (format-dataset (make-dataset runa-filename) sort-order)
        runa-summary (sum-columns runa (filter-column-names runa dollars-regex))
        merch-summary (sum-columns merch (filter-column-names merch dollars-regex))
        orders (make-orders-format runa merch)]
    {:summaries {:runa-summary runa-summary
                 :merchant-summary merch-summary}
     :orders orders}))
