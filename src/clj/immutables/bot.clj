(ns immutables.bot
  (:require [clojure.core.matrix           :as mx]
            [clojure.core.matrix.operators :as mxo]))

(defn- rand-range [start end]
  (let [val-range (range start end)]
    (nth val-range (rand-int (count val-range)))))

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

(defn- target-enemy [bot bots]
  (let [enemy (first (scan-for-bots (:location bot) (:sight bot) bots))]
    (if (not (nil? enemy))
      (assoc-in bot [:target] (mxo/- (:location bot) (mxo/- (:location enemy) (:location bot))))
      (assoc-in bot [:target] [200 200]))))

(defmulti sense (fn [bot world] (:mood bot)))
(defmethod sense :default [bot world]
  (-> bot
      (target-enemy (:bots world))))

(defmulti react (fn [bot] (:mood bot)))
(defmethod react :default [bot]
  (-> bot
      (move-bot)
      (update-velocity)))

(defn update [bot world]
  (-> bot
      (sense world)
      (react)))

