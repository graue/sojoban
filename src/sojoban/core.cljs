(ns sojoban.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer [html] :include-macros true]
            [sojoban.levels.yoshio :refer [yoshio-levels]]))

(defn image-url
  "Image URL for a square"
  [cell]
  (-> cell
      {#{:space} "space"
       #{:player} "man"
       #{:block} "bag"
       #{:goal} "goal"
       #{:block :goal} "bag_goal"
       #{:player :goal} "man_goal"
       #{:wall} "block"}
      (as-> x (str "http://www.ne.jp/asahi/ai/yoshio/sokoban/image/p_"
                   x ".gif"))))

(defn sojoban-widget [data owner]
  (om/component
    (html [:div#game
           (for [row (:board data)]
             [:div {:className "row"}
              (for [cell row]
                [:td [:img {:src (image-url cell)}]])])])))

(om/root {:board (yoshio-levels 0)} sojoban-widget js/document.body)
