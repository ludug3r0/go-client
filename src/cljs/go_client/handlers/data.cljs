(ns go-client.handlers.data
  (:require [re-frame.core :as re-frame]
            [go-client.db :as db]))


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
