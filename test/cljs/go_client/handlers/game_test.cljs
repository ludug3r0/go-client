(ns go-client.handlers.game-test
  (:require-macros [cemerick.cljs.test :refer [is deftest with-test run-tests testing test-var]])
  (:require [cemerick.cljs.test]
            [go-client.db :as db]
            [go-client.handlers.game :as game]
            [clojure.set :as set]))

(def starting-db db/default-db)

(deftest creating-a-new-game
         (let [starting-number-of-games (count (:games starting-db))
               expected-number-of-games (inc starting-number-of-games)
               changed-db (game/create-game starting-db ["title"])]
           (is (= expected-number-of-games (count (:games changed-db))))))

(deftest game-title
         (let [starting-game-keys (set (keys (:games starting-db)))
               changed-db (game/create-game starting-db ["New game title"])
               changed-game-keys (set (keys (:games changed-db)))
               created-game-key (first (set/difference changed-game-keys starting-game-keys))]
           (is (= "New game title" (get-in changed-db [:games created-game-key :title])))))

