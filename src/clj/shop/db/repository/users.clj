(ns shop.db.repository.users
  (:require
            [korma.core :refer :all]
            [shop.db.entities :refer :all]
            [shop.db.protocols.common :refer [common-protocol]]
            [shop.db.protocols.users :refer [users-protocol]]
            [shop.db.repository.common :refer [common-repository]]))

(deftype users-repository []
  users-protocol

  (get-by-email [this email] (select users
                                     (where {:email email})))
  )

(extend users-repository
  common-protocol
  (common-repository users))