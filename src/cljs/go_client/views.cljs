(ns go-client.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [go-client.board :as board]))

;; --------------------
(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "Hello from " @name ". This is the Home Page.")
       :level :level1])))

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn link-to-current-game []
  [re-com/hyperlink-href
   :label "go to Current Game"
   :href "#/current-game"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title] [link-to-about-page] [link-to-current-game]]])

;; --------------------
(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])  

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])

(defn current-game-title []
  [re-com/title
   :label "This is the current game"
   :level :level1])

(defn current-game-board [current-game]
  [board/render-game current-game])

(defn current-game-panel []
  (let [current-game (re-frame/subscribe [:current-game])]
    (fn []
      [re-com/v-box
       :gap "1em"
       :children [[current-game-title] [current-game-board current-game] [link-to-home-page]]])))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :current-game-panel [] [current-game-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [(panels @active-panel)]])))
