(ns immutables.server
  (:require [ring.adapter.jetty       :as jetty]
            [ring.middleware.resource :as resources]
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

(defroutes app
  (GET "/" [] (response/redirect "/game"))
  (GET "/game" []
       (response/resource-response "public/game.html"))
  (GET "/world" [] (response/response (world/render)))
  (GET "/init"  [] (response/response (do (world/init)
                                          (world/render))))

  (POST "/createbot" {body :body} (world/make-random-bot (slurp body)))

  (route/resources "/")
  (route/not-found (response/resource-response "public/missing.html"))
  )

(defn -main [& args]
  (init)
  (jetty/run-jetty app {:port 3000}))

