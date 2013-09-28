(ns immutables.world
  (:require [immutables.object :as object]))

(def world (atom {:objects []}))

(def starting-world {:objects [{:velocity [0 0] :location [318.047 409.0] :name "brady"}
                               {:velocity [0 0] :location [251.368 535.937] :name "korny"}
                               {:velocity [0 0] :location [251.377 124.457] :name "jean"}
                               {:velocity [0 0] :location [582.714 334.363] :name "sarah"}
                               {:velocity [0 0] :location [42.837 89.189] :name "fred"}
                               {:velocity [0 0] :location [584.84 156.605] :name "bob"}
                               {:velocity [0 0] :location [264.22 561.742] :name "leo"}
                               {:velocity [0 0] :location [618.664 178.174] :name "claire"}
                               {:velocity [0 0] :location [510.834 30.911] :name "cassie"}
                               {:velocity [0 0] :location [247.985 46.483] :name "mia"}
                               {:velocity [0 0] :location [142.929 38.12] :name "erlangga"}
                               {:velocity [0 0] :location [553.962 330.598] :name "steve"}
                               {:velocity [0 0] :location [335.016 270.246] :name "andy"}]})

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
