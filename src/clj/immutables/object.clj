(ns immutables.object
  (:require [clojure.core.matrix           :as mx]
            [clojure.core.matrix.operators :as mxo]))

(defn- boid-location [] 
  [(Math/abs (* (Math/sin (System/currentTimeMillis)) 512))
   (Math/abs (* (Math/cos (System/currentTimeMillis)) 512))])

(defn- rand-range [start end]
  (let [val-range (range start end)]
    (nth val-range (rand-int (count val-range)))))

(defn- update-target [object]
  (assoc-in object [:target] (boid-location)))

(defn- update-velocity [object]
  (let [velocity (mx/normalise (mxo/- (:target object) (:location object)))]
  (assoc-in object [:velocity] velocity)))

(defn- move-object [object]
  (let [location (:location object)
        velocity (:velocity object)]
    (assoc-in object [:location] (mxo/+ location velocity))))

(defmulti sense (fn [object objects] (:mood object)))
(defmethod sense :default [object objects]
  object)

(defmulti react (fn [object] (:mood object)))
(defmethod react :default [object]
  (-> object
      (move-object)
      (update-target)
      (update-velocity)))

(defn update [object objects]
  (-> object
      (sense objects)
      (react)))


