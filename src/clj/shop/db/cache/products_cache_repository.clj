(ns shop.db.cache.products-cache-repository
  (:use [shop.db.protocols.common]
        [shop.db.repository.common]
        [shop.db.repository.products]))

(def products-repository (->products-repository))

(def cache (atom {}))

(deftype products-cache-repository []
  common-protocol

  (get-records [this] (let [records @cache]
                        (if (empty? records)
                          (let [from-db (reduce #(assoc %1 (keyword (str (:id %2))) %2) {} (get-records products-repository))]
                            (reset! cache from-db)
                            (vals from-db)
                            )
                          (vals records))))

  (get-record [this id] (let [cached ((keyword (str id)) @cache)]
                          (if (nil? cached)
                            (let [from-db (get-record products-repository id)]
                              (swap! cache conj {(keyword (str id)) (first from-db)})
                              (first from-db))
                            cached)))

  (insert-record [this data] (let [id (:generated_key (insert-record products-repository data))]
                               (swap! cache conj (assoc data :id id))
                               id))
  (update-record [this id data]
      (update-record products-repository id data)
      (swap! cache assoc (keyword (str id)) (merge ((keyword (str id)) @cache) data)))

  (delete-record [this id]
      (delete-record products-repository id)
      (swap! cache dissoc (keyword (str id)))))