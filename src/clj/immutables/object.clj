(ns immutables.object
  (:require [clojure.core.matrix           :as mx]
            [clojure.core.matrix.operators :as mxo]))

(defn- rand-range [start end]
  (let [val-range (range start end)]
    (nth val-range (rand-int (count val-range)))))

(defn- update-target [object]
  (assoc-in object [:target] [200 200]))

(defn- update-velocity [object]
  (let [velocity (mxo/* (mx/normalise (mxo/- (:target object) (:location object))) 1)]
  (assoc-in object [:velocity] velocity)))

(defn- move-object [object]
  (let [location (:location object)
        velocity (:velocity object)]
    (assoc-in object [:location] (mxo/+ location velocity))))

(defn- scan-for-objects [location radius objects]
  (filter (fn [object] 
            (let [scan  (mxo/- (:location object) location)]
              (and (> radius (Math/abs (first scan)))
                   (> radius (Math/abs (second scan)))
                   (not (= (:location object) location)))))
          objects))

(defn- target-enemy [object objects]
  (let [enemy (first (scan-for-objects (:location object) 30 objects))]
    (if (not (nil? enemy))
      (assoc-in object [:target] (mxo/- (:location object) (mxo/- (:location enemy) (:location object))))
      (assoc-in object [:target] [200 200]))))

(defmulti sense (fn [object objects] (:mood object)))
(defmethod sense :default [object objects]
  (-> object
      (target-enemy objects)))

(defmulti react (fn [object] (:mood object)))
(defmethod react :default [object]
  (-> object
      (move-object)
      (update-velocity)))

(defn update [object objects]
  (-> object
      (sense objects)
      (react)))


