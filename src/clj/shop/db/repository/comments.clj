(ns shop.db.repository.comments
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.comments :refer [comments-protocol]]
              [shop.db.repository.common :refer [common-repository]]
              ))


(deftype comments-repository []
  comments-protocol

  (get-by-product-id [this id] (select comments
                                       (with users)
                                       (where {:product_id id})))

  )

(extend comments-repository
  common-protocol
  (common-repository comments))
