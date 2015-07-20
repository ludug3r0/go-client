(ns go-client.board
  (:require [reagent.core :refer [atom]]
            [re-frame.core :as re-frame]))

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

(defn- render-vertex
  [game-id vertex]
  (let [line (-> vertex first str)
        column (-> vertex second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "vertex"
                 (str "line-" line)
                 (str "column-" column)]))
     {:on-mouse-over #(re-frame/dispatch [:resolve-vertex game-id vertex])}]))

(defn- render-ghost-stone
  [game-id stone]
  (let [color (-> stone first name)
        coordinates (-> stone second)
        line (-> coordinates first str)
        column (-> coordinates second str)]
    [(keyword (clojure.string/join
                "."
                ["div"
                 "placement"
                 color
                 (str "line-" line)
                 (str "column-" column)]))
     {:on-click #(re-frame/dispatch [:occupy-vertex game-id stone])}]))

(defn- render-placed-stones [stones]
  [:div
   (for [stone stones]
     ^{:key (second stone)}
     [render-stone stone])])

(defn- render-empty-vertices [game-id vertices]
  [:div
   (for [vertex vertices]
     ^{:key vertex}
     [render-vertex game-id vertex])])

(defn- render-playable-stones [game-id stones]
  [:div
   (for [stone stones]
     ^{:key stone}
     [render-ghost-stone game-id stone])])

(defn render-game
  [game-id placed-stones empty-vertices playable-stones]
  [:div.board
   [render-placed-stones placed-stones]
   [render-empty-vertices game-id empty-vertices]
   [render-playable-stones game-id playable-stones]])