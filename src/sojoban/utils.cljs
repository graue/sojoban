(ns sojoban.utils)

(defn val-map
  "Map f over hashmap m's values. Should be in the dang core."
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

