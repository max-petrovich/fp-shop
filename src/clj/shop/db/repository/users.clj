(ns shop.db.repository.users
  (:require
            [korma.core :refer :all]
            [shop.db.models.users :refer :all]
            [shop.db.protocols.common :refer [common-protocol]]
            [shop.db.protocols.users :refer [users-protocol]]
            ))

(deftype users-repository []

  common-protocol

  (get-record [this id] (select users
                                (where {:id id})))

  (get-records [this] (select users))

  (insert-record [this data] (insert users
                                     (values data)))

  (update-record [this data] "str update")
  (delete-record [this id] "str delete id")

  users-protocol

  (get-by-email [this email] (select users
                                     (where {:email email})))
  )
