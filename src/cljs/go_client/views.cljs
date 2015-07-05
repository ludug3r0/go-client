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

(defn link-to-game-list []
  [re-com/hyperlink-href
   :label "go to Game List"
   :href "#/games"])

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn link-to-current-game []
  [re-com/hyperlink-href
   :label "go to Current Game"
   :href "#/games/abcdef"])

(defn home-panel []
  [re-com/v-box
   :gap "1em"
   :children [[home-title] [link-to-game-list] [link-to-about-page] [link-to-current-game]]])

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

;; --------------------
(defn game-list-title []
  [re-com/title
   :label "Game list"
   :level :level1])

(defn link-to-game [[game-id game]]
  [re-com/hyperlink-href
   :label (:title game)
   :href (str "#/games/" game-id)])

(defn game-list []
  (let [games (re-frame/subscribe [:game-list])]
    [re-com/v-box
     :gap "1em"
     :children (mapv link-to-game @games)]))

(defn game-list-panel []
  [re-com/v-box
   :gap "1em"
   :children [[game-list-title] [game-list] [link-to-home-page]]])

;; --------------------
(defn current-game-title [game-title]
  [re-com/title
   :label (str "Game: " game-title)
   :level :level1])

(defn game-board [game-id placed-stones empty-vertices]
  [board/render-game game-id placed-stones empty-vertices])

(defn game-panel []
  (let [game-id (re-frame/subscribe [:active-game-id])
        game-title (re-frame/subscribe [:active-game-title])
        placed-stones (re-frame/subscribe [:game-stones])
        ;;empty-vertices (re-frame/subscribe [:empty-vertices game-id])
        ]
    (fn []
      [re-com/v-box
       :gap "1em"
       :children [[current-game-title @game-title]
                  [game-board @game-id @placed-stones []]
                  [link-to-home-page]]])))

;; --------------------
(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :game-list [] [game-list-panel])
(defmethod panels :game-panel [] [game-panel])
(defmethod panels :default [] [:div])


(defn state-panel [{:keys [connected? logged-user]}]
  [re-com/v-box
   :children [[:p (str "connected to server: " (if connected? "Y" "N"))]
              [:p (str "user: " (or logged-user "N/A"))]]]
  )

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])
        server-state (re-frame/subscribe [:server-state])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[state-panel @server-state]
                  [:p {:on-click #(re-frame/dispatch [:send-event-to-server [:util/echo "echo"]])} "echo"]
                  [:p {:on-click #(re-frame/dispatch [:log-into-server "rafael"])} "login"]
                  [:p {:on-click #(re-frame/dispatch [:logout-from-server])} "logout"]
                  [:p {:on-click #(re-frame/dispatch [:create-game "New Game"])} "create new game"]

                  (panels @active-panel)]])))
