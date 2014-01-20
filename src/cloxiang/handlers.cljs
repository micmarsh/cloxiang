(ns cloxiang.handlers
    (:use [cloxiang.game :only [open missing-player]]))

(def games (atom { }))

(defn get-id [req]
    (apply str
        (-> req
            (aget "url")
            rest)))

(defn debug [thing & [message]]
    (let [to-print (if message
                    (str message thing)
                    thing)])
    (do (println to-print) thing))

(defn registrar [req]
    (let [id (get-id req)]
        (swap! games #(open % id))
        (-> @games
            (debug "all the games evar: ")
            (get id)
            (debug "the game u found: ")
            missing-player
            name)))
