(ns go-client.board
  (:require [go.game :as game]
            [reagent.core :refer [atom]]))

(defn- render-stone
  [stone]
  (let [vertex (second stone)
        color (-> stone first name)
        line (-> vertex first str)
        column (-> vertex second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "stone"
                 color
                 (str "line-" line)
                 (str "column-" column)]))]))

(defn- set-vertex-as-playable [vertex-data vertex]
  (swap! vertex-data update-in [:empty] disj vertex)
  (swap! vertex-data update-in [:playable] conj vertex))

(defn- set-vertex-as-non-playable [vertex-data vertex]
  (swap! vertex-data update-in [:empty] disj vertex))

(defn- render-vertex
  [game vertex-data vertex]
  (let [line (-> vertex first str)
        column (-> vertex second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "vertex"
                 (str "line-" line)
                 (str "column-" column)]))
     {:on-mouse-over #(if (game/playable-vertex? @game vertex)
                       (set-vertex-as-playable vertex-data vertex)
                       (set-vertex-as-non-playable vertex-data vertex))}]))

(defn- render-playable-stone
  [game vertex]
  (let [current-player (-> @game game/current-player-color name)
        line (-> vertex first str)
        column (-> vertex second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "placement"
                 current-player
                 (str "line-" line)
                 (str "column-" column)]))
     {:on-click #(swap! game game/occupy-vertex vertex)}]))

(defn- render-placed-stones [stones]
  [:div
   (for [stone stones]
     ^{:key (second stone)}
     [render-stone stone])])

(defn- render-empty-vertices [game vertex-data]
  [:div
   (for [vertex (:empty @vertex-data)]
     ^{:key vertex}
     [render-vertex game vertex-data vertex])])

(defn- render-playable-vertices [game vertex-data]
  [:div
   (for [vertex (:playable @vertex-data)]
     ^{:key vertex}
     [render-playable-stone game vertex])])

(defn render-game
  [game-id placed-stones empty-vertices]
  ;; TODO insert back hover functionality
  [:div.board
   [render-placed-stones placed-stones]
   #_[render-empty-vertices game-id empty-vertices]])