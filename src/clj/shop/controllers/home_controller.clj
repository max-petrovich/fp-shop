(ns shop.controllers.home-controller
  (:require [shop.layout :as layout]
            [shop.db.repository.products :refer :all]
            ))

(def products-repository (->products-repository))

(defn index [{:keys [flash] :as req}]

  (layout/render "home.html" (merge {:products (take 4 (.get-records products-repository))}
                                    flash)))