(ns immutables.server
  (:require [ring.adapter.jetty       :as jetty]
            [ring.middleware.resource :as resources]
            [ring.util.response       :as response]
            [compojure.core           :refer :all]
            [compojure.route          :as route]
            [immutables.world         :as world])
  (:gen-class))


(defroutes app
  (GET "/" [] (response/file-response "game.html" {:root "resources/public"}))
  (GET "/world" [] (response/response (world/render)))
  (route/not-found (response/file-response "help.html" {:root "resources/public"})))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))

