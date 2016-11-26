(ns shop.handler
  (:require [shop.db.core]
            [compojure.core :refer [routes wrap-routes]]
            [shop.layout :refer [error-page]]
            [shop.layout :as layout]
            [shop.routes.home :refer [home-routes]]
            [shop.routes.admin :refer [admin-routes]]
            [compojure.route :as route]
            [shop.env :refer [defaults]]
            [mount.core :as mount]
            [shop.middleware :as middleware]
            ))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'admin-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats)
        (wrap-routes middleware/wrap-admin))
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (layout/error-page {:status 404 :title "Not found" :message "ooops"}))))


(defn app [] (middleware/wrap-base #'app-routes))
