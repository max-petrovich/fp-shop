(ns shop.services.smtp-mail
  (:require [shop.protocols.mail-service :refer [mail-service-protocol]]
            [postal.core :refer [send-message]]))


(deftype smtp-mail-service [smtp-con]
  mail-service-protocol
  (send-mail [this data] (send-message smtp-con data)))