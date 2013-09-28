(ns immutables.client
  (:require
   [cljs.core.async :as async
    :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
   [jayq.core :refer [$ append ajax inner css $deferred
                      when done resolve pipe on bind attr
                      offset] :as jq]
   [jayq.util :refer [log]]
   [monet.canvas :refer [get-context] :as canvas]
   [immutables.graphics :as graphics]
   [immutables.async-utils :as au])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(defn handle-click []
  (js/alert "Hello!"))

(def the-canvas ($ :canvas#game))
(def ctx
    (canvas/get-context (.get the-canvas 0) "2d"))

(def active (atom true))

(def slowth (atom 100))

(defn animloop [chan timestamp]
  (.requestAnimationFrame js/window (partial animloop chan))
  (put! chan timestamp))

(defn a-game-loop []
  (let [anim-chan (chan)
        ajax-chan (au/ajax-loop "world" slowth active)]
    (animloop anim-chan 0)
    (go
     (loop []
       (when @active
         (let [[value ch] (alts! [anim-chan ajax-chan])]
           (if (=  ch
                   anim-chan) (do
                         (log "anim frame time:" value))
              (do
                         (log "ajax result!")
                         (graphics/draw-map ctx value))))
         (recur))))))

(defn start-game []
  (reset! active true)
  (a-game-loop))

(defn stop-game []
  (reset! active false))

(def clickable ($ :#clickable))
(jq/bind ($ :#start) :click start-game)
(jq/bind ($ :#stop) :click stop-game)
