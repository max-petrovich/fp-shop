(ns shop.routes.admin
  (:require [compojure.core :refer [defroutes GET POST]]
            [shop.controllers.products-controller :as products-controller]
            [shop.controllers.cart-controller :as cart-controller]
            ))

(defroutes admin-routes
           (GET "/product/add" request (products-controller/create request))
           (POST "/product/add" request (products-controller/store request))

           (GET "/cart" request (cart-controller/index request))
           (POST "/cart/add" request (cart-controller/add-product request))
           (POST "/cart/remove" request (cart-controller/remove-product request))
           )

