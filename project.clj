(defproject immutables "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.0"]
                 [org.clojure/clojurescript "0.0-1889"]
                 [com.cemerick/piggieback "0.1.0"]]
  :injections [(require '[cljs.repl.browser :as brepl]
                        '[cemerick.piggieback :as pb])
               (defn browser-repl []
                 (pb/cljs-repl :repl-env (brepl/repl-env :port 9000)))]
  :plugins [[lein-cljsbuild "0.3.3"]
            [lein-ring "0.8.7"]]
  :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :profiles {:dev
             {:cljsbuild {:builds
                          [{:source-paths ["src/brepl" "src/cljs"]
                            :compiler {:output-to "resources/public/js/cljs.js"
                                       :optimizations :simple
                                       :pretty-print true}
                            :jar true}]}}
             :prod
             {:cljsbuild {:builds
                          [{:source-paths ["src/cljs"]
                            :compiler {:output-to "resources/public/js/cljs.js"
                                       :optimizations :advanced
                                       :pretty-print false}
                            :jar true}]}}}
  :main immutables.server
  :ring {:handler immutables.server/app})

