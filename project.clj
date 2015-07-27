(defproject go-client "0.2.0-SNAPSHOT"
            :description "A Clojure & ClojureScript client for playing the Game of Go"
            :url "https://github.com/ludug3r0/go-client"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [
                           ;; go_client.handlers side dependencies
                           [org.clojure/clojure "1.6.0"]
                           [http-kit "2.1.18"]
                           [ring/ring-devel "1.1.8"]
                           [ring/ring-core "1.4.0-RC2"]
                           [ring/ring-defaults "0.1.5"]
                           [compojure "1.3.4"]

                           ;; client side dependencies
                           [org.clojure/clojurescript "0.0-3211"]
                           [reagent "0.5.0"]
                           [re-frame "0.4.1"]
                           [re-com "0.5.4"]
                           [secretary "1.2.3"]
                           [org.clojars.ludug3r0/go-schema "0.0.4-SNAPSHOT"]
                           [org.clojars.ludug3r0/go-rules "0.0.2-SNAPSHOT"]
                           [prismatic/schema "0.4.3"]

                           ;; go_client.handlers and client side dependencies
                           [com.taoensso/sente "1.5.0"]
                           [com.taoensso/timbre "3.4.0"]]

            :plugins [[lein-cljsbuild "1.0.6"]
                      [lein-figwheel "0.3.3" :exclusions [cider/cider-nrepl]]
                      [com.cemerick/clojurescript.test "0.3.3"]]

            :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
            :source-paths ["src/clj" "src/cljs"]
            :test-paths ["test/cljs"]
            :hooks [leiningen.cljsbuild]
            :figwheel {:ring-handler go-server.core/my-app}

            :cljsbuild {:builds        [{:id           "dev"
                                         :source-paths ["src/cljs"]

                                         :figwheel     {:on-jsload "go-client.core/mount-root"}

                                         :compiler     {:main                 go-client.core
                                                        :output-to            "resources/public/js/compiled/app.js"
                                                        :output-dir           "resources/public/js/compiled/out"
                                                        :asset-path           "js/compiled/out"
                                                        :source-map-timestamp true}}

                                        {:id       "test"
                                         :source-paths ["test/cljs"]
                                         :compiler {:output-to     "resources/public/js/compiled/tests.js"
                                                    :optimizations :whitespace}}

                                        {:id       "auto-test"
                                         :source-paths ["src/cljs" "test/cljs"]
                                         :notify-command ["phantomjs"
                                                          :cljs.test/runner
                                                          "resources/public/js/compiled/app.js"
                                                          "resources/public/js/compiled/tests.js"]
                                         :compiler {:output-to     "resources/public/js/compiled/tests.js"
                                                    :optimizations :whitespace}}



                                        {:id       "min" :source-paths ["src/cljs"]
                                         :compiler {:main          go-client.core
                                                    :output-to     "resources/public/js/compiled/app.js"
                                                    :optimizations :advanced
                                                    :pretty-print  false}}]
                        :test-commands {"unit-tests" ["phantomjs"
                                                      :cljs.test/runner
                                                      "resources/public/js/compiled/app.js"
                                                      "resources/public/js/compiled/tests.js"]}})
