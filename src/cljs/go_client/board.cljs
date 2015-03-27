(ns go-client.board
  (:require [go.game :as game]))

(defn- render-stone
  [stone]
  (let [vertex (second stone)
        color (-> stone first name)
        line (-> vertex first str)
        column (-> vertex second str)]
    ^{:key [line column]}
    [(keyword (clojure.string/join
                "."
                ["div"
                 "stone"
                 color
                 (str "line-" line)
                 (str "column-" column)]))]))

(defn- render-empty-vertex
  [game vertex]
  (let [line (-> vertex first str)
        column (-> vertex second str)]
    ^{:key [line column]}
    [(keyword (clojure.string/join
                "."
                ["div"
                 "vertex"
                 (str "line-" line)
                 (str "column-" column)]))
     {:on-click #(swap! game game/occupy-vertex vertex)}]))

(defn render
  [game]
  (let [configuration (game/configuration @game)
        empty-vertices (game/empty-vertices @game)]
    [:div.board
     (concat
       (mapv render-stone configuration)
       (mapv (partial render-empty-vertex game) empty-vertices))]))