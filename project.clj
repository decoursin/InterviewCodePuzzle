(defproject code-puzzle "0.1.0-SNAPSHOT"
  :description "Solution to Runa Code Puzzle"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring "1.4.0"]
                 [ring-server "0.3.1"]
                 [net.cgrand/moustache "1.2.0-alpha2"]
                 [kindlychung/incanter-core "1.9.1-SNAPSHOT"]
                 [camel-snake-kebab "0.3.2"]
                 [org.clojure/data.json "0.2.6"]
                 ;;for Testing
                 [clj-http "2.0.0"]
                 [org.skyscreamer/jsonassert "1.2.3"]
                 [expectations "2.0.9"]]
  :plugins [[lein-ring "0.8.12"]]
  :ring {:handler code-puzzle.handler/staples-app
         :open-browser? false, :stacktraces? false, :auto-reload? false}
  :dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]})
