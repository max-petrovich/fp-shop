(ns shop.db.repository.carts
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.carts :refer [carts-protocol]]))

(deftype carts-repository []
  common-protocol

  (get-record [this id] (select category
                                (where {:id id})))

  (get-records [this] "no")

  (insert-record [this data] (insert carts
                                     (values data)))
  (update-record [this id data] "no")
  (delete-record [this id] (delete carts
                                   (where {:id id})))

  carts-protocol

  (get-by-user-id [this user_id] (select carts
                                         (with users)
                                         (with products)
                                         (where {:user_id user_id})))
  )