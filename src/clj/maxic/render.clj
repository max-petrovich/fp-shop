(ns maxic.render
  (:use [maxic.emit]))

(declare render-operator)
(declare render-expression)

(def NONE (->Sql "" nil))

(defn render-limit [s]
  (if-let [l (:limit s)]
    ['LIMIT l]
    NONE))

(defn render-field
  [[alias nm]]
  (if (= alias nm)
    nm
    [(render-expression nm) 'AS alias]))

(defn render-fields
  [{:keys [fields]}]
  (if (or (nil? fields) (= fields :*))
    '*
    (interpose (symbol ",") (map render-field fields))))

(defn render-where
  [{:keys [where]}]
  (if where
    ['WHERE (render-expression where)]
    NONE))

(defn render-table
  [[alias table]]
  (if (= alias table)
    table
    [table 'AS alias]))

(defn render-join-type
  [jt]
  (get
    {nil (symbol ",")
     :left '[LEFT OUTER JOIN],
     :right '[RIGHT OUTER JOIN],
     :inner '[INNER JOIN],
     :full '[FULL JOIN],
     } jt jt))

(defn render-from
  [{:keys [tables joins]}]
  (if (not (empty? joins))
    ['FROM
     (let [[a jn] (first joins)
           t (tables a)]
       (assert (nil? jn))
       (render-table [a t]))
     (for [[a jn c] (rest joins)
           :let [t (tables a)]]
       [(render-join-type jn)
        (render-table [a t])
        (if c ['ON (render-expression c)] NONE)
        ])]
    NONE))

(defn- function-symbol? [s]
  (re-matches #"\w+" (name s)))

(defn render-operator
  [op & args]
  (let [ra (map render-expression args)
        lb (symbol "(")
        rb (symbol ")")]
    (if (function-symbol? op)
      [op lb (interpose (symbol ",") ra) rb]
      [lb (interpose op (map render-expression args)) rb])))

(defn render-expression
  [etree]
  (if (and (sequential? etree) (symbol? (first etree)))
    (apply render-operator etree)
    etree))

(defn render-order
  [{order :order}]
  (let [f (fn [[c d]]
            [(render-expression c)
             (get {nil [] :asc 'ASC :desc 'DESC} d d)])]
    (if order
      ['[ORDER BY] (interpose (symbol ",") (map f order))]
      [])))

(defn render-select
  [select]
  ['SELECT
   (mapv
     #(% select)
     [render-fields
      render-from
      render-where
      render-order
      render-limit])])