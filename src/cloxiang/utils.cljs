(ns cloxiang.utils
    (:use [cljs.core :only [js->clj]]))

(defn debug [thing & [message]]
    (let [to-print (if message
                    (str message thing)
                    thing)]
        (do (println to-print) thing)))

(defn to-keywords [str-map]
    (->> (for [key (keys str-map)
              value [(str-map key)]]
            [(keyword key)
                (if (map? value)
                    (to-keywords value)
                    value)])
        flatten
        (apply hash-map)))

(defn str->clj [string]
    (->> string
        (.parse js/JSON)
        js->clj
        to-keywords))
