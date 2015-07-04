(ns go-client.handlers
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [re-frame.core :as re-frame]
            [go-client.db :as db]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.sente :as sente :refer [cb-success?]]
            [taoensso.encore :as encore :refer [tracef infof debugf]]))

(re-frame/register-handler
  :server-event
  (fn [db [_ payload]]
    ;;TODO: handle server events
    ;(infof (pr-str (:event payload)))
    db))

(re-frame/register-handler
  :unset-server-handlers
  (fn [db _]

    db))

(re-frame/register-handler
  :set-server-handlers
  (fn [db [_ ch-recv send-fn state chsk]]

    db))

(re-frame/register-handler
  :connect-to-server
  (fn [db _]
    (sente/ajax-call
      "/csrf-token"
      {:method :get}
      (fn [ajax-resp]
        (if (= (:?status ajax-resp) 200)
          (let [{:keys [ch-recv send-fn state chsk]} (sente/make-channel-socket! "/chsk")]
            ;; log user into server
            (re-frame/register-handler
              :log-into-server
              (fn [db [_ user-id]]
                (sente/ajax-call
                  "/login"
                  {:method :post
                   :params {:user-id    user-id
                            :csrf-token (:csrf-token @state)}}
                  (fn [ajax-resp]
                    (debugf "Ajax login POST response: %s" ajax-resp)
                    (let [login-successful? (= (:?status ajax-resp) 200)]
                      (if-not login-successful?
                        (debugf "Login failed")
                        (do
                          (debugf "Login successful")
                          (sente/chsk-reconnect! chsk))))))
                db))

            ;; send events to server
            (re-frame/register-handler
              :send-event-to-server
              (fn [db [_ server-v]]
                (send-fn server-v
                         1000
                         (fn [edn-reply]
                           (when-not (cb-success? edn-reply)
                             (infof (pr-str edn-reply " - " server-v)))))
                db))

            ;; setting up server-state-watcher
            (add-watch state :server-state-watcher
                       (fn [k r o n]
                         (when (not= o n)
                           ;;server connection state changed
                           (re-frame/dispatch [:set-server-state n]))))

            ;; listening for server pushes
            (go-loop [pushed-message (<! ch-recv)]
                     (re-frame/dispatch-sync [:server-event pushed-message])
                     (recur (<! ch-recv)))
            )
          )
        ))

    db))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

;; set-server-state handler
(re-frame/register-handler
  :set-server-state
  (fn [db [_ state]]
    (assoc db :server-state state)))

(re-frame/register-handler
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc db :active-panel active-panel)))

(re-frame/register-handler
  :set-active-game
  (fn [db [_ active-game]]
    (assoc db :active-game active-game)))
