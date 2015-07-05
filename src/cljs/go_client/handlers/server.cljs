(ns go-client.handlers.server
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [re-frame.core :as re-frame]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.sente :as sente :refer [cb-success?]]
            [taoensso.encore :as encore :refer [tracef infof debugf]]))

(re-frame/register-handler
  :server-event
  (fn [db [_ payload]]
    ;;TODO: handle go_client.handlers events
    (infof (pr-str (:event payload)))
    db))

;; set-go_client.handlers-state handler
(re-frame/register-handler
  :set-server-state
  (fn [db [_ state]]
    (debugf (pr-str state))
    (let [{:keys [open? uid]} state]
      (assoc db :server {:open? open?
                         :uid uid}))))

(re-frame/register-handler
  :connect-to-server
  (fn [db _]
    (sente/ajax-call
      "/csrf-token"
      {:method :get}
      (fn [ajax-resp]
        (if (= (:?status ajax-resp) 200)
          (let [{:keys [ch-recv send-fn state chsk]} (sente/make-channel-socket! "/chsk")]
            ;; log user into go_client.handlers
            (re-frame/register-handler
              :log-into-server
              (fn [db [_ user-id]]
                (sente/ajax-call
                  "/login"
                  {:method :post
                   :params {:user-id    user-id
                            :csrf-token (:csrf-token @state)}}
                  (fn [ajax-resp]
                    (let [login-successful? (= (:?status ajax-resp) 200)]
                      (if-not login-successful?
                        (debugf "Login failed")
                        (do
                          (debugf "Login successful")
                          (sente/chsk-reconnect! chsk))))))
                db))

            (re-frame/register-handler
              :logout-from-server
              (fn [db [_ user-id]]
                (sente/ajax-call
                  "/logout"
                  {:method :post
                   :params {:csrf-token (:csrf-token @state)}}
                  (fn [ajax-resp]
                    (let [logout-successful? (= (:?status ajax-resp) 200)]
                      (if-not logout-successful?
                        (debugf "Logout failed")
                        (do
                          (debugf "Logout successful")
                          (sente/chsk-reconnect! chsk))))))
                db))

            ;; send events to go_client.handlers
            (re-frame/register-handler
              :send-event-to-server
              (fn [db [_ server-v]]
                (send-fn server-v
                         1000
                         (fn [edn-reply]
                           (when-not (cb-success? edn-reply)
                             (infof (pr-str edn-reply " - " server-v)))))
                db))

            ;; setting up go_client.handlers-state-watcher
            (add-watch state :server-state-watcher
                       (fn [k r o n]
                         (when (not= o n)
                           ;;go_client.handlers connection state changed
                           (re-frame/dispatch [:set-server-state n]))))

            ;; listening for go_client.handlers pushes
            (go-loop [pushed-message (<! ch-recv)]
                     (re-frame/dispatch-sync [:server-event pushed-message])
                     (recur (<! ch-recv)))))))

    db))