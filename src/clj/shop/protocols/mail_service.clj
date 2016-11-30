(ns shop.protocols.mail-service)

(defprotocol mail-service-protocol
  (send-mail [this data]))