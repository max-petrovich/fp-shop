(ns shop.db.repository.delivery-types
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]))

(deftype delivery-types-repository []
  common-protocol

  (get-record [this id] "")
  (get-records [this] (select delivery-types))
  (insert-record [this data] "")
  (update-record [this id data] "")
  (delete-record [this id] ""))
