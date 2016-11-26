(ns shop.routes.admin
  (:require [shop.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [shop.db.repository.users :refer :all]
            [shop.db.repository.products :refer :all]
            [shop.db.repository.category :refer :all]
            [shop.request-validators :as rv]
            [me.raynes.fs :as fs]
            [clj-time.core :as t]
            [clj-time.coerce :as tc]))

(def users-repository (->users-repository))
(def category-repository (->category-repository))
(def products-repository (->products-repository))

(declare upload-file)


(defn product-add-page [request]
  (layout/render "product/add.html" {:category-hierarchy (.get-hierarchy-format category-repository) :flash (:flash request)})
  )


(defn product-add-store! [{:keys [params] :as request}]
  ;(str (tc/to-sql-time (java.util.Date.)))
  (let [errors (rv/validate-product-form params)]
    (if errors
      (-> (response/found "/product/add")
          (assoc :flash (assoc params :errors errors)))
      (do
        (def filename (upload-file (:photo params)))
        (.insert-record products-repository (merge {:user_id 1 :date (tc/to-sql-time (java.util.Date.)) :photo filename}
                                                   (select-keys params [:category_id :price :article :title :description])))
        (-> (response/found "/")
            (assoc :flash {:message "Product successully added!"}))
        )
      ))
  )

(defn upload-file [file]
  (if (> (:size file) 0)
    (fs/copy (:tempfile file) (str "storage/photos/" (tc/to-long (t/now)) "_" (:filename file)))))

(defroutes admin-routes
           (GET "/product/add" request (product-add-page request))
           (POST "/product/add" request (product-add-store! request))
         )

