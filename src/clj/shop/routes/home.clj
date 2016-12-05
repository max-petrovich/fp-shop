(ns shop.routes.home
  (:require [compojure.core :refer [defroutes GET POST]]
            [shop.controllers.home-controller :as home-controller]
            [shop.controllers.account-controller :as account-controller]
            [shop.controllers.products-controller :as products-controller]
            [shop.controllers.test-controller :as test-controller]
            ))

(defroutes home-routes
           (GET "/" request (home-controller/index request))
           (GET "/account" request (account-controller/account-index request))
           (GET "/logout" request (account-controller/do-logout request))
           (POST "/login" request (account-controller/do-auth-user request))
           (POST "/register" request (account-controller/do-register-user request))

           (GET "/products" [] (products-controller/index))
           (GET "/product/:id" [id :as request] (products-controller/show id request))
           (POST "/product/:product_id" [product_id :as request] (products-controller/comments-store product_id request))

           (GET "/test" [] (test-controller/index))
           )

