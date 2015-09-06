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
                     (throw (js/Error (str "schema check failed: " (prn problems))))
                     :success))]
      (set-validator! db valid?)
      db)))

(re-frame/register-handler
  :navigate-to
  (fn [db [_ uri]]
    (set! (.-href (.-location js/window)) uri)
    db))

(re-frame/register-handler
  :set-active-tab
  (fn [db [_ active-tab]]
    (assoc db :active-tab active-tab)))

(re-frame/register-handler
  :set-active-game
  (fn [db [_ active-game]]
    (assoc db :active-game active-game)))
