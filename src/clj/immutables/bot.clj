(ns immutables.bot
  (:require [clojure.core.matrix           :as mx]
            [clojure.core.matrix.operators :as mxo]))

;size shouldn't be here...
(def world-width 1680)
(def world-height 1050)

(defn- rand-range [start end]
  (let [val-range (range start end)]
    (nth val-range (rand-int (count val-range)))))

(defn- rand-location [start end]
  [(rand-range start end)
   (rand-range start end)])

(defn- update-velocity [bot]
  (let [velocity (mxo/* (mx/normalise (mxo/- (:target bot) (:location bot))) (:speed bot))]
  (assoc-in bot [:velocity] velocity)))

(defn- keep-inbounds [location]
  (let [lx (first location)
        ly (second location)]
    [(mod lx world-width)
     (mod ly world-height)]))

(defn- move-bot [bot]
  (let [location (:location bot)
        velocity (:velocity bot)]
    (assoc-in bot [:location] (mxo/+ location velocity))))

(defn- scan-for-bots [location radius bots]
  (filter (fn [bot] 
            (let [scanned-bot (mxo/- (:location bot) location)]
              (and (> radius (Math/abs (first scanned-bot)))
                   (> radius (Math/abs (second scanned-bot)))
                   (not (= (:location bot) location)))))
          bots))

(defmulti tactic (fn [bot enemy] (:tactic bot)))

(defmethod tactic :chase [bot enemy] 
  (mxo/+ (:location enemy) (rand-location -10 10)))

(defmethod tactic :escape [bot enemy]
  (mxo/- (:location bot) (mxo/- (:location enemy) (:location bot))))

(defmethod tactic :default [bot enemy]
  (:location bot))

(defn- find-target [bot bots]
  (let [enemy (first (scan-for-bots (:location bot) (:sight bot) bots))]
    (if (not (nil? enemy))
      (assoc-in bot [:target] (-> (tactic bot enemy)
                                  (keep-inbounds)))
      (if (nil? (:target bot))
        (assoc-in bot [:target] (mxo/+ (:location bot) (rand-location -40 40)))
        bot))))

(defn- clear-target [bot]
  (let [delta    (mxo/- (:target bot) (:location bot))
        dx       (Math/abs (first delta))
        dy       (Math/abs (second delta))]
    (if (and (< dx 2) (< dy 2))
      (assoc-in bot [:target] nil)
      bot)))

(defn- take-damage [bot bots]
  (let [close-bots   (scan-for-bots (:location bot) (:attack-radius bot) bots)
        total-damage (reduce (fn [total bot] (+ total (:damage bot))) 0 close-bots)]
    (update-in bot [:energy] - total-damage)))

(defn sense [bot world]
  (let [bots (:bots world)]
  (-> bot
      (take-damage bots)
      (clear-target)
      (find-target bots))))

(defn react [bot]
  (-> bot
      (move-bot)
      (update-velocity)))

(defn update [bot world]
  (-> bot
      (sense world)
      (react)))

