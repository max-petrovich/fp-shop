(ns shop.controllers.products-controller
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

(def users-repository (->users-repository))
(def category-repository (->category-repository))
(def products-repository (->products-repository))
(def comments-repository (->comments-repository))

(declare upload-file)

(defn index []
  (layout/render "product/index.html" {:products (.get-records products-repository)}))

(defn create [request]
  (layout/render "product/add.html" {:category-hierarchy (.get-hierarchy-format category-repository) :flash (:flash request)})
  )

(defn store [{:keys [params] :as request}]
  (let [errors (rv/validate-product-form params)]
    (if errors
      (-> (response/found "/product/add")
          (assoc :flash (assoc params :errors errors)))
      (do
        (def filepath (upload-file (:photo params)))
        (.insert-record products-repository (merge {:user_id (:id (:identity request))
                                                    :date (tc/to-sql-time (java.util.Date.))
                                                    :photo (clojure.string/replace filepath #"resources/public/" "")}
                                                   (select-keys params [:category_id :price :article :title :description])))
        (-> (response/found "/")
            (assoc :flash {:message "Product successully added!"}))
        )
      ))
  )

(defn show [id request]
  (def row (first (.get-record products-repository id)))
  (if-not (empty? row)
    (do
      (def category (first (.get-record category-repository (:category_id row))))
      (def comments (.get-by-product-id comments-repository (:id row)))
      (layout/render "product/show.html" {:row row :category category :comments comments :flash (:flash request)})
      )
    (layout/error-page {:status 404 :title "Not found" :message "ooops"}))
  )


(defn comments-store [product_id request]
  (def params (:params request))
  (let [errors (rv/validate-comments-form params)]
    (if errors
      (-> (response/found (str "/product/" product_id))
          (assoc :flash (assoc params :comments-errors errors)))
      (do
        (def row (first (.get-record products-repository product_id)))
        ; insert comment
        (.insert-record comments-repository {:product_id product_id
                                             :user_id (:id (:identity request))
                                             :text (:text params)
                                             :date (tc/to-sql-time (java.util.Date.))})
        ; inc comments num
        (.update-record products-repository product_id {:comments_num (+ 1 (:comments_num row))})

        (-> (response/found (str "/product/" product_id))
            (assoc :flash (assoc params :message "Commen added")))
        ))))
;;---------------------------------------------
;;---------------------------------------------

(defn upload-file [file]
  (if (> (:size file) 0)
    (fs/copy (:tempfile file) (str "resources/public/storage/photos/" (tc/to-long (t/now)) "_" (:filename file)))))