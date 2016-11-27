(ns shop.db.protocols.order-products)

(defprotocol order-products-protocol
  (get-by-order-id [this order_id]))