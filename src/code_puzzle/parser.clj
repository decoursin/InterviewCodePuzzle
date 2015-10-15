(ns code-puzzle.parser
  (:require [clojure.java.io :refer [reader]]
            [clojure.string :refer [split]]
            [clojure.core :refer [->]]
            [incanter.io]
            [incanter.datasets]
            [incanter.core :refer [transform-col dataset]]))
;            [code-puzzle.util :refer [with-open-seq]]))

(def filename "/home/nick/Working/clojure/InterviewCodePuzzle/resources/external_data.psv")
(def integer-columns #"Cents|Dollars")

(defn getExtension
  [filename]
  (.substring filename (inc (.lastIndexOf filename "."))))

(defn get-delimeter
  [filename]
  (let [ch (first (getExtension filename))]
    (cond
      (= ch \p) "|"
      (= ch \c) ","
      :else (throw (Exception. "filetype is not valid, expects .csv or .psv")))))


(defn to-regex
  [string]
  (java.util.regex.Pattern/compile string java.util.regex.Pattern/LITERAL))

(defn parse
  [filename]
  (with-open [rdr (reader filename)]
    (let [delim (to-regex (get-delimeter filename))
          headers (split (.readLine rdr) delim)]
      [(dataset (map keyword headers)
               (doall (for [line (line-seq rdr)]
                        (split line delim))))
       headers])))


(defn parse-number [s]
  (when (not (nil? s))
    (Integer/parseInt s)))

(defn ni
  [[data headers]]
  [(reduce #(if (re-find integer-columns %2) (transform-col %1 %2 parse-number) %1) data headers)
   headers])

(def data (parse filename))

(let [[data headers] (ni data)]
  (println "NICK: " data headers))
;; (def data (->> filename parse n))

;; (println "second data: " data)

;; (defn select-keys-regex
;;   [m re]
;;   () (comp vals select-keys))

;; (defn select-headers-by-regex
;;   [headers re]
;;   (into [] (filter #(re-find re %) headers)))

;(select-headers-by-regex (keys (first %)) integer-columns)
;(map #(apply parse-number (select-values % )) data)
;(select-headers-by-regex (keys (first data)) integer-columns)

