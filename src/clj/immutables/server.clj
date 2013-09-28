(ns immutables.server
  (:require [ring.adapter.jetty       :as jetty]
            [ring.middleware.resource :as resources]
            [ring.util.response       :as response]
            [compojure.core           :refer :all]
            [compojure.route          :as route]
            [immutables.world         :as world])
  (:gen-class))

(defn init-game []
  (.start (Thread. (fn [] (loop [] 
                            (world/update)
                            (println "updating")
                            (Thread/sleep 5000)
                            (recur))))))

(defroutes app
  (GET "/" [] (response/file-response "game.html" {:root "resources/public"}))
  (GET "/world" [] (response/response (world/render)))
  (GET "/init"  [] (response/response (do (world/init)
                                          (world/render))))
  (route/resources "/")
  (route/not-found (response/file-response "help.html" {:root "resources/public"})))

(defn -main [& args]
  (init-game)
  (jetty/run-jetty app {:port 3000}))

