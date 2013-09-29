(ns immutables.graphics
  (:require [monet.canvas :refer [get-context] :as canvas]
            [jayq.util :refer [log]]))

(def scale 1.5)
(def object-vector-shape [[0.8 5.0] [5.0 (- 4.0)] [2.0 (- 5.0)] [1.5 (- 3.0)]
                          [(- 1.5) (- 3.0)] [(- 2.0) (- 5.0)] [(- 5.0) (- 4.0)] [(- 0.8) 5.0]])

(defn draw-closed-polygon [ctx points]
  (canvas/begin-path ctx)
  (doall (map #(canvas/line-to ctx (* scale (first %)) (* scale (second %))) points))
  (canvas/close-path ctx)
  (canvas/stroke ctx)
  )

(defn draw-energy [ctx energy]
  (let [energy-normalized (min 1.0 (max 0 (/ energy 100.0)))]
    (canvas/fill-style ctx (get ["#FF0000" "#FFFF00" "#00FF00"] (int (* energy-normalized 2))))
    (canvas/fill-rect ctx {:x (- (* 7 scale)) :y (- (* 11 scale)) :w (* energy-normalized (* 14 scale)) :h (* 1 scale)})
    ))

(defn name-to-color [name]
  (.toUpperCase (.toString (bit-and (bit-or (js/parseInt (str (reverse name)) 36) (bit-not (hash name))) 0xFFFFFF) 16))
  )

(defn draw-object [ctx delta {name :name [tx ty] :target [x y] :location [vx vy] :velocity energy :energy}]
  (canvas/save ctx)
  (canvas/fill-style ctx (str "#" (name-to-color name)))
  (canvas/circle ctx {:x tx :y ty :r 2})
  (canvas/translate ctx (+ x (* vx delta)) (+ y (* vy delta)))
  (canvas/text ctx {:text name :x (* (.-width (.measureText ctx name)) (- 0.5)) :y (* 14 scale)})
  (draw-energy ctx energy)
  (canvas/stroke-style ctx (str "#" (name-to-color name)))
  (canvas/rotate ctx (Math/atan2 (- vx) vy))
  (draw-closed-polygon ctx object-vector-shape)
  (canvas/restore ctx)
  )

(defn draw-map [ctx world delta]
  (canvas/clear-rect ctx {:x 0 :y 0 :w (.-width (.-canvas ctx)) :h (.-height (.-canvas ctx))})
  (canvas/save ctx)
  (doall (map (fn [object] (draw-object ctx delta object)) (:bots world)))
  (canvas/restore ctx))
