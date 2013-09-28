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

(defn js-now [] (.now (.-performance js/window)))

(defn scaled-time [now last-time]
  (/ (- now last-time) 50.0))

(defn game-loop []
  (let [anim-chan (au/anim-ch active)
        ajax-chan (au/ajax-loop "world" slowth active)]
    (go
     (loop [state {:world {} :timestamp (js-now)}]
       (when @active
         (let [next-state
                 (alt!
                    anim-chan ([val] (do
                                       (graphics/draw-map
                                          ctx (:world state) (scaled-time val (:timestamp state)))
                                       state)) ; no state change
                    ajax-chan ([val] {:world val :timestamp (js-now)}))]
           (recur next-state)))))))

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
