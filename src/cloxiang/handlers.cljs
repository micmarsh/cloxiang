(ns cloxiang.handlers
    (:use [cloxiang.game :only
            [open missing-player get-game full?]]
          [cloxiang.utils :only [debug str->clj]]
          [cljs.core :only [clj->js]])
    (:require [schema.core :as s]))

(def MoveMessage {
            :type s/Str ; only "move" right now
            :from s/Str; dd?,dd? format, may be same as js is expecting
            :to s/Str ; same^
        })

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

(defn- handle-move! [gameId player object]
    (let [{:keys [type from to]} object
          game (get-game @games gameId)
          {:keys [board red black]} game
          message (->> object clj->js (.stringify js/JSON))]
          (if  (and (= type "move")
                    (full? game)
                    (.canMove board from to))
                (do
                    (make-move! board from to)
                    (.send red message)
                    (.send black message)))))

(defn- player? [player]
    (or (= player "red")
        (= player "black")))

(defn- finalize-metadata [[type id player]]
     {:type type
      :id id
      :player player})
(defn- get-metadata [socket]
    (-> socket
        (aget "upgradeReq")
        get-url
        (clojure.string/split #"/")
        finalize-metadata))

(defn move! [message socket]
    (let [{:keys [id player]} (get-metadata socket)
          finish! (partial handle-move! id player)]
        (->> message
            str->clj
            (validate MoveMessage)
            finish!)))

(defn connect! [socket]
    (let [{:keys [id player]} (get-metadata socket)
          game (get-game @games id)
          with-player (if (player? player)
                        (assoc game (keyword player) socket)
                        game)]
        (swap! games #(assoc % id with-player))
        "connected"))
