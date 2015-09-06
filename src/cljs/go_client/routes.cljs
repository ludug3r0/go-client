(ns go-client.routes
  (:require-macros [secretary.core :refer [defroute]])
  (:import goog.History)
  (:require [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as EventType]
            [re-frame.core :as re-frame]))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      EventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn app-routes []
  (secretary/set-config! :prefix "#")
  ;; --------------------
  ;; define routes here
  (defroute default "/" []
            (re-frame/dispatch [:set-active-tab :game-list]))

  (defroute game-list "/games" []
            (do
              (.log js/console "Oh games")
              (re-frame/dispatch [:set-active-tab :game-list])))

  (defroute development "/development" []
            (do
              (.log js/console "Oh dev")
              (re-frame/dispatch [:set-active-tab :development-panel])))

  (defroute game "/games/:game-id" [game-id]
            (re-frame/dispatch [:set-active-game game-id])
            (re-frame/dispatch [:set-active-tab :game-panel]))

  ;; --------------------
  (hook-browser-navigation!))
