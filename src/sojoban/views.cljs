(ns sojoban.views
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [sojoban.utils :refer [val-map]]))

(def image-url
  "Map from cell contents to image URL."
  (val-map #(str "images/" % ".png")
           {#{} "space"
            #{:player} "man"
            #{:block} "bag"
            #{:goal} "goal"
            #{:block :goal} "bag_goal"
            #{:player :goal} "man_goal"
            #{:wall} "block"}))

(defn board-widget [data owner]
  (om/component
    (html [:div#game
           (for [row (:board data)]
             [:div {:className "row"}
              (for [cell row]
                [:span [:img {:src (image-url cell)}]])])])))

(defn level-info-widget [data owner]
  (om/component
    (html [:p#level-info
           [:span#level-name (:level-title data)]
           " by "
           [:span#level-author
            (-> data :level-set :author)]])))

(defn status-message [{:keys [history won]}]
  (let [num-moves (count history)]
    (if (= num-moves 0)
      [:p#status.blank ""]
      (if won
        [:p#status.animated.flash
         (str "Completed in " num-moves " moves! Press N to continue.")]
        [:p#status
         (str "Moves: " num-moves)]))))

(def key-help
  [["arrow keys" "move/push"]
   ["u" "undo"]
   ["r" "restart"]
   ["n" "next level"]
   ["p" "previous level"]])

(def instructions-html
  [:div#instructions
   [:p "Push all blocks onto goals."]
   (apply vector :dl#keys
    (->>
      (for [[k v] key-help]
        [[:dt k]
         [:dd v]])
      (apply concat)))])

(def preloaded-images
  "Some divs to make sure all the images stay loaded, so you don't see lag
  when you walk onto a goal, for instance."
  (for [url (vals image-url)]
    [:div {:style
           {:background (str "url(" url ") no-repeat -9999px -9999px")}}]))

(defn app [data owner]
  (om/component
    (html [:div#app
           [:h1 "Sojoban"]
           [:p "Sokoban, "
            [:a {:href "http://clojure.org"} "with a J in it"] "."]
           (if (:level-set data)
             (om/build level-info-widget data)
             "")
           (if (:board data)
             [:div#game-and-legend
              (om/build board-widget data)
              instructions-html]
             [:p#loading-msg "Loading, please wait."])
           (status-message (om/value data))
           preloaded-images])))
