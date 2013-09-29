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
  (if (= (:target bot) (:location bot))
    (assoc-in bot [:velocity] [0.0 0.0])
    (let [velocity (mxo/* (mx/normalise (mxo/- (:target bot) (:location bot))) (:speed bot))]
      (assoc-in bot [:velocity] velocity))))

(defn- fix-loc [bot lx ly mx my]
  (-> bot (assoc-in [:location] [lx ly]) (assoc-in [:target] [mx my])))

(defn- keep-inbounds [bot]
  (let [location (:location bot)
        lx (first location)
        ly (second location)
        mx (/ world-width 2)
        my (/ world-height 2)]
    (cond-> bot
      (> lx world-width)  (fix-loc 2 ly mx my)
      (< lx 0)            (fix-loc (- world-width 2) ly mx my)
      (> ly world-height) (fix-loc lx 2 mx my)
      (< ly 0)            (fix-loc lx (- world-height 2) mx my))))

(defn- move-bot [bot]
  (let [location (:location bot)
        velocity (:velocity bot)]
    (-> bot
        (assoc-in [:location] (mxo/+ location velocity))
        (keep-inbounds))))

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
      (assoc-in bot [:target] (tactic bot enemy))
      (if (nil? (:target bot))
        (assoc-in bot [:target] (mxo/+ (:location bot) (rand-location -40 40)))
        bot))))

(def on-target 3.0)
(def too-far 300.0)

(defn- clear-target [bot]
  (let [delta    (mxo/- (:target bot) (:location bot))
        dist     (mx/length delta)]
    (if (or (< dist on-target) (> dist too-far))
      (assoc-in bot [:target] nil)
      bot)))

(defn- attack? [attacking-bot target-bot]
  (let [attack-delta (mxo/- (:location target-bot) (:location attacking-bot))
        attack-mag   (mx/length attack-delta)]
    (< (:attack-radius attacking-bot) attack-mag)))

(defn- scan-for-attacks [bot bots]
  (filter (fn [attacking] (not (attack? attacking bot))) bots))

(defn- calculate-damage [bot attacking-bots]
  (-> (reduce (fn [total attacking-bot] 
                (+ total (:damage attacking-bot))) 
              0 
              attacking-bots)
      (/ (:armor bot))))

(defn- take-damage [bot bots]
  (let [close-bots      (scan-for-bots (:location bot) 40 bots)
        attacking-bots  (scan-for-attacks bot close-bots)
        total-damage    (calculate-damage bot attacking-bots)]
    (update-in bot [:energy] - total-damage)))

(defn- evaluate-tactics [bot]
  (let [max-energy 100.0]
    (if (< (:energy bot) (/ max-energy 2.0))
      (assoc-in bot [:tactic] :escape)
      (assoc-in bot [:tactic] :chase))))

(defn- regen [bot]
  (update-in bot [:energy] + (:regen bot)))

(defn sense [bot world]
  (let [bots (:bots world)]
  (-> bot
      (evaluate-tactics)
      (take-damage bots)
      (clear-target)
      (find-target bots))))

(defn react [bot]
  (-> bot
      (regen)
      (move-bot)
      (update-velocity)))

(defn update [bot world]
  (-> bot
      (sense world)
      (react)))

