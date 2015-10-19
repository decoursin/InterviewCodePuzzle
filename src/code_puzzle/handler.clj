(ns code-puzzle.handler
  (:require [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]] ;TODO get rid of this?
            [ring.middleware.params :refer :all]
            [net.cgrand.moustache :refer [app]]
            ;; [cheshire.core :as json]
            [clojure.data.json :as json]
            [clojure.string :as s]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds])
  
  (:require [code-puzzle.parser :refer [make-dataset]]
            [code-puzzle.formatter :refer [format-dataset dollars-regex]]
            [code-puzzle.utils :refer :all]
            :reload-all))

(def ^:const merch-filename "resources/external_data.psv")
(def ^:const runa-filename "resources/staples_data.csv")

(defn make-orders-format
  "Turns the two datasets into the expected output format"
  [runa merch]
  (partition-into-hashmap 4 (interleave
                             (cycle [:runa-data :merchant-data])
                             (interleave (build-cns-cell-map runa) (build-cns-cell-map merch)))))

(defn sum-columns
  "Reduces selected columns to the sum of their columns and 
   returns a hashmap of {cn sum-amount}"
  [data column-names]
  (let [data (ds/select-columns data column-names)]
    (apply assoc {} (interleave column-names
                (apply mapv
                       (fn [& xs] (round2 1 (apply + xs)))
                       (I/to-vect data))))))

(defn make-report
  [sort-order]
  (let [merch (format-dataset (make-dataset merch-filename) sort-order)
        runa (format-dataset (make-dataset runa-filename) sort-order)
        runa-summary (sum-columns runa (filter-column-names runa dollars-regex))
        merch-summary (sum-columns merch (filter-column-names merch dollars-regex))
        orders (make-orders-format runa merch)]
    {:summaries {:runa-summary runa-summary
                 :merchant-summary merch-summary}
     :orders orders}))

(defn- parse-sort-order
  [string]
  (let [s (s/split string #"-")
        order (keyword (last s))
        cn (s/join "-" (butlast s))]
    [cn order]))

;TODO: rounding exactly has 1 decimal?
(defn handler [req params]
  (let [sort-order (parse-sort-order (get params "order_by"))]
    ;; (response (json/generate-string (make-report sort-order)))))
    (response (json/write-str (make-report sort-order)))))

(def staples-app (app wrap-reload ["runatic" "report"]
                      #(handler %1 (:query-params (params-request %1)))))

;; (make-report ["unit-price-dollars" "desc"])
