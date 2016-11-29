(ns shop.controllers.cart-controller
  (:use [shop.db.protocols.common]
        [shop.db.protocols.carts])
  (:require [shop.layout :as layout]
            [ring.util.http-response :as response]
            [shop.db.repository.carts :refer :all]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]))

(def carts-repository (->carts-repository))


(defn index [request]
  (let [cart (get-by-user-id carts-repository (:id (:identity request)))
        cart-total (if (count cart)
                     (reduce + (map #(:price %) cart)))]
    (layout/render "cart.html" (merge {:cart cart
                                       :cart-total cart-total}
                                      (:flash request))))
  )

(defn add-product [{:keys [params] :as request}]
  (insert-record carts-repository {:user_id (:id (:identity request)) :product_id (:product_id params) :date (tc/to-sql-time (java.util.Date.))})
  (->  (response/found "/cart")
       (assoc :flash {:message "Item added"}))
  )

(defn remove-product [{:keys [params] :as request}]
  (delete-record carts-repository (:id params))
  (->  (response/found "/cart")
       (assoc :flash {:message "Item removed"}))
  )
