(defproject sojoban "0.1.0-SNAPSHOT"
  :description "Sokoban with a J in it"
  :url "http://toxicsli.me/sojoban/"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2138"]
                 [om "0.1.5"]
                 [sablono "0.1.7"]
                 [secretary "0.4.0"]
                 [cljs-ajax "0.2.3"]]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "sojoban"
              :source-paths ["src"]
              :compiler {
                :output-to "sojoban.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}
             {:id "optimized"
              :source-paths ["src"]
              :compiler {
                :output-to "sojoban-opt.js"
                :optimizations :advanced
                :pretty-print false
                :preamble ["react/react.min.js"]
                :externs ["react/externs/react.js"]}}]})
