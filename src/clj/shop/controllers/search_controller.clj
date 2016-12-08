(ns shop.controllers.search-controller
  (:require [shop.db.repository.products :refer :all])
  (:import [org.sphx.api SphinxClient]))

(def repo (->products-repository))

(def sphinx-client (new SphinxClient))
(.SetServer sphinx-client "127.0.0.1" 9312)
(.SetMatchMode sphinx-client (SphinxClient/SPH_MATCH_ANY))
(.SetLimits sphinx-client 0 10)
(.SetSortMode sphinx-client (SphinxClient/SPH_SORT_RELEVANCE) "")

(defn search-sphinx
  [query]
  (let [result (.Query sphinx-client query "indexProducts")]
    (reduce #(conj %1 (.docId %2)) [] (.matches result))))

(defn index [query]
  (let [searched (search-sphinx query)]
    (if (empty? searched)
      []
      (.get-by-ids repo searched)))
  )