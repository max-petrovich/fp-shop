(ns shop.controllers.checkout-controller
  (:use [shop.db.protocols.common]
        [shop.db.protocols.carts])
  (:require [shop.layout :as layout]
            [ring.util.http-response :as response]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]
            [shop.request-validators :as rv]
            [shop.db.repository.carts :refer :all]
            [shop.db.repository.orders :refer :all]
            [shop.db.repository.delivery-types :refer :all]
            [shop.db.repository.order-products :refer :all]
            ))

(def carts-repository (->carts-repository))
(def orders-repository (->orders-repository))
(def delivery-types-repository (->delivery-types-repository))
(def order-products-repository (->order-products-repository))


(defn index [{:keys [identity params flash] :as request}]
  (let [cart (get-by-user-id carts-repository (:id identity))]
    (if-not (empty? cart)
      (let [cart-total (reduce + (map #(:price %) cart))]
        (layout/render "checkout.html" (merge {}
                                              {:cart cart
                                               :cart-total cart-total
                                               :delivery_types (get-records delivery-types-repository)
                                               :flash flash
                                               })))
      (layout/render "404.html" {:status "oops.." :title "Your cart is empty" :text "Add products, please ;)"}))))


(defn store [{:keys [identity params flash] :as request}]
  (let [errors (rv/validate-checkout-form params)]
    (if errors
      (-> (response/found "/checkout")
          (assoc :flash (assoc params :errors errors)))
      (let [cart (get-by-user-id carts-repository (:id identity))
            cart-total (reduce + (map #(:price %) cart))]
        (let [new-order-id (:generated_key (insert-record orders-repository (merge {:user_id (:id identity)
                                                                                     :date (tc/to-sql-time (java.util.Date.))
                                                                                     :price cart-total}
                                                                                    (select-keys params [:email :phone :address :comment :delivery_type_id :first_name :last_name]))))]
          (do
            ; order products
            (dorun (map #(insert-record order-products-repository {:order_id new-order-id :product_id (:id %)}) cart))
            ; clear user cart
            (truncate-user-cart carts-repository (:id identity))
            ; result
            (-> (response/found "/")
                (assoc :flash {:message "Your order successfully placed. Thank you!"}))
            ))
        )
      )))