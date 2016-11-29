(ns shop.db.repository.carts
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.carts :refer [carts-protocol]]
              [shop.db.repository.common :refer [common-repository]]))

(deftype carts-repository []
  carts-protocol

  (get-by-user-id [this user_id] (select carts
                                         (with users)
                                         (with products)
                                         (where {:user_id user_id})))

  (truncate-user-cart [this user_id] (delete carts
                                             (where {:user_id user_id})))
  )

(extend carts-repository
  common-protocol
  (common-repository carts))