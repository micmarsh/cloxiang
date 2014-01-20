(ns cloxiang.handlers
    (:use [cloxiang.game :only [open missing-player get-game]]
          [cloxiang.utils :only [debug str->clj]])
    (:require [schema.core :as s]))

(def games (atom { }))

(defn get-id [req]
    (let [to-str (partial apply str)]
        (-> req
            (aget "url")
            rest
            to-str)))

(defn registrar [req]
    (let [id (get-id req)
          prev-game (get-game @games id)]
        (swap! games #(open % id))
        (-> prev-game
            missing-player
            (or :none)
            name)))

(def MoveMessage
    {:gameId s/Str ; five chars long
     :player s/Str ; "black", "red", "none"
     :message {
            :type s/Str ; only "move" right now
            :from s/Str; dd?,dd? format, may be same as js is expecting
            :to s/Str ; same^
        }})

(defn move [message socket]
    (debug message "yo message: ")
    (->> message
        str->clj
        (s/validate MoveMessage)))
