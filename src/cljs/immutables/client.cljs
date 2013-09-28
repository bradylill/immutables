(ns immutables.client
  (:require
   [cljs.core.async :as async
    :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
   [jayq.core :refer [$ append ajax inner css $deferred
                      when done resolve pipe on bind attr
                      offset] :as jq]
   [jayq.util :refer [log]]
   [monet.canvas :refer [get-context] :as canvas])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(defn handle-click []
  (js/alert "Hello!"))

(def the-canvas ($ :canvas#game))
#_(def ctx (canvas/get-context (.get the-canvas 0) "2d"))

(def ctx (canvas/init (.get the-canvas 0)))

(defn draw-something []
  (canvas/add-entity ctx :background
                     (canvas/entity {:x 50 :y 50 :w 100 :h 100}
                                    nil ;;update function
                                    (fn [ctx box]
                                      (-> ctx
                                          (canvas/fill-style "#596d31")
                                          (canvas/rect box)))))
)

(def clickable ($ :#clickable))
(jq/bind clickable :click draw-something)
