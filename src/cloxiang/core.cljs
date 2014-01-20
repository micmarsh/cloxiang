(ns cloxiang.core
    (:use [overnight.server :only [initialize]]))

(defn js-keys [obj]
    (.keys js/Object obj))
(defn js-vals [obj]
    (for [key (js-keys obj)]
        (aget obj key)))
(def zip (partial map #(identity [%1 %2])))

(defn view-js [obj]
  (zip (js-keys obj)
       (js-vals obj)))

(defn -main [& args]
    (initialize
        [:get "/" #(identity "yo")]))

(set! *main-cli-fn* -main)

