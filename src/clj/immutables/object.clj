(ns immutables.object
  (:require [clojure.core.matrix           :as mx]
            [clojure.core.matrix.operators :as mxo]))

(defn- rand-range [start end]
  (let [val-range (range start end)]
    (nth val-range (rand-int (count val-range)))))

(defn- update-velocity [object]
  (assoc-in object [:velocity] [(rand-range -10 10)
                                (rand-range -10 10)]))

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
      (update-velocity)
      (move-object)))

(defn update [object objects]
  (-> object
      (sense objects)
      (react)))


