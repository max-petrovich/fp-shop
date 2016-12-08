(ns shop.db.protocols.products)

(defprotocol products-protocol
  (get-by-ids [this ids]))