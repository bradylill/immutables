(ns immutables.async-utils
  (:require    [jayq.core :refer [ajax]]
               [cljs.core.async :refer [chan close! >! <! timeout]])
  (:require-macros [cljs.core.async.macros :as m :refer [go]]))

(defn ajax-get-1 [url]
  (let [ch (chan 1)]
    (ajax url
          {:dataType "edn"
           :success (fn [data]
                      (go (>! ch data)
                          (close! ch)))})
    ch))

(defn ajax-get [url ch]
  (ajax url
        {:dataType "edn"
         :success (fn [data]
                    (go (>! ch data)))})
  ch)

(defn ajax-loop [url delayref activeref]
  (let [ch (chan)]
    (go
     (loop []
       (when @activeref
         (>! ch (<! (ajax-get-1 url)))
         (<! (timeout @delayref))
         (recur))))
    ch))
