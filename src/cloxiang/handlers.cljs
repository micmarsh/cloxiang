(ns cloxiang.handlers
    (:use [cloxiang.game :only [open missing-player]]))

(def games (atom { }))

(defn get-id [req]
    (apply str
        (-> req
            (aget "url")
            rest)))

(defn pret [thing]
    (do (println thing) thing))

(defn registrar [req]
    (let [id (get-id req)]
        (swap! games #(open % id))
        (-> @games
            pret
            (get id)
            pret
            missing-player
            name)))
