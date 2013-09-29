(ns immutables.world
  (:require [immutables.object :as object]))

(def world (atom {:objects []}))

(def starting-world {:objects [{:energy 100.0 :target [0 0] :velocity [0 0] :location [318.047 409.0] :name "brady"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [251.368 535.937] :name "korny"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [251.377 124.457] :name "jean"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [582.714 334.363] :name "sarah"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [42.837 89.189] :name "fred"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [584.84 156.605] :name "bob"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [264.22 561.742] :name "leo"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [618.664 178.174] :name "claire"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [510.834 30.911] :name "cassie"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [247.985 46.483] :name "mia"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [142.929 38.12] :name "erlangga"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [553.962 330.598] :name "steve"}
                               {:energy 100.0 :target [0 0] :velocity [0 0] :location [335.016 270.246] :name "andy"}]})

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
