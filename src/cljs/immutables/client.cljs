(ns immutables.client
  (:require
   [cljs.core.async :as async
    :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
   [jayq.core :refer [$ append ajax inner css $deferred
                      when done resolve pipe on bind attr
                      offset] :as jq]
   [jayq.util :refer [log]]
   [monet.canvas :refer [get-context] :as canvas])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(def world-default
  { :world { :width 1024.0 :height 1024.0 }
   :objects [{:location [15.4 12.3]
              :name     "dude1"}
             {:location [10.1 10.1]
              :name     "dude2"}]})

(defn handle-click []
  (js/alert "Hello!"))

(def the-canvas ($ :canvas#game))
(def ctx
    (canvas/get-context (.get the-canvas 0) "2d"))

#_(defn draw-something []
  (canvas/add-entity ctx :background
                     (canvas/entity {:x 50 :y 50 :w 100 :h 100}
                                    (fn [s] (update-in s [:x] inc)) ;;update function
                                    (fn [ctx box]
                                      (-> ctx
                                          (canvas/fill-style "#596d31")
                                          (canvas/rect box)))))
  )

(defn draw-object
  [{name :name [x y] :location}]
  (canvas/circle ctx {:x x :y x :r 2})
  (canvas/text ctx {:text name :x (+ 5 x) :y (+ 5 y)}))

(defn draw-map [world]
  (log world)
  (canvas/fill-style ctx "#FFFFFF")
  (doall (map draw-object (:objects world))))

(defn draw-the-map []
  (draw-map world-default))

(defn ajax-map []
  (log "ajax?")
  (ajax "world"
      {:dataType "edn"
       :success  (fn [data] (draw-map data))}))

(def clickable ($ :#clickable))
(jq/bind clickable :click ajax-map)
