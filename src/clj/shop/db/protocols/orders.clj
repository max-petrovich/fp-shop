(ns shop.db.protocols.orders)

(defprotocol orders-protocol
  (get-by-user-id [this user_id]))