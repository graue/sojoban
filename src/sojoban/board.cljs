(ns sojoban.board
  "Game logic.")

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

        ;; Where a block would go if pushed.
        push-loc (map + move-loc dir-vec)

        [start-cell move-cell push-cell]
        (map #(get-in board %) [start-loc move-loc push-loc])]

    (when (and move-cell
               (not (move-cell :wall)))
      (if-not (move-cell :block)
        (move-from board :player start-loc move-loc)

        ;; We have to push a block.
        (when (and push-cell
                   (not (push-cell :wall))
                   (not (push-cell :block)))
          (-> board
              (move-from :block move-loc push-loc)
              (move-from :player start-loc move-loc)))))))

(defn won? [board]
  (->> board
       flatten
       (filter #(and (get % :goal)
                     (not (get % :block))))
       first
       not))
