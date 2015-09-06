(ns go-client.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]
            [go.game :as game]))

(re-frame/register-sub
  :name
  (fn [db _]
    (reaction (:name @db))))


(re-frame/register-sub
  :server-state
  (fn [db _]
    (reaction {:connected?  (-> @db :server :open?)
               :logged-user (if (-> @db :server :uid string?)
                              (-> @db :server :uid)
                              nil)})))

(re-frame/register-sub
  :active-game-id
  (fn [db _]
    (reaction
      (:active-game @db))))

(re-frame/register-sub
  :game-title
  (fn [db [_ game-id]]
    (reaction
      (get-in @db [:games game-id :title]))))

(re-frame/register-sub
  :game-stones
  (fn [db [_ game-id]]
    (reaction
      (let [game-moves (get-in @db [:games game-id :moves])]
        (if game-moves
          (game/configuration game-moves)
          #{})))))

(re-frame/register-sub
  :empty-vertices
  (fn [db [_ game-id]]
    (reaction
      (get-in @db [:games game-id :empty-vertices]))))

(re-frame/register-sub
  :playable-stones
  (fn [db [_ game-id]]
    (reaction
      (get-in @db [:games game-id :playable-stones]))))

(re-frame/register-sub
  :game-list
  (fn [db _]
    (reaction (:games @db))))

(re-frame/register-sub
  :active-tab
  (fn [db _]
    (reaction (:active-tab @db))))