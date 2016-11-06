(ns user
  (:require [mount.core :as mount]
            shop.core))

(defn start []
  (mount/start-without #'shop.core/repl-server))

(defn stop []
  (mount/stop-except #'shop.core/repl-server))

(defn restart []
  (stop)
  (start))


