(defproject code-puzzle "0.1.0-SNAPSHOT"
  :description "Solution to Runa Code Puzzle"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [compojure "1.4.0"]
                 [net.cgrand/moustache "1.2.0-alpha2"]; TODO: remove
                 [ring-server "0.3.1"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 [kindlychung/incanter-core "1.9.1-SNAPSHOT"]
                 ;testing
                 [clj-http "2.0.0"]
                 [org.skyscreamer/jsonassert "1.2.3"]
                 [expectations "2.0.9"]]
  
  :plugins [[lein-ring "0.8.12"]
            [lein-expectations "0.0.7"]]
  :ring {:handler code-puzzle.handler/staples-app}
  :profiles {:dev {:dependencies [[alembic "0.3.2"]
                                  [org.clojure/tools.namespace "0.2.11"]]}
             :ring {:open-browser? false, :stacktraces? true, :auto-reload? true}})
