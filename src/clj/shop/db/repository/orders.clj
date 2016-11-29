(ns shop.db.repository.orders
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.orders :refer [orders-protocol]]
              [shop.db.repository.common :refer [common-repository]]))

(deftype orders-repository []
  orders-protocol
  (get-by-user-id [this user_id] "")
  )

(extend orders-repository
  common-protocol
  (common-repository orders))