(defproject code-puzzle "0.1.0-SNAPSHOT"
  :description "Solution to Runa Code Puzzle"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.4.0"]
                 [net.cgrand/moustache "1.2.0-alpha2"]
                 [compojure "1.1.6"]
                 [ring-server "0.3.1"]
                 [incanter "1.5.6"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler code-puzzle.handler/my-app
         :init code-puzzle.handler/init
         :destroy code-puzzle.handler/destroy}
  :profiles {:dev {:dependencies [[alembic "0.3.2"]
                                  [org.clojure/tools.namespace "0.2.11"]]}
             :ring {:open-browser? false, :stacktraces? true, :auto-reload? true}})
