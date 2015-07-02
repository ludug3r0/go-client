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
   :href "#/game/abcdef"])

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

(defn current-game-title [game-id]
  [re-com/title
   :label (str "Game:" game-id)
   :level :level1])

(defn game-board [game-id placed-stones empty-vertices]
  [board/render-game game-id placed-stones empty-vertices])

(defn game-panel []
  (let [game-id (re-frame/subscribe [:active-game-id])
        placed-stones (re-frame/subscribe [:game-stones])
        ;;empty-vertices (re-frame/subscribe [:empty-vertices game-id])
        ]
    (fn []
      [re-com/v-box
       :gap "1em"
       :children [[current-game-title @game-id]
                  [game-board @game-id @placed-stones []]
                  [link-to-home-page]]])))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :game-panel [] [game-panel])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        connected? (re-frame/subscribe [:server-connected?])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[:p (if @connected? ":)" ":(")]
                  [:p {:on-click #(re-frame/dispatch [:send-to-server [:util/echo "test"]])} "test"]
                  (panels @active-panel)]])))
