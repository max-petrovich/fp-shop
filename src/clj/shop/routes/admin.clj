(ns shop.routes.admin
  (:require [shop.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]))

(defroutes admin-routes
           (GET "/admin" [] (str "ADMIKA")))

