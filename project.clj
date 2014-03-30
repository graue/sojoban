(defproject sojoban "0.1.0-SNAPSHOT"
  :description "Sokoban with a J in it"
  :url "http://toxicsli.me/sojoban/"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2173"]
                 [om "0.5.3"]
                 [sablono "0.2.14"]
                 [secretary "0.4.0"]
                 [cljs-ajax "0.2.3"]]

  :plugins [[lein-cljsbuild "1.0.2"]]

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
