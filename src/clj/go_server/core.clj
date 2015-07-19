(ns go-server.core
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]

    [taoensso.sente :as sente]
    [taoensso.timbre :as timbre :refer [infof debugf]]

    [org.httpkit.server :refer [run-server]]
    [taoensso.sente.server-adapters.http-kit :refer (sente-web-server-adapter)]

    [ring.middleware.keyword-params]
    [ring.middleware.params]
    [ring.middleware.reload :as reload]
    [ring.middleware.defaults]
    [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]

    [clojure.core.async :as async :refer (<! >! put! chan)]
    ))

;;TODO #5: improve how we bind up these channels and handlers
(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! sente-web-server-adapter {})]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)                                     ; ChannelSocket's receive channel
  (def chsk-send! send-fn)                                  ; ChannelSocket's send API fn
  (def connected-uids connected-uids)                       ; Watchable, read-only atom
  )

;;TODO #5: extract this to a proper server handler namespace/package
(async/go-loop [event-msg (<! ch-chsk)]
  ;; handle events
  (condp :id event-msg
    :util/echo (chsk-send! :sente/all-users-without-uid [:util/echo (:?data event-msg)]))

  ;; reply if the user requests
  (when-let [reply-fn (:?reply-fn event-msg)]
    (reply-fn :ack))
  (recur (<! ch-chsk)))

;;TODO #4: proper login logic
(defn login! [ring-request]
  (let [{:keys [session params]} ring-request
        {:keys [user-id]} params]
    (debugf "Login request: %s" params)
    {:status 200 :session (assoc session :uid user-id)}))

(defn logout! [ring-request]
  (let [{:keys [session params]} ring-request]
    (debugf "Logout request: %s" params)
    {:status 200 :session (dissoc session :uid)}))

(defroutes my-app-routes
           (GET "/chsk" req (ring-ajax-get-or-ws-handshake req))
           (POST "/chsk" req (ring-ajax-post req))

           (GET "/csrf-token" req (-> (ring.util.response/response (pr-str {:csrf-token *anti-forgery-token*}))
                                      (ring.util.response/header "csrf-token" *anti-forgery-token*)
                                      (ring.util.response/content-type "application/edn")))
           (POST "/logout" req (logout! req))
           (POST "/login" req (login! req))

           (route/resources "/"))

(def my-app
  (let [ring-defaults-config
        (-> ring.middleware.defaults/site-defaults
            (assoc-in [:security :anti-forgery] {:read-token (fn [req] (-> req :params :csrf-token))}))]
    (-> (reload/wrap-reload #'my-app-routes)
        (ring.middleware.defaults/wrap-defaults ring-defaults-config))))

;; entry point
;;TODO #6: add settings module
(defn -main [& args]
  (run-server my-app {:port 8080}))


