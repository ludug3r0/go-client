(ns hello
  (:require [reagent.core :as reagent]))

(defn ^:export run []
      (reagent/render-component
        [:p "Hello"]
        (.-body js/document)))