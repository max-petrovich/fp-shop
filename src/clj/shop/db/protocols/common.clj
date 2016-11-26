(ns shop.db.protocols.common)

(defprotocol common-protocol
  (get-record [this id])
  (get-records [this])
  (insert-record [this data])
  (update-record [this id data])
  (delete-record [this id]))