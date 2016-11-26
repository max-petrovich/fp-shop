(ns shop.db.repository.category
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.category :refer [category-protocol]]))

(deftype category-repository []
  common-protocol

  (get-record [this id] (select category
                                (where {:id id})))

  (get-records [this] (select category))

  (insert-record [this data] "todo")
  (update-record [this id data] "todo")
  (delete-record [this id] "todo")

  category-protocol

  (get-hierarchy-format [this]
    (let [h (select category
                    (order :id :ASC))]
      (for [x (filter #(= (:parent_id %) 0) h)]
        (assoc x :childs (filter #(= (:parent_id %) (:id x))  h))))))
