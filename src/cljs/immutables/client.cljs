(ns immutables.client
  (:require
   [cljs.core.async :as async
    :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
   [jayq.core :refer [$ append ajax inner css $deferred
                      when done resolve pipe on bind attr
                      offset] :as jq]
   [jayq.util :refer [log]]
   [monet.canvas :refer [get-context] :as canvas]
   [immutables.graphics :as graphics]
   [immutables.async-utils :as au])
  (:require-macros [cljs.core.async.macros :as m :refer [go alt!]]))

(defn handle-click []
  (js/alert "Hello!"))

(def the-canvas ($ :canvas#game))
(def ctx
    (canvas/get-context (.get the-canvas 0) "2d"))

(def active (atom true))

(def slowth (atom 50))

(defn js-now [] (.now (.-performance js/window)))

(defn scaled-time [now last-time]
  (/ (- now last-time) 50.0))

(defn game-loop []
  (let [anim-chan (au/anim-ch active)
        ajax-chan (au/ajax-loop "world" slowth active)]
    (go
     (loop [state {:world {} :timestamp (js-now)}]
       (when @active
         (let [next-state
                 (alt!
                    anim-chan ([val] (do
                                       (graphics/draw-map
                                          ctx (:world state) (scaled-time val (:timestamp state)))
                                       state)) ; no state change
                    ajax-chan ([val] {:world val :timestamp (js-now)}))]
           (recur next-state)))))))

(def pausebtn ($ :#pause))

(defn start-game []
  (reset! active true)
  (jq/text pausebtn "Pause")
  (game-loop))

(defn stop-game []
  (jq/text pausebtn "Resume")
  (reset! active false))

(defn toggle-paused []
  (if @active
    (stop-game)
    (start-game)))

(defn init-world []
  (ajax "init"
        {:dataType "edn"
         :success identity}))

(def modal-dialog ($ :#botModal))
(def modal-close ($ :#modalClose))
(def bot-name ($ :#botname))
(def bot-speed ($ :#speed))
(def bot-damage ($ :#damage))
(def bot-regen ($ :#regen))

(defn hide-modal []
  (jq/trigger modal-close :click))

(defn reset-modal []
  (jq/val bot-name "")
  (jq/val bot-speed "50")
  (jq/val bot-damage "50")
  (jq/val bot-regen "50")
)

(defn create-bot-modal []
  (ajax "createbot"
        {:type "POST" :data { :name (jq/val bot-name)
                             :speed (jq/val bot-speed)
                             :damage (jq/val bot-damage)
                             :regen (jq/val bot-regen)}})
  (reset-modal)
  (hide-modal)
)

(defn nav-to-info []
  (set! (.-location js/window) "http://clojurecup.com/app.html?app=immutables")
  )

(jq/bind pausebtn :click toggle-paused)
(jq/bind ($ :#init) :click init-world)
(jq/bind ($ :#info) :click nav-to-info)
(jq/bind ($ :#modalCreate) :click create-bot-modal)
(jq/bind ($ :#modalCancel) :click hide-modal)
(jq/document-ready start-game)
