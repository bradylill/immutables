(ns immutables.world)

(def world (atom {:objects []}))

(def starting-world {:objects [{:location [15 12]
                                :name     "dude1"}
                               {:location [10 10]
                                :name     "dude2"}]})

(defn render []
  (str @world))

(defn init []
  (reset! world starting-world))

(defn add-object [new-object]
  (swap! world (fn [current-world]
                 (let [objects (:objects current-world)]
                   (update-in current-world [:objects] (conj objects new-object))))))
