(ns immutables.world)

(defn render []
  (str {:object [{:location [15 12]
                  :name     "dude1"}
                 {:location [10 10]
                  :name     "dude2"}]}))