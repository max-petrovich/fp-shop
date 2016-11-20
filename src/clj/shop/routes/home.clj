(ns shop.routes.home
  (:require [shop.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [clojure.java.io :as io]
            [shop.db.core :as db]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [buddy.hashers :as hashers]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [buddy.auth.backends.session :refer [session-backend]]
  ))

(defn home-page []
    (layout/render "home.html"))

(defn account-page [{:keys [flash] :as req}]
  (if (authenticated? req)
    (response/found "/")
    (layout/render
      "account.html"
      (merge {}
             (select-keys flash [:email :password :auth-errors :reg-errors :message])))
    ))

;; controllers

;; ---------------- AUTH
(defn validate-auth [params]
  (first
    (b/validate
      params
      :email [v/required v/email]
      :password v/required)))

(defn auth-user! [{:keys [params] session :session :as req }]
  (if-let [errors (validate-auth params)]
    (-> (response/found "/account")
        (assoc :flash (assoc params :auth-errors errors)))
    (do
      (def user (db/get-user-by-email {:email (get params :email)}))
      (if (and user (hashers/check (get params :password) (get user :password)))
        (-> (response/found "/")
            (assoc :session (assoc session :identity user)))
        (-> (response/found "/account")
            (assoc :flash (assoc params :auth-errors {:email "Account not exists or wrong password!"}))))
      )
    )
)

;(defn do-login [{{username "username" password "password" next "next"} :params
;                 session :session :as req}]
;  (if-let [user (lookup-user username password)]    ; lookup-user defined elsewhere
;    (assoc (redirect (or next "/"))                 ; Redirect to "next" or /
;      :session (assoc session :identity user)) ; Add an :identity to the session
;    (response "Login page goes here")))

;; --------------- REGISTRATION

(defn validate-register [params]
  (first
    (b/validate
      params
      :email [v/required v/email]
      :password [v/required [v/min-count 6]])))

(defn register-user! [{:keys [params]}]
  (let [errors (validate-register params)]
    (cond
      errors (-> (response/found "/account")
                 (assoc :flash (assoc params :reg-errors errors)))
      (db/get-user-by-email (select-keys params [:email])) (-> (response/found "/account")
                                                               (assoc :flash (assoc params :reg-errors {:email "Email exists in base"})))
      :else (do
              (db/create-user! (merge {:name "" :last_name "" :role 2 :password (hashers/encrypt (params :password))}
                                      (select-keys params [:email])))
              (-> (response/found "/account")
                  (assoc :flash {:message "You are successfully registred!"}))
              )
      )
    )
  )

;----------
(defn do-logout [{session :session}]
  (-> (response/found "/")
      (assoc :session (dissoc session :identity))))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/logout" request (do-logout request))
  (GET "/account" request (account-page request))
  (POST "/login" request (auth-user! request))
  (POST "/register" request (register-user! request)))

