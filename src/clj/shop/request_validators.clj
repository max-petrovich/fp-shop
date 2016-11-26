(ns shop.request-validators
  (:require   [bouncer.core :as b]
              [bouncer.validators :as v]))


(defn validate-auth [params]
  (first
    (b/validate
      params
      :email [v/required v/email]
      :password v/required)))

(defn validate-register [params]
  (first
    (b/validate
      params
      :name v/required
      :last_name v/required
      :email [v/required v/email]
      :password [v/required [v/min-count 6]])))

(defn validate-product-form [params]
  (first
    (b/validate
      params
      :title v/required
      :category_id v/required
      :description [v/required [v/min-count 6]]
      :price [v/required [v/matches #"^\d+$"]]
      :article [v/required [v/matches #"^\d+$"]])))