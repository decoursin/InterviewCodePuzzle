(ns code-puzzle.handler
  (:require [ring.util.response :refer [response]]
            [ring.middleware.reload :refer [wrap-reload]] ;TODO get rid of this?
            [ring.middleware.params :refer :all] ;TODO get rid of this?
            [net.cgrand.moustache :refer [app]]
            [clojure.data.json :as json]
            [clojure.string :as s]
            [incanter.core :as I]
            [clojure.core.matrix.dataset :as ds])
  (:require [code-puzzle.parser :refer [make-dataset]]
            [code-puzzle.formatter :refer [format-dataset dollars-regex]]
            [code-puzzle.utils :refer :all]
            :reload-all))
            ;; [compojure.core :refer :all]
            ;; [compojure.handler :as :all] :TODO get rid
            ;; [compojure.route :as :all]
            ;; :reload-all))

(def ^:const merch-filename "resources/external_data.psv")
(def ^:const runa-filename "resources/staples_data.csv")

(defn make-orders-format
  "Turns the two datasets into the expected output format"
  [runa merch]
  (partition-into-hashmap 4 (interleave
                             (cycle [:runa-data :merchant-data])
                             (interleave (build-cns-cell-map runa) (build-cns-cell-map merch)))))

(defn sum-columns
  "Reduce and return the money columns to the sum of their columns"
  [data column-names]
  (defn sum-column
    [coll]
    (reduce + coll))
  (let [data (ds/select-columns data column-names)]
    (println "data in sum columns: " data)
    (apply assoc {} (flatten (for [[key column] (I/to-map data)]
                               (do (println "key &&&& column: " key column)
                                   (println "column-names" column-names)
                                   (println "(get col-names key)" (get column-names key))
                                   [key (round2 1 (sum-column column))]))))))

(defn make-report
  [sort-order]
  (let [merch (format-dataset (make-dataset merch-filename) sort-order)
        runa (format-dataset (make-dataset runa-filename) sort-order)
        a (println "merch: " merch)
        b (println "runa: " runa)
        runa-summary (sum-columns runa (filter-column-names runa dollars-regex))
        d (println "runa-summary: " runa-summary)
        merch-summary (sum-columns merch (filter-column-names merch dollars-regex))
        e (println "merch-summary: " merch-summary)
        orders (make-orders-format runa merch)
        f (println "orders: " orders)
        g (println "orders class: " (class orders))
        m {:summaries {:runa-summary runa-summary
                       :merchant-summary merch-summary}
           :orders orders}]
    (println "m: " m)
    m))
    ;; {:summaries {:runa-summary runa-summary
    ;;              :merchant-summary merch-summary}
    ;;  :orders orders}))

(defn- parse-sort-order
  [string]
  (let [s (s/split string #"-")
        order (last s)
        cn (s/join "-" (butlast s))]
    [cn order]))

;TODO: rounding exactly has 1 decimal?
(defn handler [req params]
  (let [[cn order] (parse-sort-order (get params "order_by"))]
    (println "Param: " params)
    (println "cn: " cn)
    (response (json/write-str (make-report [cn compare])))))
    ;; (println (make-report [cn compare]))))
      ;; (response (json/write-str (println (class (make-report [cn compare])))))))
      ;; (response (json/write-str (clojure.pprint/pprint (make-report [cn compare]))))))
    ;; (let [opposite #(* -1 %)
    ;;       order-function compare]
    ;;         ;; (order-function (case cn
    ;;         ;;                   "session-type" (if (= order "desc") (comp opposite compare) compare)

    ;;         ;;                   "order-id"     compare
    ;;         ;;                   "unit-price-dollars" compare))]
    ;;   (response (json/write-str (make-report [cn order-function]))))))

(def staples-app (app wrap-reload ["runatic" "report"]
                      #(handler %1 (:query-params (params-request %1)))))

;; (make-report ["unit-price-dollars" "desc"])
