(ns shop.db.protocols.comments)

(defprotocol comments-protocol
  (get-by-product-id [this id]))