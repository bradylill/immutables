(ns immutables.world)

(def world (atom {:objects []}))

(def starting-world {:objects [{:location [318.047 409.0] :name "brady"}
                               {:location [251.368 535.937] :name "korny"}
                               {:location [251.377 124.457] :name "jean"}
                               {:location [582.714 334.363] :name "sarah"}
                               {:location [42.837 89.189] :name "fred"}
                               {:location [584.84 156.605] :name "bob"}
                               {:location [264.22 561.742] :name "leo"}
                               {:location [618.664 178.174] :name "claire"}
                               {:location [510.834 30.911] :name "cassie"}
                               {:location [247.985 46.483] :name "mia"}
                               {:location [142.929 38.12] :name "erlangga"}
                               {:location [553.962 330.598] :name "steve"}
                               {:location [335.016 270.246] :name "andy"}]})

(defn render []
  (str @world))

(defn update []
  (swap! world (fn [old] old)))

(defn init []
  (reset! world starting-world))

(defn add-object [new-object]
  (swap! world (fn [current-world]
                 (let [objects (:objects current-world)]
                   (update-in current-world [:objects] (conj objects new-object))))))
