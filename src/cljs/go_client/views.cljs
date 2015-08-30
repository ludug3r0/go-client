(ns go-client.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [go-client.board :as board]))

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
   :children [[game-list-title]
              [:p {:on-click #(re-frame/dispatch [:create-game "New Game"])} "Create a new game"]
              [game-list]]])

;; --------------------
(defn current-game-title [game-title]
  [re-com/title
   :label (str "Game: " game-title)
   :level :level1])

(defn game-board [game-id placed-stones empty-vertices playable-vertices]
  [board/render-game game-id placed-stones empty-vertices playable-vertices])

(defn game-panel []
  (let [game-id (re-frame/subscribe [:active-game-id])
        game-title (re-frame/subscribe [:game-title @game-id])
        placed-stones (re-frame/subscribe [:game-stones @game-id])
        empty-vertices (re-frame/subscribe [:empty-vertices @game-id])
        playable-stones (re-frame/subscribe [:playable-stones @game-id])]
    (fn []
      [re-com/v-box
       :gap "1em"
       :children [[current-game-title @game-title]
                  [game-board @game-id @placed-stones @empty-vertices @playable-stones]]])))

(defn state-panel [{:keys [connected? logged-user]}]
  [re-com/v-box
   :children [[:p (str "connected to server: " (if connected? "Y" "N"))]
              [:p (str "user: " (or logged-user "N/A"))]]])

(defn development-panel []
  (let [server-state (re-frame/subscribe [:server-state])]
    [re-com/v-box
     :children [[state-panel @server-state]
                [:p {:on-click #(re-frame/dispatch [:send-event-to-server [:util/echo "echo"]])} "echo"]
                [:p {:on-click #(re-frame/dispatch [:log-into-server "rafael"])} "login"]
                [:p {:on-click #(re-frame/dispatch [:logout-from-server])} "logout"]]]))

;; --------------------
(defmulti panels identity)
(defmethod panels :game-list [] [game-list-panel])
(defmethod panels :game-panel [] [game-panel])
(defmethod panels :development-panel [] [development-panel])
(defmethod panels :default [] [:div])


(def tabs-definition
  [{:id :development-panel :label "Development"}
   {:id :game-list :label "Game List"}
   {:id :game-panel :label "Game"}])

(defn header []
  (let [selected-panel (re-frame/subscribe [:active-panel])]
    [re-com/horizontal-tabs
     :model selected-panel
     :tabs tabs-definition
     :on-change #(re-frame/dispatch [:set-active-panel %])]))

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[header]
                  (panels @active-panel)]])))
