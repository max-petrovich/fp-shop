(ns shop.db.entities
  (:use [korma.core]))

(declare users products category carts comments delivery-types orders order-products)

(defentity users)
(defentity products)
(defentity category
           (table :categories))
(defentity carts
           (belongs-to users {:fk :user_id})
           (belongs-to products {:fk :product_id}))
(defentity comments
           (belongs-to users {:fk :user_id}))
(defentity delivery-types
           (table :delivery_types))
(defentity orders)
(defentity order-products
           (table :order_products))