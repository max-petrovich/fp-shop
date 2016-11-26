(ns shop.db.protocols.category)

(defprotocol category-protocol
  (get-hierarchy-format [this]))