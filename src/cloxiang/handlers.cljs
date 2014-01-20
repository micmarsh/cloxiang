(ns cloxiang.handlers
    (:use [cloxiang.game :only
            [open missing-player get-game full?]]
          [cloxiang.utils :only [debug str->clj]])
    (:require [schema.core :as s]))

(def MoveMessage
    {:gameId s/Str ; five chars long
     :player s/Str ; "black", "red", "none"
     :message {
            :type s/Str ; only "move" right now
            :from s/Str; dd?,dd? format, may be same as js is expecting
            :to s/Str ; same^
        }})

(def games (atom { }))

(defn- get-url [req]
    (let [to-str (partial apply str)]
        (-> req
            (aget "url")
            rest
            to-str)))

(defn registrar [req]
    (let [id (get-url req)
          prev-game (get-game @games id)]
        (swap! games #(open % id))
        (-> prev-game
            missing-player
            (or :none)
            name)))

(defn validate [schema object]
    (try (s/validate schema object)
        (catch js/Error e
            (debug object "Incorrect Input: "))))

(defn make-move! [board from to]
    (.makeMove board from to))

(defn- handle-move [object]
    (let [{:keys [gameId player message]} object
          {:keys [type from to]} message
          game (get-game @games gameId)
          {:keys [board red black]} game]

          (println (= type "move"))
          (println (full? game))
          (println (.canMove board from to))

          (if  (and (= type "move")
                    (full? game)
                    (.canMove board from to))
                (do
                    (make-move! board from to)
                    (.send red object)
                    (.send black object)))))

; TODO here: need a check for move legality, if legal then send
; move to both red and black if true. Imperative as all heck, but much cleaner

(defn move [message socket]
    (->> message
        str->clj
        (validate MoveMessage)
        debug
        handle-move))

(defn player? [player]
    (or (= player "red")
        (= player "black")))

(defn connect [socket]
    (let [request (aget socket "upgradeReq")
          info (get-url request)
          [m id player] (clojure.string/split info #"/")
          game (get-game @games id)
          with-player (if (player? player)
                        (assoc game (keyword player) socket)
                        game)]
        (println with-player)
        (swap! games #(assoc % id with-player))
        "connected"))
