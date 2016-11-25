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

            [shop.db.repository.users :refer :all]
            ))

; -- repository --
(def users-repository (->users-repository))
; ----------------

(defn home-page [req]
  (layout/render "home.html"))

(defn account-page [{:keys [flash] :as req}]
  (if (authenticated? req)
    (response/found "/")
    (layout/render
      "account.html"
      flash)))


(defn test-page []
  (str "")
  )

; ---------------- AUTH
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
      (def user (first (.get-by-email users-repository (:email params))))

      (if (and user (hashers/check (:password params) (:password user)))
        (-> (response/found "/")
            (assoc :session (assoc session :identity user)))
        (-> (response/found "/account")
            (assoc :flash (assoc params :auth-errors {:email "Account not exists or wrong password!"})))))))

; --------------- REGISTRATION

(defn validate-register [params]
  (first
    (b/validate
      params
      :name v/required
      :last_name v/required
      :email [v/required v/email]
      :password [v/required [v/min-count 6]])))

(defn register-user! [{:keys [params]}]
  (let [errors (validate-register params)]
    (cond
      errors (-> (response/found "/account")
                 (assoc :flash (assoc params :reg-errors errors)))
      (first (.get-by-email users-repository (:email params))) (-> (response/found "/account")
                                                               (assoc :flash (assoc params :reg-errors {:email "Email exists in base"})))
      :else (do
              (.insert-record users-repository (merge {:role 2 :password (hashers/encrypt (params :password))}
                                                      (select-keys params [:name :last_name :email])))
              (-> (response/found "/account")
                  (assoc :flash {:message "You are successfully registred!"}))
              ))))

;----------
(defn do-logout [{session :session}]
  (-> (response/found "/")
      (assoc :session (dissoc session :identity))))

(defroutes home-routes
           (GET "/test" [] (test-page))
           (GET "/" request (home-page request))
           (GET "/logout" request (do-logout request))
           (GET "/account" request (account-page request))
           (POST "/login" request (auth-user! request))
           (POST "/register" request (register-user! request)))

