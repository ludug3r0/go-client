(ns go-client.db
  (:require [go.game :as game]
            [go.schema :as go-schema]
            [schema.core :as s :include-macros true]))

(defn uuid4-match [s]
  (re-matches #"[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}" s))

(s/defschema db-schema
  {:name         s/Str
   :active-game  (s/maybe s/Str)
   :active-panel (s/maybe (s/enum :home-panel :about-panel :game-list :game-panel))
   :games        {(s/pred uuid4-match) {:title           s/Str
                                        :moves           go-schema/game
                                        :empty-vertices  #{go-schema/vertex}
                                        :playable-stones #{go-schema/move}}}
   :server       {:open? s/Bool
                  :uid   (s/either (s/eq :taoensso.sente/nil-uid) s/Str)}})

(def default-db
  (let [ear-reddening-game-moves [[:black [16 17]]
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
                                  [:white [6 15]]]
        ear-reddening-game {:title "Ear reddening Game"
                            :moves ear-reddening-game-moves
                            :empty-vertices (set (game/empty-vertices ear-reddening-game-moves))
                            :playable-stones #{}}]
    {:name         "re-frame"
     :active-game  nil
     :active-panel :home-panel
     :games       {"4f2b7fe1-5908-4533-b018-05143dbd20ca" ear-reddening-game}
     :server      {:open? false
                   :uid   :taoensso.sente/nil-uid}}))
