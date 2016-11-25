(ns shop.db.protocols.users)

(defprotocol users-protocol
  (get-by-email [this email]))