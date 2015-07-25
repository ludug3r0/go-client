(ns go-client.handlers.game
  (:require-macros [cemerick.cljs.test :refer [is deftest with-test run-tests testing test-var]])
  (:require [cemerick.cljs.test]
            [go-client.db :as db]))

(deftest testing-the-test
         (is (= true true)))

(deftest testing-the-failing-test
         (is (= false true)))
