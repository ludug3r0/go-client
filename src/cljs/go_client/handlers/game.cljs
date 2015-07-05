(ns go-client.handlers.game
  (:require [re-frame.core :as re-frame]
            [taoensso.encore :as encore]))

(re-frame/register-handler
  :create-game
  re-frame/debug
  (fn [db [_ title]]
    (let [uuid (encore/uuid-str)]
      (assoc-in db [:games uuid] {:title title
                                  :moves []}))))
