(ns code-puzzle.formatter
  (:require [clojure.string :as s]
            [code-puzzle.utils :refer :all]
            [clojure.core.matrix.dataset :as ds];
            [camel-snake-kebab.core :refer [->kebab-case-string]];
            :reload-all))

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

(defn format-dataset
  [data]
  (let [data (-> data
                 (rename-columns s/lower-case)
                 (update-columns cents-or-dollars-regex to-float)
                 (update-columns cents-regex #(/ % 100))
                 (rename-columns cents-regex dollars-string)
                 (rename-columns ->kebab-case-string))]
    (let [not-dollars-cns (filter-column-names data #(if (not (re-find cents-or-dollars-regex %)) %))]
      (update-columns data not-dollars-cns s/lower-case))))
