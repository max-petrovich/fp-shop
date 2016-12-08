(ns maxic.core
  (:use [maxic.emit]
        [maxic.render]
        [clojure.template])
  (:require [clojure.java.jdbc :as jdbc]))


(defn- to-sql-params
  [relation]
  (let [{s :sql p :args} (as-sql relation)]
    (vec (cons s p))))

(defn fetch-one
  [db relation]
  (jdbc/query
    db
    (to-sql-params relation)
    {:result-set-fn first}))

(defn fetch-all
  [db relation]
  (jdbc/query
    db
    (to-sql-params relation)
    {:result-set-fn vec}))

;;; =================================

(defn limit
  [relation v]
  (assoc relation :limit v))

(defn join*
  [{:keys [tables joins] :as q} type alias table on]
  (let [a (or alias table)]
    (assoc
      q
      :tables (assoc tables a table)
      :joins (conj (or joins []) [a type on]))))

(defn from
  ([q table] (join* q nil table table nil))
  ([q table alias] (join* q nil table alias nil)))

(defmacro select
  [& body]
  `(-> (map->Select {}) ~@body))

(defrecord Select [fields where order joins tables offet limit]
  SqlLike
  (as-sql [this] (as-sql (render-select this))))


(defn- conj-expression
  [e1 e2]
  (cond
    (not (seq e1)) e2
    (= 'and (first e1)) (conj (vec e1) e2)
    :else (vector 'and e1 e2)))


(defn prepare-expression
  [e]
  (if (seq? e)
    `(vector
       (quote ~(first e))
       ~@(map prepare-expression (rest e)))
    e))

(defn where*
  [query expr]
  (assoc query :where (conj-expression (:where query) expr)))

(defmacro where
  [q body]
  `(where* ~q ~(prepare-expression body)))

(do-template
  [join-name join-key]

  (defmacro join-name
    ([relation alias table cond]
     `(join* ~relation ~join-key ~alias ~table ~(prepare-expression cond)))
    ([relation table cond]
     `(let [table# ~table]
        (join* ~relation ~join-key nil table# ~(prepare-expression cond)))))

  join-inner :inner,
  join :inner,
  join-right :right,
  join-left :left,
  join-full :full)


(defn- map-vals
  [f m]
  (into (if (map? m) (empty m) {}) (for [[k v] m] [k (f v)])))

(defn as-alias
  [n]
  (cond
    (string? n) (keyword n)
    :else n))

(defn- prepare-fields
  [fs]
  (if (map? fs)
    (map-vals prepare-expression fs)
    (into {} (map (juxt as-alias prepare-expression) fs))))

(defn fields*
  [query fd]
  (assoc query :fields fd))

(defmacro fields
  [query fd]
  `(fields* ~query ~(prepare-fields fd)))

(defn order*
  ([relation column] (order* relation column nil))
  ([{order :order :as relation} column dir]
   (assoc
     relation
     :order (cons [column dir] order))))

(defmacro order
  ([relation column]
   `(order* ~relation
            ~(prepare-expression column)))
  ([relation column dir]
   `(order* ~relation
            ~(prepare-expression column) ~dir)))
