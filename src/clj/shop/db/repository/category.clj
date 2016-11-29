(ns shop.db.repository.category
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.protocols.category :refer [category-protocol]]
              [shop.db.repository.common :refer [common-repository]]))

(deftype category-repository []
  category-protocol

  (get-hierarchy-format [this]
    (let [h (select category
                    (order :id :ASC))]
      (for [x (filter #(= (:parent_id %) 0) h)]
        (assoc x :childs (filter #(= (:parent_id %) (:id x))  h))))))

(extend category-repository
  common-protocol
  (common-repository category))