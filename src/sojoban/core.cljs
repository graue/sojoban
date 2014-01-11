(ns sojoban.core
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer [html] :include-macros true]
            [goog.events :as events]
            [goog.events.EventType]
            [sojoban.levels.yoshio :refer [yoshio-levels]]))

(defn image-url
  "Image URL for a square"
  [cell]
  (-> cell
      {#{} "space"
       #{:player} "man"
       #{:block} "bag"
       #{:goal} "goal"
       #{:block :goal} "bag_goal"
       #{:player :goal} "man_goal"
       #{:wall} "block"}
      (as-> x (str "images/p_" x ".gif"))))

(defn find-player
  "Return [row col] coordinates of the player on the board."
  [board]
  (loop [row-idx 0, col-idx 0]
    (when-let [row (get board row-idx)]
      (if-let [cell (get row col-idx)]
        (if (cell :player)
          [row-idx col-idx]
          (recur row-idx (inc col-idx)))
        (recur (inc row-idx) 0)))))

(def ^{:doc "Row/column vector for a direction."} dir->vec
  {:up [-1 0]
   :down [1 0]
   :left [0 -1]
   :right [0 1]})

(defn move-from
  "Move the object from from-loc to to-loc in the board."
  [board obj from-loc to-loc]
  (-> board
    (update-in to-loc conj obj)
    (update-in from-loc disj obj)))

(defn try-move
  "Try moving the player in the direction given. Return the updated board
  or nil."
  [board dir]
  (let [start-loc (find-player board)
        dir-vec (dir->vec dir)
        move-loc (map + start-loc dir-vec)

        ; Where a block would go if pushed.
        push-loc (map + move-loc dir-vec)

        [start-cell move-cell push-cell]
        (map #(get-in board %) [start-loc move-loc push-loc])]

    (when (and move-cell
               (not (move-cell :wall)))
      (if-not (move-cell :block)
        (move-from board :player start-loc move-loc)

        ; we have to push a block
        (when (and push-cell
                   (not (push-cell :wall))
                   (not (push-cell :block)))
          (-> board
              (move-from :block move-loc push-loc)
              (move-from :player start-loc move-loc)))))))

(defn board-won? [board]
  (->> board
       flatten
       (filter #(and (get % :goal)
                     (not (get % :block))))
       first
       not))

(defn process-move [{:keys [board won num-moves]
                     :or {won false, num-moves 0}
                     :as state}
                    dir]
  (if won
    state  ; Don't accept moves once the level is done.
    (if-let [new-board (try-move board dir)]
      (assoc state :board new-board
                   :num-moves (inc num-moves)
                   :won (board-won? new-board))

      ; Move failed; return unchanged state.
      state)))

(def keycode->dir
  {38 :up
   40 :down
   37 :left
   39 :right})

(def state (atom {:board (yoshio-levels 0)}))

(defn process-keydown [ev]
  (when-not (:won @state)
    (when-let [dir (keycode->dir (.-keyCode ev))]
      (swap! state process-move dir)
      (when (:won @state)
        (js/alert (str "You win, in " (:num-moves @state) " moves!"))))))

(defn sojoban-widget [data owner]
  (om/component
    (html [:div#game
           (for [row (:board data)]
             [:div {:className "row"}
              (for [cell row]
                [:td [:img {:src (image-url cell)}]])])])))

(events/listen js/document goog.events.EventType.KEYDOWN
               process-keydown)

(om/root state sojoban-widget js/document.body)
