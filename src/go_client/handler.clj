(ns go-client.handler
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [environ.core :refer [env]]
            [org.httpkit.server :refer [run-server]]
            [ring.middleware.reload :as reload]
            [ring.middleware.stacktrace :as trace]
            [compojure.handler :refer [site]]
            [ring.middleware.session.cookie :as cookie]
            [clojure.java.io :as io]))

(defroutes
  app
  (GET "/" [] "Hello world!")
  (route/resources "/")
  (route/not-found (slurp (io/resource "404.html"))))

(defn wrap-error-page [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))
        store (cookie/cookie-store {:key (env :session-secret)})]
    (run-server (-> #'app
                    ((if (env :production)
                       wrap-error-page
                       (comp trace/wrap-stacktrace
                             reload/wrap-reload)))
                    (site {:session {:store store}}))
                {:port port :join? false})))