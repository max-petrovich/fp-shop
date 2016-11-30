(ns shop.controllers.products-controller
  (:use [shop.db.protocols.common]
        [shop.db.protocols.category]
        [shop.db.protocols.comments])
  (:require [shop.layout :as layout]
            [ring.util.http-response :as response]
            [shop.db.repository.users :refer :all]
            [shop.db.repository.products :refer :all]
            [shop.db.repository.category :refer :all]
            [shop.db.repository.comments :refer :all]
            [shop.request-validators :as rv]
            [me.raynes.fs :as fs]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]))

(def category-repository (->category-repository))
(def products-repository (->products-repository))
(def comments-repository (->comments-repository))

(declare upload-file)

(defn index []
  (layout/render "product/index.html" {:products (get-records products-repository)}))

(defn create [request]
  (layout/render "product/add.html" {:category-hierarchy (get-hierarchy-format category-repository) :flash (:flash request)})
  )

(defn store [{:keys [params] :as request}]
  (let [errors (rv/validate-product-form params)]
    (if errors
      (-> (response/found "/product/add")
          (assoc :flash (assoc params :errors errors)))
      (do
        (insert-record products-repository (merge {:user_id (:id (:identity request))
                                                    :date (tc/to-sql-time /(java.util.Date.))
                                                    :photo (clojure.string/replace (upload-file (:photo params)) #"resources/public/" "")}
                                                   (select-keys params [:category_id :price :article :title :description])))
        (-> (response/found "/")
            (assoc :flash {:message "Product successfully added!"}))
        )
      ))
  )

(defn show [id request]
  (let [row (first (get-record products-repository id))]
    (if-not (empty? row)
      (do
        (let [category (first (get-record category-repository (:category_id row)))
              comments (get-by-product-id comments-repository (:id row))]
          (layout/render "product/show.html" {:row row :category category :comments comments :flash (:flash request)})
          )))))

(defn edit [id request]
  (if-let [product (first (get-record products-repository id))]
    (layout/render "product/edit.html" {:category-hierarchy (get-hierarchy-format category-repository)
                                        :product product
                                        :flash (:flash request)}))
  )

(defn update [id {:keys [params] :as request}]
  (if-let [errors (rv/validate-product-form params)]
    (-> (response/found (str "/product/" id "/edit"))
        (assoc :flash (assoc params :errors errors)))
    (do
      (update-record products-repository id (select-keys params [:category_id :price :article :title :description]))
      (-> (response/found "/")
          (assoc :flash {:message "Product successfully updated!"})))))


(defn comments-store [product_id {:keys [params] :as request}]
  (if-let [errors (rv/validate-comments-form params)]
    (-> (response/found (str "/product/" product_id))
        (assoc :flash (assoc params :comments-errors errors)))
    (let [row (first (get-record products-repository product_id))]
      (do
        (insert-record comments-repository {:product_id product_id
                                             :user_id (:id (:identity request))
                                             :text (:text params)
                                             :date (tc/to-sql-time (java.util.Date.))})
        ; inc comments num
        (update-record products-repository product_id {:comments_num (+ 1 (:comments_num row))})

        (-> (response/found (str "/product/" product_id))
            (assoc :flash (assoc params :message "Commen added")))
        ))))
;;---------------------------------------------
;;---------------------------------------------

(defn upload-file [file]
  (if (> (:size file) 0)
    (fs/copy (:tempfile file) (str "resources/public/storage/photos/" (tc/to-long (t/now)) "_" (:filename file)))))