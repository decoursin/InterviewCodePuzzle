(ns code-puzzle.reporter
  :require '[code-puzzle.parser :refer (parse-data)])

(defn report
  [sort-order]
  (let [{summary, data} (parse-data)]
    (println data)))
