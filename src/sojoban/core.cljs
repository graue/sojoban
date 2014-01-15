(ns sojoban.core
  (:require [om.core :refer [root]]
            [goog.events :as events]
            [goog.events.EventType]
            [goog.history.EventType]
            [secretary.core :as secretary]
            [sojoban.levels.yoshio :refer [yoshio-levels]]
            [sojoban.board :as board]
            [sojoban.views :as views])
  (:import [goog History])
  (:require-macros [secretary.macros :refer [defroute]]))

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

(root state views/app js/document.body)
