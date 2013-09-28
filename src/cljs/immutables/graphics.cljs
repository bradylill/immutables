(ns immutables.graphics
  (:require [monet.canvas :refer [get-context] :as canvas]
            [jayq.util :refer [log]]))


(def object-vector-shape [[0.0 10.0] [10.0 (- 10.0)] [(- 10.0) (- 10.0)]])

(defn draw-closed-polygon [ctx points]
  (canvas/begin-path ctx)
  (doall (map #(canvas/line-to ctx (first %) (second %)) points))
  (canvas/close-path ctx)
  (canvas/stroke ctx)
  )

(defn draw-object [ctx delta {name :name [x y] :location [vx vy] :velocity}]
  (canvas/save ctx)
  (canvas/translate ctx (+ x (* vx delta)) (+ y (* vy delta)))
  (canvas/fill-style ctx "#FFFFFF")
  (canvas/text ctx {:text name :x (* (.-width (.measureText ctx name)) (- 0.5)) :y 20})
  (canvas/rotate ctx (Math/atan2 (- vx) vy))
  (canvas/stroke-style ctx "#FF0000")
  (draw-closed-polygon ctx object-vector-shape)
  (canvas/restore ctx)
  )

(defn draw-map [ctx world delta]
  (canvas/clear-rect ctx {:x 0 :y 0 :w 800 :h 600})
  (canvas/save ctx)
  (doall (map (fn [object] (draw-object ctx delta object)) (:objects world)))
  (canvas/restore ctx))