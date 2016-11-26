(ns shop.routes.admin
  (:require [compojure.core :refer [defroutes GET POST]]
            [shop.controllers.products-controller :as products-controller]
            ))

(defroutes admin-routes
           (GET "/product/add" request (products-controller/create request))
           (POST "/product/add" request (products-controller/store request)))

