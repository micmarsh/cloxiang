(ns cloxiang.game
    (:use [cloxiang.utils :only [debug]]))

;API
;
;open should: take games map and id, return a new map w/ id and and game w/ either red or both
;then u can just use missing player to query for things on the top level pass back what U need

(defn- first-missing [red black]
        (cond (and (not black) red)
                :black
              (not red)
                :red))

(defn missing-player [game]
    (let [red (game :red)
          black (game :black)]
        (first-missing red black)))

(defn- add-missing-player [game]
    (assoc game
        (missing-player game)
          :unconfirmed))

(defn get-game [games id]
    (let [exists (contains? games id)]
        (if exists (games id) {:id id})))

(defn open [games id]
    (let [update-games (partial assoc games id)]
        (-> games
            (get-game id)
            add-missing-player
            update-games)))
