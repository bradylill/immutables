(ns immutables.graphics
  (:require [monet.canvas :refer [get-context] :as canvas]
            [jayq.util :refer [log]]))


(def object-vector-shape [[0.5 10.5] [10.5 -10.5] [-10.5 -10.5]])

(defn draw-closed-polygon [ctx points]
  (canvas/begin-path ctx)
  (doall (map #(canvas/line-to ctx (first %) (second %)) points))
  (canvas/close-path ctx)
  (canvas/stroke ctx)
  )

(defn draw-object [ctx {name :name [x y] :location}]
  (canvas/save ctx)
  (canvas/translate ctx x y)
  (canvas/stroke-style ctx "#FF0000")
  (draw-closed-polygon ctx object-vector-shape)
  (canvas/fill-style ctx "#FFFFFF")
  (canvas/text ctx {:text name :x 0 :y 0})
  (canvas/restore ctx)
  )

(defn draw-map [ctx world]
  (canvas/save ctx)
  (canvas/clear-rect ctx {:x 0 :y 0 :w 800 :h 600})
  (doall (map (fn [object] (draw-object ctx object)) (:objects world)))
  (canvas/restore ctx))