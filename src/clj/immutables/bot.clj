(ns immutables.bot
  (:require [clojure.core.matrix           :as mx]
            [clojure.core.matrix.operators :as mxo]))

(defn- rand-range [start end]
  (let [val-range (range start end)]
    (nth val-range (rand-int (count val-range)))))

(defn- rand-location [start end]
  [(rand-range start end)
   (rand-range start end)])

(defn- update-velocity [bot]
  (let [velocity (mxo/* (mx/normalise (mxo/- (:target bot) (:location bot))) (:speed bot))]
  (assoc-in bot [:velocity] velocity)))

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

(defn- find-target [bot bots]
  (let [enemy (first (scan-for-bots (:location bot) (:sight bot) bots))]
    (if (not (nil? enemy))
      (assoc-in bot [:target] (mxo/- (:location bot) (mxo/- (:location enemy) (:location bot))))
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
    (update-in bot [:energy] (partial - total-damage))))

(defmulti sense (fn [bot world] (:mood bot)))
(defmethod sense :default [bot world]
  (let [bots (:bots world)]
  (-> bot
      (take-damage bots)
      (clear-target)
      (find-target bots))))

(defmulti react (fn [bot] (:mood bot)))
(defmethod react :default [bot]
  (-> bot
      (move-bot)
      (update-velocity)))

(defn update [bot world]
  (-> bot
      (sense world)
      (react)))

