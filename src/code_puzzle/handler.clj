(ns code-puzzle.handler
  (:require [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]] ;TODO get rid of this?
            [net.cgrand.moustache :refer [app]]
            [clojure.data.json :as json]
            [code-puzzle.parser :refer [make-dataset]]
            [code-puzzle.formatter :refer [format-dataset dollars-regex]]
            [code-puzzle.utils :refer :all]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds]
            :reload-all))

(def ^:const merch-filename "resources/external_data.psv")
(def ^:const runa-filename "resources/staples_data.csv")

(defn make-orders
  "Turns the two datasets into the expected output format"
  [runa merch]
  (defn make-order
    "Turns the dataset into it's individually expected output format"
    [data]
    (let [rows (I/to-vect data)
          cns (ds/column-names data)]
      (for [row rows]
        (apply assoc {} (interleave cns row)))))
  (partition-into-hashmap 4 (interleave
                             (cycle [:runa-data :merchant-data])
                             (interleave (make-order runa) (make-order merch)))))

(defn sum-columns
  "Reduce and return the money columns to the sum of their columns"
  [data column-names]
  (defn sum-column
    [coll]
    (reduce + coll))
  (let [data (ds/select-columns data column-names)]
    (apply assoc {} (flatten (for [[key column] (I/to-map data)]
                               [(get column-names key) (round2 1 (sum-column column))])))))

(defn make-report
  [sort-order]
  (let [merch (format-dataset (make-dataset merch-filename))
        runa (format-dataset (make-dataset runa-filename))
        cns-to-sum (filter-column-names runa dollars-regex)
        runa-summary (sum-columns runa cns-to-sum)
        merch-summary (sum-columns merch cns-to-sum)
        orders (make-orders runa merch)]
    {:summaries {:runa-summary runa-summary
                 :merchant-summary merch-summary}
     :orders orders}))

;TODO: sortorder
;TODO: rounding exactly has 1 decimal?
(defn handler [req] (response (json/write-str (make-report "1"))))
(def staples-app (app wrap-reload ["runatic" "report"] #'handler))
