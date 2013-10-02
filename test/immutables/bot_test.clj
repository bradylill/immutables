(ns immutables.bot_test
  (:require [midje.sweet :refer :all]
            [immutables.bot :as bot]))

(fact "rand-rang is a value between start and end"
      (bot/rand-range -1 1) => (roughly 0 1)
      (bot/rand-range -10 10) => (roughly 0 10))

;TODO: Way of checking the range.
#_(fact "rand-location is a 2d vector with x and y bettween start and end"
      (bot/rand-location -2 2) => #([-2 -1 0 1 2] %))

(fact "update-velocity calculates the bots new velocity vector based on its target"
      (let [test-bot {:location [0 0]
                      :target   [2 3]
                      :speed    3}]
      (bot/update-velocity test-bot) 
        => {:location [0 0] 
            :target [2 3] 
            :speed 3
            :velocity [1.6641005886756874
                       2.4961508830135313]}))

(fact "set-location overrides the bots current location"
      (bot/set-location {:location [0 0]} [5 4]) => {:location [5 4]})

(fact "set-target overrides the bots current target"
      (bot/set-target {:target [0 0]} [1 3]) => {:target [1 3]})

(fact "keep-inbounds adjusts the bots position so that it wraps around the world"
      (let [middle  [840 525]]
        (bot/keep-inbounds {:location [1681 10]})
          => {:location [2 10]
              :target   middle}
        
        (bot/keep-inbounds {:location [-1 10]})
          => {:location [1678 10]
              :target   middle}
        
        (bot/keep-inbounds {:location [10 1051]})
          => {:location [10 2]
              :target   middle}
        
        (bot/keep-inbounds {:location [10 -1]})
          => {:location [10 1048]
              :target   middle})) 

(fact "move-bot applies the velocity to the bots location"
      (bot/move-bot {:location [0 0]
                     :velocity [3 5]})
        => {:location [3 5]
            :velocity [3 5]})

(fact "scan-for-bots finds all within a radius from a location"
      (let [in-bot  {:location [5 5]}
            out-bot {:location [6 5]}]
        (bot/scan-for-bots [0 0] 5 [in-bot out-bot])
          => [in-bot]))

(facts "about tactics" 
       (let [enemy {:location [3 8]}]
         (fact "chasing bot targets close to the enemies position"
               (let [[tx ty] (bot/tactic {:tactic :chase} enemy)]
                 tx => (roughly 3 10)
                 ty => (roughly 8 10)))
         (fact "escaping bot targets the opposite of the enemy position"
               (bot/tactic {:tactic :escape 
                            :location [0 0]} enemy)
               => [-3 -8])
         (fact "default bot targets itself"
               (bot/tactic {:tactic nil
                            :location [9 4]} enemy)
               => [9 4])))
