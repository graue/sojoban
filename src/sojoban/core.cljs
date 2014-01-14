(ns sojoban.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer [html] :include-macros true]
            [goog.events :as events]
            [goog.events.EventType]
            [goog.history.EventType]
            [secretary.core :as secretary]
            [sojoban.levels.yoshio :refer [yoshio-levels]]
            [sojoban.utils :refer [val-map]]
            [sojoban.board :as board])
  (:import [goog History])
  (:require-macros [secretary.macros :refer [defroute]]))

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

(defn process-move [{:keys [board won history]
                     :or {won false}
                     :as state}
                    dir]
  (if won
    state  ; Don't accept moves once the level is done.
    (if-let [new-board (board/try-move board dir)]
      (assoc state :board new-board
                   :won (board/won? new-board)
                   :history (conj history board))

      ; Move failed; return unchanged state.
      state)))

(def keycode->action
  {38 :up
   40 :down
   37 :left
   39 :right
   82 :restart  ; XXX: not sure why (int \R) doesn't work?
   85 :undo
   78 :next-level
   80 :prev-level})

(def dirs #{:up :down :left :right})

(defn start-level [state-value level-set level-number]
  (assoc state-value
         :level-set level-set
         :level-number level-number
         :board (level-set level-number)
         :history []
         :won false))

(defn undo [state-value]
  (if (> (count (:history state-value)) 0)
    (-> state-value
        (assoc :board (peek (:history state-value)))
        (update-in [:history] pop))
    state-value))

(defn try-seek-level [state-value diff]
  (let [new-level-num (+ diff (:level-number state-value))]
    (if (<= 0 new-level-num (dec (count (:level-set state-value))))
      (start-level state-value (:level-set state-value) new-level-num)
      state-value)))

(def init-state
  (start-level {} yoshio-levels 0))

(def state (atom init-state))

(defn process-action [action]
  (cond
    (and (dirs action)
         (not (:won @state)))
    (swap! state process-move action)

    (= action :restart)
    (swap! state #(start-level % (:level-set %) (:level-number %)))

    (and (= action :undo)
         (not (:won @state)))
    (swap! state undo)

    (= action :prev-level)
    (swap! state try-seek-level -1)

    (= action :next-level)
    (swap! state try-seek-level 1)))

(defn no-key-modifiers? [ev]
  (and
    (not (.-shiftKey ev))
    (not (.-ctrlKey ev))
    (not (.-altKey ev))
    (not (.-metaKey ev))))

(defn process-keydown [ev]
  (when-let [action (keycode->action (.-keyCode ev))]
    (when (no-key-modifiers? ev)
      (process-action action)
      (.preventDefault ev))))

(defn board-widget [data owner]
  (om/component
    (html [:div#game
           (for [row (:board data)]
             [:div {:className "row"}
              (for [cell row]
                [:td [:img {:src (image-url cell)}]])])])))

(defn level-info-widget [data owner]
  (om/component
    (html [:p#level-info
           [:span#level-name
            (str (-> data :level-set om/value meta :title)
                 " " (-> data :level-number inc))]
           " by "
           [:span#level-author
            (-> data :level-set om/value meta :author)]])))

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

(defn app-widget [data owner]
  (om/component
    (html [:div#app
           [:h1 "Sojoban"]
           [:p "Sokoban, "
            [:a {:href "http://clojure.org"} "with a J in it"] "."]
           (om/build level-info-widget data)
           (if (:board data)
             [:div#game-and-legend
              (om/build board-widget data)
              instructions-html]
             "")
           (status-message (om/value data))
           preloaded-images])))

(events/listen js/document goog.events.EventType.KEYDOWN
               process-keydown)

(def level-sets
  {"yoshio" yoshio-levels})

(def history (History.))

(defroute "/:set-name/:idx" {:keys [set-name idx]}
  (let [level-set (get level-sets set-name)
        idx (js/parseInt idx)
        level (get level-set (dec idx))]
    (if level
      (swap! state start-level level-set (dec idx))
      (.replaceToken history "/"))))

(defroute "/" []
  (.replaceToken history "/yoshio/1"))

(events/listen history goog.history.EventType.NAVIGATE
               (fn [ev] (secretary/dispatch! (.-token ev))))

(.setEnabled history true)

(defn update-url-to-match-level [_ state old new]
  (let [new-token (str "/" "yoshio" "/" (inc (:level-number new)))]
    (when (and (not= [(:level-set old) (:level-number old)]
                     [(:level-set new) (:level-number new)])
               (not= new-token (.getToken history)))
      (.setToken history new-token))))

(add-watch state ::update-url-to-match-level update-url-to-match-level)

(om/root state app-widget js/document.body)
