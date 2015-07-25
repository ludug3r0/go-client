(ns go-client.handlers.game
  (:require [re-frame.core :as re-frame]
            [taoensso.encore :as encore]
            [go.game :as game]))

(defn create-game [db [title]]
  (let [uuid (encore/uuid-str)
        starting-moves []]
    (assoc-in db [:games uuid] {:title           title
                                :moves           starting-moves
                                :empty-vertices  (set (game/empty-vertices starting-moves))
                                :playable-stones (set [])})))

(re-frame/register-handler :create-game re-frame/trim-v create-game)

(re-frame/register-handler
  :resolve-vertex
  (fn [db [_ game-id vertex]]
    (let [game (get-in db [:games game-id :moves])
          current-player (game/current-player-color game)]
      (if (game/playable-vertex? game vertex)
        (-> db
            (update-in [:games game-id :playable-stones] conj [current-player vertex])
            (update-in [:games game-id :empty-vertices] disj vertex))
        (-> db
            (update-in [:games game-id :empty-vertices] disj vertex))))))

(re-frame/register-handler
  :occupy-vertex
  (fn [db [_ game-id [_ vertex]]]
    (let [current-game-state (get-in db [:games game-id :moves])
          next-game-state (game/occupy-vertex current-game-state vertex)]
      (-> db
          (assoc-in [:games game-id :moves] next-game-state)
          (assoc-in [:games game-id :empty-vertices] (set (game/empty-vertices next-game-state)))
          (assoc-in [:games game-id :playable-stones] (set []))))))