(ns board
  (:require [go.game :as game]))

(defn- render-placement
  [placement]
  (let [color (first placement)
        vertex (second placement)
        line (-> vertex first str)
        column (-> vertex second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "stone"
                 (name color)
                 (str "line-" line)
                 (str "column-" column)]))]))

(defn- render-placements
  [game]
  (for [placement (game/configuration game)]
    [render-placement placement]))


;;TODO: move to game namespace?
(defn- place-stone
  [game placement]
  (swap! game conj placement))

(defn- render-ghost
  [game placement]
  (let [color (first placement)
        vertex (second placement)
        line (-> vertex first str)
        column (-> vertex second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "stone"
                 (str "ghost-"(name color))
                 (str "line-" line)
                 (str "column-" column)]))
     {:on-click #(place-stone game placement)}]))

(defn render-ghosts
  [game]
  (for [valid-placement [[:black [10 10]]
                         [:white [10 9]]]]
    [render-ghost game valid-placement]))

(defn render
  [game]
  [:div.board
   (concat
     (render-placements @game)
     (render-ghosts game))])