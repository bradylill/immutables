(ns immutables.world
  (:require [immutables.bot :as bot]))

(def world (atom {:bots []}))

(def starting-world {:bots [{:tactic :chase  :armor 5.5 :attack-radius 35 :damage 6.5 :speed 4 :sight 90 :energy 100.0 :target [500 200] :velocity [0 0] :location [618.047 809.0] :name "brady"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 2 :sight 30 :energy 100.0 :target [300 200] :velocity [0 0] :location [451.368 935.937] :name "korny"}
                            {:tactic :escape :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 60 :energy 100.0 :target [700 200] :velocity [0 0] :location [451.377 224.457] :name "jean"}
                            {:tactic :escape :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [1082.714 634.363] :name "sarah"}
                            {:tactic :escape :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [82.837 169.189] :name "fred"}
                            {:tactic :escape :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [1084.84 256.605] :name "bob"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 2 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [464.22 1001.742] :name "leo"}
                            {:tactic :escape :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 80 :energy 100.0 :target [350 400] :velocity [0 0] :location [1218.664 378.174] :name "claire"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 2 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [1010.834 60.911] :name "cassie"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [447.985 86.483] :name "mia"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 4 :sight 10 :energy 100.0 :target [350 400] :velocity [0 0] :location [242.929 68.12] :name "erlangga"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 4 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [1053.962 630.598] :name "steve"}
                            {:tactic :chase  :armor 1.5 :attack-radius 25 :damage 0.5 :speed 1 :sight 30 :energy 100.0 :target [350 400] :velocity [0 0] :location [1335.016 570.246] :name "andy"}]})

(defn- update-bots [world]
  (pmap #(bot/update % world) (:bots world)))

(defn- remove-dead [bots]
  (filter #(<= 0 (:energy %)) bots))

(defn- next-generation [old-world]
  (-> (update-bots old-world)
      (remove-dead)))

(defn render []
  (str @world))

(defn update []
  (swap! world (fn [old-world] 
                 (assoc-in old-world [:bots] (next-generation old-world)))))

(defn init []
  (reset! world starting-world))

(defn add-bot [new-bot]
  (println "adding bot:" new-bot)
  (swap! world (fn [current-world]
                 (let [bots (:bots current-world)]
                   (assoc-in current-world [:bots] (conj bots new-bot))))))

(defn make-random-bot [name]
  (add-bot {:tactic :chase :attack-radius 25 :damage 8.5 :speed 3 :sight 20 :energy 50.0 :target [840 525] :velocity [0 0] :location [(rand 1680) (rand 1050)] :name name})
  )

(defn as-int [str]
  (Integer/parseInt str))

(defn verify-name [field]
  (if (or (clojure.string/blank? field)
          (< 255 (.length field)))
    (throw (Exception. (str "Invalid name '" field "'")))))

(defn verify-range [field]
  (let [val (as-int field)]
    (if (or (> 0 val)
            (< 100 val))
      (throw (Exception. (str "Invalid field '" field "' - must be int 0 to 100"))))))

(defn scale [percent min max]
  (+ min
     (* (- max min) (/ percent 100.0))))

(defn add-user-bot [name speed damage range]
  (verify-name name)
  (verify-range speed)
  (verify-range damage)
  (verify-range range)
  (let [speed (as-int speed)
        damage (as-int damage)
        range (as-int range)
        min-speed 1.0
        max-speed 4.0
        min-armor 1.0
        max-armor 2.0
        min-damage 1.0
        max-damage 10.0
        min-regen 0.0
        max-regen 10.0
        min-range 10.0
        max-range 30.0
        min-sight 20.0
        max-sight 50.0
        bot-speed (scale speed min-speed max-speed)
        bot-armor (scale (- 100 speed) min-armor max-armor)
        bot-damage (scale damage min-damage max-damage)
        bot-regen (scale (- 100 damage) min-regen max-regen)
        bot-range (scale range min-range max-range)
        bot-sight (scale (- 100 range) min-sight max-sight)
        ]
    (add-bot {:speed bot-speed
              :armor bot-armor
              :damage bot-damage
              :regen bot-regen
              :attack-radius bot-range
              :sight bot-sight
              :energy 100.0
              :target [840 525]
              :velocity [0 0]
              :location [(rand 1680) (rand 1050)]
              :name name})))
