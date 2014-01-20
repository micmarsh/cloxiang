(ns cloxiang.game)

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
          black (game :black)
          to-add (first-missing red black)]
          (assoc game to-add :unconfirmed)))

(defn open [games id]
    (let [exists (contains? games id)
          game (if exists
                    (games id)
                    {:id id})]
        (missing-player game))
