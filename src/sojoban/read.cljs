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

(defn make-rectangular
  "Make the vector of vectors rectangular by padding each row with `fill`,
  up to the length of the longest row."
  [vov fill]
  (let [max-length (apply max (map count vov))]
    (mapv (fn [v]
            (vec (take max-length
                       (concat v (repeat fill)))))
          vov)))

(defn ascii-level-to-board [ascii]
  (as-> ascii x
        (string/split x #"\n")
        (vov-map ascii-to-objset x)
        (make-rectangular x #{})))
