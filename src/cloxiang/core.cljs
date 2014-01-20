(ns cloxiang.core
    (:use [overnight.server :only [initialize]]
          [overnight.sockets :only [with-sockets]]
          [cloxiang.handlers :only [registrar move]]))

(defn js-keys [obj]
    (.keys js/Object obj))
(defn js-vals [obj]
    (for [key (js-keys obj)]
        (aget obj key)))
(def zip (partial map #(identity [%1 %2])))

(defn view-js [obj]
  (zip (js-keys obj)
       (js-vals obj)))

(defn -main [& args] (->
    (initialize
        [:get "/" #(println (view-js %))]
        [:get "/\\w{5}" registrar])

    (with-sockets
        [:open "/move" #(println "open!!!")]
        [:message "/move" move])
))

(set! *main-cli-fn* -main)

