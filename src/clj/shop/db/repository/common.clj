(ns shop.db.repository.common
  (:use [korma.core]))

(defn common-repository [entity]
  {
   :get-record (fn [this id] (select entity
                                  (where {:id id})))
   :get-records (fn [this] (select entity
                                   (order :id :DESC)))
   :insert-record (fn [this data] (insert entity
                                     (values data)))
   :update-record (fn [this id data] (update entity
                                     (set-fields data)
                                     (where {:id id})))
   :delete-record (fn [this id] (delete entity
                                     (where {:id id})))
   })