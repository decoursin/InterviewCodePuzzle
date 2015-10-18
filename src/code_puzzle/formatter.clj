(ns code-puzzle.formatter
  (:require [clojure.string :as s]
            [code-puzzle.utils :refer :all]
            [clojure.core.matrix.dataset :as ds]
            [incanter.core :as I]
            [camel-snake-kebab.core :refer [->kebab-case-string]];
            :reload-all))
;TODO: replace data with ds?

(def ^:const cents-or-dollars-regex #"(?i)cents|dollars")
(def ^:const cents-regex #"(?i)cents")
(def ^:const dollars-regex #"(?i)dollars")
(def ^:const dollars-string "dollars")

(defn- columns-to-replace
  "Produces a map where the key is the old column name
   and the value is the new column name. Can take either a function
   or a pair of regexes, where the latter is turned into a function"
  ([column-names f]
   (zipmap column-names (map f column-names)))
  ([data from to]
   (let [cns (filter-column-names data from)]
     (columns-to-replace cns #(s/replace % from to)))))

(defn- rename-columns
  "Renames the column names (i.e. only the headers) of the dataset. Can
   take either a function or a pair of regexes to perform renaming"
  ([data f]
   (let [m (columns-to-replace (ds/column-names data) f)]
     (ds/rename-columns data m)))
  ([data from to]
   (let [m (columns-to-replace data from to)]
     (ds/rename-columns data m))))

(defn- cns-cell-map-to-dataset
  "Incanter API."
  [cns-cell-map cns]
  (I/dataset cns
             (map (apply juxt (map (fn [cn] #(get % cn)) cns)) cns-cell-map)))

(defn- sort-rows
  "Incanter API."
  [data [cn order]]
  (println "CN to sort: " cn)
  (let [cns-cell-map (build-cns-cell-map data)
        sorted-cns-cell-map (sort-by #(get % cn) cns-cell-map)]
    (println "sorted-cns-cell-map: " sorted-cns-cell-map)
    (cns-cell-map-to-dataset sorted-cns-cell-map (ds/column-names data))))
    ;; (clojure.pprint/pprint (sort-by #(get % cn) cns-cell-map))))

(defn format-dataset
  [data sort-order]
  (let [data (-> data
                 (rename-columns s/lower-case)
                 (update-columns cents-or-dollars-regex to-float)
                 (update-columns cents-regex #(/ % 100))
                 (rename-columns cents-regex dollars-string)
                 (rename-columns ->kebab-case-string))]
    (let [not-cents-or-dollars-cns (filter-column-names data #(if (not (re-find cents-or-dollars-regex %)) %))]
      (-> data
          (update-columns not-cents-or-dollars-cns s/lower-case)
          (sort-rows sort-order)))))