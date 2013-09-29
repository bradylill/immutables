(ns immutables.world
  (:require [immutables.object :as object]))

(def world (atom {:objects []}))

(def starting-world {:objects [{:energy 100.0 :target [0 0] :velocity [0 0] :location [618.047 809.0] :name "brady"}
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

(defn- update-objects [objects]
  (pmap #(object/update % objects) objects))

(defn render []
  (str @world))

(defn update []
  (swap! world (fn [old-world] 
                 (let [old-objects (:objects old-world)]
                       (assoc-in old-world [:objects] (update-objects old-objects))))))

(defn init []
  (reset! world starting-world))

(defn add-object [new-object]
  (swap! world (fn [current-world]
                 (let [objects (:objects current-world)]
                   (assoc-in current-world [:objects] (conj objects new-object))))))
