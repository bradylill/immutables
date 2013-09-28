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

(def slowth (atom 50))

(def last-world (atom nil))
(def last-timestamp (atom 0))

(defn game-loop []
  (let [anim-chan (au/anim-ch active)
        ajax-chan (au/ajax-loop "world" slowth active)]
    (go
     (loop []
       (when @active
         (let [[value ch] (alts! [anim-chan ajax-chan])]
           (if (= ch anim-chan)
             (do
               (log "anim frame time:" value)
               (graphics/draw-map ctx @last-world (/ (- value @last-timestamp) 50.0))
               )
             (do
               (log "ajax result!")
               (reset! last-world value)
               (reset! last-timestamp (.now (.-performance js/window)))
               )))
         (recur))))))

(defn start-game []
  (reset! active true)
  (game-loop))

(defn stop-game []
  (reset! active false))

(defn init-world []
  (ajax "init"
        {:dataType "edn"
         :success identity}))

(jq/bind ($ :#start) :click start-game)
(jq/bind ($ :#stop) :click stop-game)
(jq/bind ($ :#init) :click init-world)
