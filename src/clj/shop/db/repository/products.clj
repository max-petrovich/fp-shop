(ns shop.db.repository.products
  (:require  [shop.db.entities :refer :all]
             [shop.db.protocols.common :refer [common-protocol]]
             [shop.db.repository.common :refer [common-repository]]))

(deftype products-repository [])

(extend products-repository
  common-protocol
  (common-repository products))