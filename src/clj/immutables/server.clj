(ns immutables.server
  (:require [ring.adapter.jetty       :as jetty]
            [ring.middleware.resource :as resources]
            [ring.util.response       :as response]
            [compojure.core           :refer :all]
            [compojure.route          :as route])
  (:gen-class))

(defn render-world []
  (str {:object [{:location [15 12]
                  :name     "dude1"}
                 {:location [10 10]
                  :name     "dude2"}]}))

(defroutes app
  (GET "/" [] (response/file-response "game.html" {:root "resources/public"}))
  (GET "/world" [] (response/response (render-world)))
  (route/not-found (response/file-response "help.html" {:root "resources/public"})))

(defn -main [& args]
  (jetty/run-jetty app {:port 3000}))

