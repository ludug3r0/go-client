(ns go-client.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [go-client.board :as board]
              [go-client.routes :as routes]))

;; --------------------
(defn game-list-title []
  [re-com/title
   :label "Game list"
   :level :level1])

(defn link-to-game [[game-id game]]
  [re-com/hyperlink-href
   :label (:title game)
   :href (routes/game-tab {:game-id game-id})])

(defn game-list []
  (let [games (re-frame/subscribe [:game-list])]
    [re-com/v-box
     :gap "1em"
     :children (mapv link-to-game @games)]))

(defn game-list-tab []
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

(defn game-tab []
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

(defn development-tab []
  (let [server-state (re-frame/subscribe [:server-state])]
    [re-com/v-box
     :children [[state-panel @server-state]
                [:p {:on-click #(re-frame/dispatch [:send-event-to-server [:util/echo "echo"]])} "echo"]
                [:p {:on-click #(re-frame/dispatch [:send-event-to-server [:util/broadcast "broadcast"]])} "broadcast"]
                [:p {:on-click #(re-frame/dispatch [:log-into-server "rafael"])} "login"]
                [:p {:on-click #(re-frame/dispatch [:logout-from-server])} "logout"]]]))

;; --------------------
(defmulti panels identity)
(defmethod panels :game-list-tab [] [game-list-tab])
(defmethod panels :game-tab [] [game-tab])
(defmethod panels :development-tab [] [development-tab])
(defmethod panels :default-tab [] [game-list-tab])

(defmulti panels-url identity)
(defmethod panels-url :game-list-tab [] (routes/default-tab))
(defmethod panels-url :game-tab [] (routes/game-tab))
(defmethod panels-url :development-tab [] (routes/development-tab))
(defmethod panels-url :default-tab [] (routes/default-tab))


(def tabs-definition
  [{:id :development-tab :label "Development"}
   {:id :game-list-tab :label "Game List"}
   {:id :game-tab :label "Game"}])

(defn header []
  (let [selected-panel (re-frame/subscribe [:active-tab])]
    [re-com/horizontal-tabs
     :model selected-panel
     :tabs tabs-definition
     :on-change #(re-frame/dispatch [:navigate-to (panels-url %)])]))

(defn main-panel []
  (let [active-tab (re-frame/subscribe [:active-tab])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [[header]
                  (panels @active-tab)]])))
