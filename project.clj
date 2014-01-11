(defproject sojoban "0.1.0-SNAPSHOT"
  :description "Sokoban with a J in it"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2127"]
                 [om "0.1.5"]
                 [sablono "0.1.7"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "sojoban"
              :source-paths ["src"]
              :compiler {
                :output-to "sojoban.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
