(ns board
  (:require [go.game :as game]))

(defn- render-placement
       [{:keys [color vertex line column]}]
       [(keyword (clojure.string/join
                   "."
                   ["div"
                    "stone"
                    color
                    (str "line-" line)
                    (str "column-" column)]))])

(defn- render-ghost
       [{:keys [color vertex line column]}]
       [(keyword (clojure.string/join
                   "."
                   ["div"
                    "stone"
                    (str "ghost-" color)
                    (str "line-" line)
                    (str "column-" column)]))])

(defn- placement [stone]
      (let [vertex (second stone)]
           {:color  (-> stone first name)
            :vertex vertex
            :line   (-> vertex first str)
            :column (-> vertex second str)}))


(defn render
      [game]
      (let [configuration (game/configuration @game)
            placements (map placement configuration)
            empty-placements (map placement (game/empty-placements @game))]
           [:div.board
            (concat
              (mapv render-placement placements)
              (mapv render-ghost empty-placements))]))