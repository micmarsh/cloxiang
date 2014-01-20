(ns cloxiang.handlers
    (:use [cloxiang.game :only [open missing-player]]))

(def games (atom { }))

(defn get-id [req]
    (apply str
        (-> req
            (aget "url")
            rest)))

(defn registrar [req]
    (let [id (get-id req)]
        (swap! games #(open % id))
        (missing-player @games)))
