(ns go-client.handlers.game-test
  (:require-macros [cemerick.cljs.test :refer [is deftest with-test run-tests testing test-var]])
  (:require [cemerick.cljs.test]
            [go-client.db :as db]
            [go-client.handlers.game :as game]))

(def starting-db db/default-db)

(deftest creating-a-new-game
         (let [starting-number-of-games (count (:games starting-db))
               expected-number-of-games (inc starting-number-of-games)
               changed-db (game/create-game starting-db ["title"])]
           (is (= expected-number-of-games (count (:games changed-db))))))

