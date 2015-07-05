(ns go-client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [go.game :as game]))

(re-frame/register-sub
  :name
  (fn [db]
    (reaction (:name @db))))


(re-frame/register-sub
  :server-state
  (fn [db]
    (reaction {:connected?  (-> @db :server :open?)
               :logged-user (if (-> @db :server :uid string?)
                              (-> @db :server :uid)
                              nil)})))

(re-frame/register-sub
  :game-stones
  (fn [db]
    (let [game-id (reaction (:active-game @db))]
      (reaction
        (let [game-moves (get-in @db [:games @game-id :moves])]
          (if game-moves
            (game/configuration game-moves)
            #{}))))))

(re-frame/register-sub
  :game-list
  (fn [db]
    (reaction (:games @db))))

(re-frame/register-sub
  :active-game-id
  (fn [db]
    (reaction
      (:active-game @db))))

(re-frame/register-sub
  :active-game-title
  (let [active-game-id (re-frame/subscribe [:active-game-id])]
    (fn [db]
      (reaction
        (get-in @db [:games @active-game-id :title])))))


(re-frame/register-sub
  :active-panel
  (fn [db _]
    (reaction (:active-panel @db))))