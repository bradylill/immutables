(ns immutables.client
  (:require
   [cljs.core.async :as async
    :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
   [jayq.core :refer [$ append ajax inner css $deferred
                      when done resolve pipe on bind attr
                      offset] :as jq]
   [jayq.util :refer [log]]
   [monet.canvas :refer [get-context] :as canvas]
   [immutables.graphics :as graphics])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(defn handle-click []
  (js/alert "Hello!"))

(def the-canvas ($ :canvas#game))
(def ctx
    (canvas/get-context (.get the-canvas 0) "2d"))

(defn ajax-map [thenfn]
  (log "ajax?")
  (ajax "world"
      {:dataType "edn"
       :success  (fn [data] (do (graphics/draw-map ctx data) (thenfn)))}))

(defn after-timeout [time thenfn]
  (js/setTimeout thenfn time))

(def active (atom true))

(def slowth (atom 1000))

(defn game-loop []
  (when @active
    (ajax-map (fn [] (after-timeout @slowth #(game-loop))))))

(defn start-game []
  (reset! active true)
  (game-loop))

(defn stop-game []
  (reset! active false))

(def clickable ($ :#clickable))
(jq/bind ($ :#start) :click start-game)
(jq/bind ($ :#stop) :click stop-game)
