(ns cloxiang.core
    (:use [overnight.server :only [initialize]]
          [overnight.sockets :only [with-sockets]]
          [cloxiang.handlers :only [registrar move! connect!]]))

(defn js-keys [obj]
    (.keys js/Object obj))
(defn js-vals [obj]
    (for [key (js-keys obj)]
        (aget obj key)))
(def zip (partial map #(identity [%1 %2])))

(defn view-js [obj]
  (zip (js-keys obj)
       (js-vals obj)))

(def MOVE_ROUTE "/move/\\w{5}/(red|black)")

(defn -main [& args] (->
    (initialize
        [:get "/\\w{5}" registrar]
        {:static "public"
         :port 8008})

    (with-sockets
        [:open MOVE_ROUTE connect!]
        [:message MOVE_ROUTE move!])
))

(set! *main-cli-fn* -main)

