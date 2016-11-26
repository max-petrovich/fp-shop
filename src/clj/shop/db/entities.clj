(ns shop.db.entities
  (:use [korma.core]))

(declare users products category carts comments delivery-types orders order-products)

(defentity users)
(defentity products)
(defentity category
           (table :categories))
(defentity carts)
(defentity comments)
(defentity delivery-types
           (table :delivery_types))
(defentity orders)
(defentity order-products
           (table :order_products))

;(defentity users
;           (has-many products))
;(defentity products
;           (has-one users)
;           (has-one category))
;(defentity category
;           (has-many products))
;(defentity carts)
;(defentity comments
;           (has-one products)
;           (has-one users))
;(defentity delivery-types)
;(defentity orders
;           (has-one users)
;           (has-one delivery-types))
;(defentity order-products
;           (has-many orders)
;           (has-many products))
