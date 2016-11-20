(ns shop.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [shop.layout :refer [error-page]]
            [shop.layout :as layout]
            [shop.routes.home :refer [home-routes]]
            [shop.routes.admin :refer [admin-routes]]
            [compojure.route :as route]
            [shop.env :refer [defaults]]
            [mount.core :as mount]
            [shop.middleware :as middleware]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            ))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def backend (session-backend))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
        (wrap-authentication backend)
    (route/not-found
      (:body
        (layout/render "404.html")))))


(defn app [] (middleware/wrap-base #'app-routes))
