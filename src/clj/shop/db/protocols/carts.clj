(ns shop.db.protocols.carts)

(defprotocol carts-protocol
  (get-by-user-id [this user_id])
  (truncate-user-cart [this user_id]))