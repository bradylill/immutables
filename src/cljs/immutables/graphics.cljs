(ns immutables.graphics
  (:require [monet.canvas :refer [get-context] :as canvas]
            [jayq.util :refer [log]]))

(def ^:const scale 2.0)

(def ^:const target-radius 3.0)

(def ^:const bot-margin 11.0)

(def ^:const energy-colors ["#FF0000" "#FFFF00" "#00FF00"])

(def ^:const bot-shape [
                         [0.5 5.0]
                         [5.0 -4.0]
                         [2.0 -5.0]
                         [1.5 -3.0]
                         [-1.5 -3.0]
                         [-2.0 -5.0]
                         [-5.0 -4.0]
                         [-0.5 5.0]
                        ])

(defn get-bot-color [name]
  (str "#" (.toUpperCase (.toString (bit-and (bit-or (js/parseInt (str (reverse name)) 36) (bit-not (hash name))) 0xFFFFFF) 16))))

(defn draw-bot-target [ctx x y]
  (canvas/circle ctx {:x x :y y :r target-radius}))

(defn draw-bot-name [ctx name]
  (canvas/text ctx {:text name :x (* (.-width (.measureText ctx name)) -0.5) :y (* bot-margin scale)}))

(defn draw-bot-energy [ctx energy]
  (let [energy-level (min 1.0 (max 0.0 (/ energy 100.0)))]
    (canvas/fill-style ctx (get energy-colors (int (* energy-level 2.0))))
    (canvas/fill-rect ctx {:x (- (* bot-margin scale 0.5)) :y (* bot-margin scale -1.0) :w (* energy-level bot-margin scale) :h scale})))

(defn draw-bot-shape [ctx]
  (canvas/begin-path ctx)
  (doall (map #(canvas/line-to ctx (* scale (first %)) (* scale (second %))) bot-shape))
  (canvas/close-path ctx)
  (canvas/stroke ctx))

(defn draw-bot [ctx delta {name :name [tx ty] :target [x y] :location [vx vy] :velocity energy :energy}]
  (let [bot-color (get-bot-color name)]
    (canvas/fill-style ctx bot-color)
    (draw-bot-target ctx tx ty)
    (canvas/translate ctx (+ x (* vx delta)) (+ y (* vy delta)))
    (draw-bot-name ctx name)
    (draw-bot-energy ctx energy)
    (canvas/stroke-style ctx bot-color)
    (canvas/rotate ctx (Math/atan2 (- vx) vy))
    (draw-bot-shape ctx)
    (. ctx (setTransform 1 0 0 1 0 0))))

(defn draw-map [ctx world delta]
  (canvas/clear-rect ctx {:x 0 :y 0 :w (.-width (.-canvas ctx)) :h (.-height (.-canvas ctx))})
  (canvas/font-style ctx (str (int (* scale 5.5)) "pt sans-serif"))
  (canvas/text-baseline ctx "middle")
  (doall (map (fn [bot] (draw-bot ctx delta bot)) (:bots world))))
