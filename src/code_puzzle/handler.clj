(ns code-puzzle.handler
  (:require [ring.util.response :as ring :refer [response]]
            [ring.middleware.params :as p :refer [params-request]]
            [net.cgrand.moustache :refer [app]]
            [clojure.data.json :as json]
            [clojure.string :as s]
            [code-puzzle.utils :refer :all]
            [code-puzzle.reporter :refer [make-report]]))

(def ^:const merch-filename "resources/external_data.psv")
(def ^:const runa-filename "resources/staples_data.csv")

(defn- parse-sort-order
  "Parses the string into 1) the column name to sort by and 2) the order
   to sort by, which is either :asc or :desc. Example input is: session-type-desc
   where session-type is the column name and desc (output :desc) is the order"
  [string]
  (let [s (s/split string #"-")
        order (keyword (last s))
        cn (s/join "-" (butlast s))]
    [cn order]))

(defn handler [req params]
  (let [sort-order (parse-sort-order (get params "order_by"))]
    (ring/response (json/write-str (make-report sort-order)))))

(def staples-app (app ["runatic" "report"]
                      #(handler %1 (:query-params (p/params-request %1)))))
