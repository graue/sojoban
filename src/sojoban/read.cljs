(ns sojoban.read
  "Utilities for reading in levels."
  (:require [clojure.string :as string]))

(defn vov-map
  "Passed a two-layer-deep collection, map f over each element, returning
  a vector of vectors."
  [f vov]
  (mapv #(mapv f %) vov))

(def ascii-to-objset
  {\space #{}
   \# #{:wall}
   \@ #{:player}
   \. #{:goal}
   \$ #{:block}
   \* #{:block :goal}
   \+ #{:player :goal}})

(defn ascii-level-to-board [ascii]
  (vov-map ascii-to-objset
           (string/split ascii #"\n\s*")))
