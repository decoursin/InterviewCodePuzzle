(ns code-puzzle.utils
  (:require [clojure.core.matrix.dataset :as ds]
            [incanter.core :as I]))

(defmulti filter-column-names
  "Filters all of the column names"
  (fn [a b]
    [(cond
       ;; (= (type a) clojure.core.matrix.impl.dataset.Dataset) :dataset
       (= (type a) clojure.lang.PersistentVector) :column-names
       :else :dataset)
       ;; :else (throw (Exception. "incorrect input first arg (a)")))
     (cond
       (= (fn? b) true) :function
       (= (type b) java.util.regex.Pattern) :regex
       :else (throw (Exception. "incorrect input second arg b = " b)))]))

(defmethod filter-column-names [:dataset :regex]
  ([data re]
   (filter-column-names (ds/column-names data) #(when (re-find re %) %))))

(defmethod filter-column-names [:dataset :function]
  ([data f]
   (filter-column-names (ds/column-names data) f)))

(defmethod filter-column-names [:column-names :regex]
  ([cns re]
   (filter-column-names cns #(if (re-find re %) %))))

(defmethod filter-column-names [:column-names :function]
  ([cns f]
   (into [] (keep f cns))))

(defn update-columns
  "Updates the columns of a given dataset. If a regex is
   passed, select all columns that match the regex pattern, then
   update only those columns by the given function"
  ([data f]
   (update-columns data (ds/column-names data) f))
  ([data cns-or-regex f]
   (if (= (type cns-or-regex) java.util.regex.Pattern)
     (update-columns data (filter-column-names data cns-or-regex) f)
     (reduce #(ds/update-column %1 %2 f) data cns-or-regex))))

(defn build-cns-cell-map
  "For each cell of each column, produce a hashmap {column-name cell}.
   Voila, the name of the function reads, build {column-names cell} hashmap.
   (Parenthical note, this is the reverse to cns-cell-map-to-dataset)"
  [data]
  (let [rows (I/to-vect data)
        cns (ds/column-names data)]
    (for [row rows]
      (apply assoc {} (interleave cns row)))))

(defn partition-into-hashmap
  "Partitions a collection ordered k,v,k,v... by size into a hashmap.
   For example, (partition-into-hashmap 4 hashmap) -> ({k v, k v} {k v, k v}...)"
  [size coll]
  (map #(apply hash-map %) (partition size coll)))

(defn round2
  "Round a double to the given precision (number of significant digits)"
  [precision d]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(defn to-float
  "Parses a string into a float"
  [s]
  (when (not (nil? s))
    (Float/parseFloat s)))

(defn to-integer
  "Parses a string into a float"
  [s]
  (when (not (nil? s))
    (Integer/parseInt s)))
