(ns cloxiang.handlers
    (:use [cloxiang.game :only [open missing-player get-game]]
          [cloxiang.utils :only [debug]]))

(def games (atom { }))

(defn get-id [req]
    (apply str
        (-> req
            (aget "url")
            rest)))

(defn registrar [req]
    (let [id (get-id req)
          prev-game (get-game @games id)]
        (swap! games #(open % id))
        (-> prev-game
            missing-player
            name)))
