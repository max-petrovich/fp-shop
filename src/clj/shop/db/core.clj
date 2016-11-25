(ns shop.db.core
  (:require
    [clojure.java.jdbc :as jdbc]
    [conman.core :as conman]
    [shop.config :refer [env]]
    [mount.core :refer [defstate]]
    [korma.db :as kdb])
  (:import [java.sql
            BatchUpdateException
            PreparedStatement]))

(defstate ^:dynamic *db*
          :start (kdb/defdb kdb (env :db-spec))
          ;:stop (conman/disconnect! *db*)
          )




;(conman/bind-connection *db* "sql/queries.sql")

(defn to-date [^java.sql.Date sql-date]
  (-> sql-date (.getTime) (java.util.Date.)))

(extend-protocol jdbc/IResultSetReadColumn
  java.sql.Date
  (result-set-read-column [v _ _] (to-date v))

  java.sql.Timestamp
  (result-set-read-column [v _ _] (to-date v)))

(extend-type java.util.Date
  jdbc/ISQLParameter
  (set-parameter [v ^PreparedStatement stmt idx]
    (.setTimestamp stmt idx (java.sql.Timestamp. (.getTime v)))))

