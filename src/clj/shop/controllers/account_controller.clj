(ns shop.controllers.account-controller
  (:use [shop.db.protocols.common]
        [shop.db.protocols.users]
        [shop.services.smtp-mail])
  (:require [shop.layout :as layout]
            [shop.request-validators :as rv]
            [buddy.hashers :as hashers]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [ring.util.http-response :as response]
            [shop.db.repository.users :refer :all]
            [shop.config :refer [env]]))

(def users-repository (->users-repository))
(def mail-service (->smtp-mail-service (:smtp env)))
(def mail-agent (agent mail-service))

(defn account-index [{:keys [flash] :as req}]
  (if (authenticated? req)
    (response/found "/")
    (layout/render
      "account.html"
      flash)))

(defn do-auth-user [{:keys [params] session :session :as req }]
  (if-let [errors (rv/validate-auth params)]
    (-> (response/found "/account")
        (assoc :flash (assoc params :auth-errors errors)))
    (do
      (let [user (first (get-by-email users-repository (:email params)))]
        (if (and user (hashers/check (:password params) (:password user)))
          (-> (response/found ((:headers req) "referer"))
              (assoc :session (assoc session :identity user)))
          (-> (response/found "/account")
              (assoc :flash (assoc params :auth-errors {:email "Account not exists or wrong password!"})))))))
  )

(defn do-register-user [{:keys [params]}]
  (let [errors (rv/validate-register params)]
    (cond
      errors (-> (response/found "/account")
                 (assoc :flash (assoc params :reg-errors errors)))
      (first (get-by-email users-repository (:email params))) (-> (response/found "/account")
                                                                   (assoc :flash (assoc params :reg-errors {:email "Email exists in base"})))
      :else (do
              (insert-record users-repository (merge {:role 2 :password (hashers/encrypt (params :password))}
                                                      (select-keys params [:name :last_name :email])))

              (send mail-agent (fn [this data] (.send-mail this data) this) {:from (:email params)
                                                                             :to "unrelaxby@gmail.com"
                                                                             :subject "Thanks for registration!"
                                                                             :body "Your data....."})
              (-> (response/found "/account")
                  (assoc :flash {:message "You are successfully registred!"}))
              ))))

(defn do-logout [{session :session}]
  (-> (response/found "/")
      (assoc :session (dissoc session :identity))))
