(ns immutables.world
  (:require [immutables.object :as bot]))

(def world (atom {:bots []}))

(def starting-world {:bots [{:energy 100.0 :target [0 0] :velocity [0 0] :location [618.047 809.0] :name "brady"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [451.368 935.937] :name "korny"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [451.377 224.457] :name "jean"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [1082.714 634.363] :name "sarah"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [82.837 169.189] :name "fred"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [1084.84 256.605] :name "bob"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [464.22 1001.742] :name "leo"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [1218.664 378.174] :name "claire"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [1010.834 60.911] :name "cassie"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [447.985 86.483] :name "mia"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [242.929 68.12] :name "erlangga"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [1053.962 630.598] :name "steve"}
                            {:energy 100.0 :target [0 0] :velocity [0 0] :location [1335.016 570.246] :name "andy"}]})

(defn- update-bots [world]
  (map #(bot/update % world) (:bots world)))

(defn render []
  (str @world))

(defn update []
  (swap! world (fn [old-world] 
                 (let [old-bots (:bots old-world)]
                       (assoc-in old-world [:bots] (update-bots old-world))))))

(defn init []
  (reset! world starting-world))

(defn add-bot [new-bot]
  (swap! world (fn [current-world]
                 (let [bots (:bots current-world)]
                   (assoc-in current-world [:bots] (conj bots new-bot))))))
