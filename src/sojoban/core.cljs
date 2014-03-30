(ns sojoban.core
  (:require [om.core :refer [root]]
            [goog.events :as events]
            [goog.events.EventType]
            [goog.history.EventType]
            [secretary.core :as secretary]
            [ajax.core :refer [ajax-request json-format]]
            [sojoban.board :as board]
            [sojoban.read :refer [ascii-level-to-board]]
            [sojoban.views :as views])
  (:import [goog History])
  (:require-macros [secretary.macros :refer [defroute]]))

;;;; Top-Level State/Logic

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

(defn start-level [state-value level-set level-number]
  (let [level-raw (get-in level-set [:levels level-number])
        ;; A board can be either a string, or a map with
        ;; :contents and :title keys.
        [level-ascii title]
        (if (string? level-raw)
          [level-raw (str (:title level-set) " " (inc level-number))]
          ((juxt :contents :title) level-raw))]
    (assoc state-value
           :level-set level-set
           :level-number level-number
           :level-title title
           :board (ascii-level-to-board level-ascii)
           :history []
           :won false)))

(defn undo [state-value]
  (if (> (count (:history state-value)) 0)
    (-> state-value
        (assoc :board (peek (:history state-value)))
        (update-in [:history] pop))
    state-value))

(defn try-seek-level [state-value diff]
  (let [new-level-num (+ diff (:level-number state-value))]
    (if (<= 0 new-level-num
            (-> state-value
                (get-in [:level-set :levels])
                count
                dec)
            (dec (count (get-in state-value [:level-set :levels]))))
      (start-level state-value (:level-set state-value) new-level-num)
      state-value)))

(def state (atom {}))

(def dirs #{:up :down :left :right})

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


;;;; Key Input

(defn no-key-modifiers? [ev]
  (and
    (not (.-shiftKey ev))
    (not (.-ctrlKey ev))
    (not (.-altKey ev))
    (not (.-metaKey ev))))

(def keycode->action
  {38 :up
   40 :down
   37 :left
   39 :right
   82 :restart  ; XXX: not sure why (int \R) doesn't work?
   85 :undo
   78 :next-level
   80 :prev-level})

(defn process-keydown [ev]
  (when-let [action (keycode->action (.-keyCode ev))]
    (when (no-key-modifiers? ev)
      (process-action action)
      (.preventDefault ev))))

(events/listen js/document goog.events.EventType.KEYDOWN
               process-keydown)


;;;; Level Sets and Routing

(def level-sets
  "Initially these all point to nil meaning the level set is not yet loaded.
  While loading, we replace the nil with :loading. Once fully loaded, we
  replace that with the actual object."
  {"yoshio-murase-auto-generated" (atom nil)
   "david-skinner-microban" (atom nil)})

(def history (History.))

(defn complete-level-load [level-set idx]
  (let [level (get-in level-set [:levels (dec idx)])]
    (if level
      (swap! state start-level level-set (dec idx))
      (.replaceToken history "/"))))

;;; Async loading makes this a little harder.
;;; FIXME: This is long, and should be simplified.
(defroute "/:set-name/:idx" {:keys [set-name idx]}
  (let [idx (js/parseInt idx)
        level-set-atom (get level-sets set-name)
        level-set (and level-set-atom @level-set-atom)]
    (cond
      (nil? level-set-atom)
      ; We don't know about this level set, so redirect to the default level.
      (.replaceToken history "/")

      (nil? level-set)
      ;; Not loaded and not in the process of loading. Start load.
      (do
        (reset! level-set-atom :loading)
        (ajax-request
          (str "levels/" set-name ".json")
          :get
          {:format (json-format {:keywords? true})
           :handler
           (fn [[ok result]]
             (if-not ok
               ;; FIXME: Better error handling please. All users with crappy
               ;; connections (i.e. me) will see this regularly.
               (js/alert (str "Oh no! We couldn't load the level set "
                              set-name))

               (let [level-set (assoc result :short-name set-name)]
                 (reset! level-set-atom level-set)
                 (let [level-set-in-route (get (re-find #"^/([a-z0-9-]+)/"
                                                        (.getToken history))
                                               1)]
                   ;; If the level set we just loaded is *still* the one
                   ;; in the URL token, re-dispatch now that we can actually
                   ;; load it. Note that this will work correctly even if the
                   ;; token has changed from /this-level-set/1 to
                   ;; /this-level-set/2, for instance.
                   (when (= level-set-in-route set-name)
                     (secretary/dispatch! (.getToken history)))))))}))

      (= level-set :loading)
      ; Already loading that set, so don't do anything.
      nil

      :else (complete-level-load level-set idx))))

;;; When there's no more specific route, load a default level.
(defroute "/" []
  (.replaceToken history "/yoshio-murase-auto-generated/1"))

(events/listen history goog.history.EventType.NAVIGATE
               (fn [ev] (secretary/dispatch! (.-token ev))))

(.setEnabled history true)

(defn update-url-to-match-level [_ state old new]
  (let [set-name (get-in new [:level-set :short-name])
        new-token (str "/" set-name "/" (inc (:level-number new)))]
    (when (and set-name  ; If no set loaded yet, don't mess with token.
               (not= [(:level-set old) (:level-number old)]
                     [(:level-set new) (:level-number new)])
               (not= new-token (.getToken history)))
      (.setToken history new-token))))

(add-watch state ::update-url-to-match-level update-url-to-match-level)


;;;; Rendering

(root views/app state {:target js/document.body})
