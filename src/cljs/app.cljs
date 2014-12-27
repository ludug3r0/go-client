(ns app
  (:require [reagent.core :as reagent]
            [board :as board]))

;;global state
(def state
  (reagent/atom
    {:game [[:black [16 16]]
            [:white [16 15]]]}))

;;cursors
(def game (reagent/cursor [:game] state))

(defn ^:export run []
      (reagent/render-component
        [board/render game]
        (.-body js/document)))