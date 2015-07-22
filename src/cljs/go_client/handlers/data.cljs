(ns go-client.handlers.data
  (:require [re-frame.core :as re-frame]
            [re-frame.handlers :as re-frame-handlers]
            [go-client.db :as db]
            [schema.core :as s]))


(re-frame/register-handler
  :initialize-db
  (fn [_ _]
    db/default-db))

(re-frame-handlers/register-base
  :attach-schema-validator
  (fn [db [_ schema]]
    (let [checker (s/checker schema)
          valid? (fn [db]
                   (if-let [problems (checker db)]
                     (throw (js/Error (str "schema check failed: " problems)))
                     :success))]
      (set-validator! db valid?)
      db)))

(re-frame/register-handler
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc db :active-panel active-panel)))

(re-frame/register-handler
  :set-active-game
  (fn [db [_ active-game]]
    (assoc db :active-game active-game)))
