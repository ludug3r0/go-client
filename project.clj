(defproject go-client "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url  "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojurescript "0.0-2511"]
                           [environ "1.0.0"]
                           [ring/ring-devel "1.3.2"]
                           [http-kit "2.1.19"]
                           [javax.servlet/servlet-api "2.5"]
                           [reagent "0.4.3"]
                           [compojure "1.3.1"]]
            :source-paths ["src/clj"]

            :profiles {:dev {:plugins [[lein-cljsbuild "1.0.3"]]}}

            :cljsbuild {:builds [{:source-paths ["src/cljs"]
                                  :compiler     {:preamble     ["reagent/react.js"]
                                                 :output-to    "resources/public/app/main.js"
                                                 :source-map   "resources/public/app/main.js.map"
                                                 :output-dir   "resources/public/app/temp"
                                                 :pretty-print true}}]}

            :prep-tasks [["cljsbuild" "clean"]
                         ["cljsbuild" "once"]])
