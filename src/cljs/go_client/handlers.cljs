(ns go-client.handlers
  (:require-macros
    [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require [re-frame.core :as re-frame]
            [go-client.db :as db]
            [cljs.core.async :as async :refer (<! >! put! chan)]
            [taoensso.sente :as sente :refer [cb-success?]]
            [taoensso.encore :as encore :refer [tracef infof]]))

(defn websocket-service-url []
  "ws://localhost:8080/chsk")

(re-frame/register-handler
  :server-push
  (fn [db [_ payload]]
    (tracef (pr-str (:event payload)))
    db))

(re-frame/register-handler
  :register-to-server
  (fn [db _]
    (let [{:keys [ch-recv send-fn state]}
          (sente/make-channel-socket! "/chsk" {:chsk-url-fn websocket-service-url})]

      ;; send-to-server handler
      (re-frame/register-handler
        :send-to-server
        (fn [db [_ server-v]]
          (send-fn server-v
                   1000
                   (fn [edn-reply]
                     (when-not (cb-success? edn-reply)
                       (infof (pr-str edn-reply " - " server-v)))))
          db))

      ;; set-server-state handler
      (re-frame/register-handler
        :set-server-connected
        (fn [db [_ active?]]
          (assoc db :server-connected? active?)))

      ;; setting up server-state-watcher
      (add-watch state :server-state-watcher
                 (fn [k r o n]
                   (when (not= o n)
                     ;;server connection state changed
                     (re-frame/dispatch-sync [:set-server-connected (:open? n)]))))

      ;; listening for server pushes
      (go-loop [pushed-message (<! ch-recv)]
               (re-frame/dispatch-sync [:server-push pushed-message])
               (recur (<! ch-recv))))
    db))

(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame/register-handler
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc db :active-panel active-panel)))

(re-frame/register-handler
  :set-active-game
  (fn [db [_ active-game]]
    (assoc db :active-game active-game)))
