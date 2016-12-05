(ns shop.controllers.test-controller
  (:use [maxic.core]
        [maxic.emit])
  (:require [shop.config :refer [env]]))


(def db (:db-spec env))

(defn index []
  (str db)
  (str (fetch-all db (select
                       (from :products)
                       (join-left :users (= :products.user_id :users.id))
                       (order :products.id :desc)
                       (limit 2))))
  )