(ns shop.db.repository.comments
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.comments :refer [comments-protocol]]
              ))


(deftype comments-repository []
  common-protocol

  (get-record [this id] (select comments
                                (where {:id id})))
  (get-records [this] (select comments))
  (insert-record [this data] (insert comments
                                     (values data)))
  (update-record [this id data] "")
  (delete-record [this id] (delete comments
                                   (where {:id id})))

  comments-protocol

  (get-by-product-id [this id] (select comments
                                       (with users)
                                       (where {:product_id id})))

  ;(get-by-product-id [this id] (sql-only (select comments
  ;                                               (join users ))))

  )
