(ns cloxiang.game
    (:use [cloxiang.utils :only [debug]]))

(defn- first-missing [red black]
        (cond (and (not black) red)
                :black
              (not red)
                :red))

(defn missing-player [game]
    (let [red (game :red)
          black (game :black)]
        (first-missing red black)))

(defn full? [game]
    (boolean
        (not
          (or (= :unconfirmed (game :red))
              (= :unconfirmed (game :black))))))

(defn- add-missing-player [game]
    (assoc game
        (missing-player game)
          :unconfirmed))

(def Board (js/require "./xiangqi/movable_position"))

(defn get-game [games id]
    (get games id {:id id :board (new Board)}))

(defn open [games id]
    (let [update-games (partial assoc games id)]
        (-> games
            (get-game id)
            add-missing-player
            update-games)))
