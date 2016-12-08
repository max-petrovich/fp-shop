(ns shop.db.repository.products
  (:require   [korma.core :refer :all]
              [shop.db.entities :refer :all]
              [shop.db.protocols.common :refer [common-protocol]]
              [shop.db.repository.common :refer [common-repository]]
              [shop.db.protocols.products :refer [products-protocol]]
              [clojure.java.jdbc :as jdbc]))

(def sphinx-db-spec {:classname "com.mysql.jdbc.Driver", :subprotocol "mysql", :subname "//127.0.0.1:9306"})

(deftype products-repository []
  common-protocol

  (get-record [this id] (select products
                                (where {:id id})))

  (get-records [this] (select products
                              (order :id :DESC)))

  (insert-record [this data] (let [newRecord (insert products
                                                     (values data))]
                               (jdbc/insert! sphinx-db-spec :rtProducts
                                 {
                                  :id (:generated_key newRecord)
                                  :title (:title data)
                                  :description (:description data)
                                  })
                               ))

  (update-record [this id data] (do
                                  (update products
                                          (set-fields data)
                                          (where {:id id}))
                                  (jdbc/update! sphinx-db-spec :rtProducts
                                                {
                                                 :title (:title data)
                                                 :description (:description data)
                                                 }
                                                ["id = ?" (:id id)])
                                  ))

  (delete-record [this id] (delete products
                                   (where {:id id})))

  products-protocol
  (get-by-ids [this ids] (select products
                                 (where {:id [in ids]}))))
