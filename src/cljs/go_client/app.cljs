(ns go-client.app
  (:require [reagent.core :refer [atom cursor]]))

(def ^:private ear-reddening-game [[:black [16 17]]
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
                                   [:white [6 15]]])

;;global state
(defonce ^:private state (atom {:game ear-reddening-game}))

;;cursors
(def game (cursor state [:game]))