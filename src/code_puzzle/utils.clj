(ns code-puzzle.utils
  (:require [clojure.core.matrix.dataset :as ds]))

;;TODO: (map-reduce data column-names & fs)
;;TODO: return map with :column-name value for each row then value
;;TODO: (for :and)

;; TODO: Use multimethod or protocol accept both column-names and data
(defn filter-columns-names-by-regex
  [column-names regex]
  (into [] (keep #(if (re-find regex %) %) column-names)))

(defn update-columns
  ([data f]
   (update-columns data (ds/column-names data) f))
  ([data column-names f]
    (reduce #(ds/update-column %1 %2 f) data column-names)))
