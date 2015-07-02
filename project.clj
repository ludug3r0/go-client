(defproject go-client "0.2.0-SNAPSHOT"
            :description "A Clojure & ClojureScript client for playing the Game of Go"
            :url "https://github.com/ludug3r0/go-client"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-3211"]
                           [reagent "0.5.0"]
                           [re-frame "0.4.1"]
                           [re-com "0.5.4"]
                           [secretary "1.2.3"]
                           [com.taoensso/sente "1.5.0"]
                           [com.taoensso/timbre "3.4.0"]
                           [org.clojars.ludug3r0/go-schema "0.0.4-SNAPSHOT"]
                           [org.clojars.ludug3r0/go-rules "0.0.2-SNAPSHOT"]]

            :source-paths ["src/clj"]

            :plugins [[lein-cljsbuild "1.0.6"]
                      [lein-figwheel "0.3.3" :exclusions [cider/cider-nrepl]]]

            :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

            :cljsbuild {:builds [{:id           "dev"
                                  :source-paths ["src/cljs"]

                                  :figwheel     {:on-jsload "go-client.core/mount-root"}

                                  :compiler     {:main                 go-client.core
                                                 :output-to            "resources/public/js/compiled/app.js"
                                                 :output-dir           "resources/public/js/compiled/out"
                                                 :asset-path           "js/compiled/out"
                                                 :source-map-timestamp true}}

                                 {:id           "min"
                                  :source-paths ["src/cljs"]
                                  :compiler     {:main          go-client.core
                                                 :output-to     "resources/public/js/compiled/app.js"
                                                 :optimizations :advanced
                                                 :pretty-print  false}}]})
