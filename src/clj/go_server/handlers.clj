(ns go-server.handlers)

(defonce db (atom {}))

;; event handlers

(defmulti handle-event :id)
(defmethod handle-event :util/echo [event-msg]
  (when-let [uid (get-in event-msg [:ring-req :session :uid])]
    ((:send-fn event-msg) uid [:util/echo (:?data event-msg)])))

(defmethod handle-event :util/broadcast [event-msg]
  ;; user must be logged in
  (when (get-in event-msg [:ring-req :session :uid])
    (doseq [uid (-> event-msg :connected-uids deref :any)]
      ((:send-fn event-msg) uid [:util/broadcast (:?data event-msg)]))))

(defmethod handle-event :default [event-msg]
  (let [event-id (:id event-msg)
        server-event-ids #{:chsk/ws-ping :chsk/bad-package :chsk/bad-event :chsk/uidport-open :chsk/uidport-close}]
    (when-not (contains? server-event-ids event-id)
      (prn "UNREGISTERED EVENT HANDLER FOR: " event-msg))))

;; --------------------------



(defn event-msg-handler [{:keys [:?reply-fn] :as event-msg}]
  (handle-event event-msg)
  (when-let [reply-fn (:?reply-fn event-msg)]
    (reply-fn :ack)))