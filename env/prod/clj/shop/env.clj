(ns shop.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[shop started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[shop has shut down successfully]=-"))
   :middleware identity})
