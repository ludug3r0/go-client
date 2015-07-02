(ns go-client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [go.game :as game]))

(re-frame/register-sub
  :name
  (fn [db]
    (reaction (:name @db))))


(re-frame/register-sub
  :server-connected?
  (fn [db]
    (reaction (:server-connected? @db))))

(re-frame/register-sub
  :game-stones
  (fn [db]
    (let [game-id (reaction (:active-game @db))]
      (reaction
        (let [game (get-in @db [:games @game-id])]
          (if game
            (game/configuration game)
            #{}))))))

(re-frame/register-sub
  :active-game-id
  (fn [db]
    (reaction
      (:active-game @db))))

(re-frame/register-sub
  :active-panel
  (fn [db _]
    (reaction (:active-panel @db))))