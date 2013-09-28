(ns immutables.async-utils
  (:require    [jayq.core :refer [ajax]]
               [jayq.util :refer [log]]
               [cljs.core.async :refer [chan close! >! <! timeout put! sliding-buffer]])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]))

(defn ajax-get-1 [url]
  (let [ch (chan 1)]
    (ajax url
          {:dataType "edn"
           :success (fn [data]
                      (go (>! ch data)
                          (close! ch)))})
    ch))

(defn ajax-loop [url delayref activeref]
  (let [ch (chan)]
    (go
     (loop []
       (when @activeref
         (>! ch (<! (ajax-get-1 url)))
         (<! (timeout @delayref))
         (recur))))
    ch))

(defn animloop [chan activeref timestamp]
  (when @activeref
    (.requestAnimationFrame js/window (partial animloop chan activeref)))
  (put! chan timestamp))

(defn anim-ch [activeref]
  (let [ch (chan (sliding-buffer 1))]
    (animloop ch activeref 0)
    ch))
