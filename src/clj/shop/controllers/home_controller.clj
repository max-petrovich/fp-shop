(ns shop.controllers.home-controller
  (:use [shop.db.cache.products-cache-repository])
  (:require [shop.layout :as layout]))

(def product-repository (->products-cache-repository))

(defn index [{:keys [flash] :as req}]
  (layout/render "home.html" (merge {:products (take 4 (.get-records product-repository))}
                                    flash)))