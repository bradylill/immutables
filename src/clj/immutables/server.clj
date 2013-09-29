(ns immutables.server
  (:require [ring.adapter.jetty       :as jetty]
            [ring.middleware.resource :as resources]
            [ring.middleware.params   :refer [wrap-params]]
            [ring.util.response       :as response]
            [compojure.core           :refer :all]
            [compojure.route          :as route]
            [immutables.world         :as world])
  (:gen-class))

(defn init []
  (.start (Thread. (fn [] (loop [] 
                            (world/update)
                            (Thread/sleep 50)
                            (recur))))))

(defroutes game-routes
  (GET "/" [] (response/redirect "/game"))
  (GET "/game" []
       (response/resource-response "public/game.html"))
  (GET "/world" [] (response/response (world/render)))
  (GET "/init"  [] (response/response (do (world/init)
                                          (world/render))))

  (POST "/createbot" [name speed damage range] (do (println "bot params:" name speed damage range) (world/add-user-bot name speed damage range)))

  (route/resources "/")
  (route/not-found (response/resource-response "public/missing.html")))

(def app (wrap-params game-routes))

(defn -main [& args]
  (init)
  (jetty/run-jetty app {:port 3000}))

