(ns go-client.core
    (:require [reagent.core :as reagent]
              [re-frame.core :as re-frame]
              [go-client.handlers]
              [go-client.subs]
              [go-client.routes :as routes]
              [go-client.views :as views]))

(defn mount-root []
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn ^:export init [] 
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (mount-root))
