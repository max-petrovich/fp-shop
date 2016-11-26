(ns shop.db.repository.products
  (:require  [korma.core :refer :all]
             [shop.db.entities :refer :all]
             [shop.db.protocols.common :refer [common-protocol]]))

(deftype products-repository []
  common-protocol

  (get-record [this id] (select products
                                (where {:id id})))

  (get-records [this] (select products))

  (insert-record [this data] (insert products
                                     (values data)))

  (update-record [this id data] (update products
                                        (set-fields data)
                                        (where {:id id})))

  (delete-record [this id] (delete products
                                   (where {:id id})))
  )
