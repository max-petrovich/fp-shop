(ns shop.db.repository.delivery-types
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.repository.common :refer [common-repository]]))

(deftype delivery-types-repository [])

(extend delivery-types-repository
  common-protocol
  (common-repository delivery-types))