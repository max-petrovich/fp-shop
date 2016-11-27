(ns shop.db.repository.order-products
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.order-products :refer [order-products-protocol]]))

(deftype order-products-repository []
  common-protocol

  (get-record [this id] "")
  (get-records [this] "")
  (insert-record [this data] (insert order-products
                                     (values data)))
  (update-record [this id data] "")
  (delete-record [this id] "")

  order-products-protocol

  (get-by-order-id [this order_id] (select order-products
                                           (where {:order_id order_id})))
  )

