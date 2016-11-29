(ns shop.db.repository.order-products
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.order-products :refer [order-products-protocol]]
              [shop.db.repository.common :refer [common-repository]]))

(deftype order-products-repository []
  order-products-protocol

  (get-by-order-id [this order_id] (select order-products
                                           (where {:order_id order_id})))
  )


(extend order-products-repository
  common-protocol
  (common-repository order-products))