(ns go-client.db
  (:require [go.game :as game]
            [go.schema :as go-schema]
            [schema.core :as s :include-macros true]))

(s/defschema db-schema
  {:name         s/Str
   :active-game  (s/maybe s/Str)
   :active-panel (s/maybe (s/enum :home-panel :about-panel :game-list :game-panel))
   :games        {s/Str {:title           s/Str
                         :moves           go-schema/game
                         :empty-vertices  #{go-schema/vertex}
                         :playable-stones #{go-schema/move}}}
   :server       {:open? s/Bool
                  :uid   (s/either (s/eq :taoensso.sente/nil-uid) s/Str)}})

(def default-db
  (let [ear-reddening-game [[:black [16 17]]
                            [:white [17 4]]
                            [:black [3 16]]
                            [:white [17 15]]
                            [:black [4 3]]
                            [:white [14 3]]
                            [:black [4 5]]
                            [:white [5 17]]
                            [:black [15 16]]
                            [:white [4 14]]
                            [:black [5 16]]
                            [:white [4 16]]
                            [:black [4 15]]
                            [:white [4 17]]
                            [:black [3 15]]
                            [:white [5 15]]
                            [:black [6 16]]
                            [:white [3 17]]
                            [:black [3 14]]
                            [:white [6 15]]]]
    {:name        "re-frame"
     :active-game nil
     :active-panel :home-panel
     :games       {"abcdef" {:title           "Ear reddening Game"
                             :moves           ear-reddening-game
                             :empty-vertices  (set (game/empty-vertices ear-reddening-game))
                             :playable-stones (set [[:black [1 1]]])}}
     :server      {:open? false
                   :uid   :taoensso.sente/nil-uid}}))
