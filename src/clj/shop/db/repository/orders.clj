(ns shop.db.repository.orders
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.orders :refer [orders-protocol]]))

(deftype orders-repository []
  common-protocol

  (get-record [this id] "")
  (get-records [this] "")
  (insert-record [this data] (insert orders
                                     (values data)))
  (update-record [this id data] "")
  (delete-record [this id] "")

  orders-protocol
  (get-by-user-id [this user_id] "")
  )
