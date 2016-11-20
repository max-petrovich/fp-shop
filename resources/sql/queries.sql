-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(email, password, name, last_name, role)
VALUES (:email, :password, :name, :last_name, :role)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET email = :email, name = :name, last_name = :last_name
WHERE id = :id

-- :name get-users :? :*
-- :doc select all users
SELECT * FROM users ORDER BY id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name get-user-by-email :? :1
-- :doc retrieve a user given the login
SELECT * FROM users
WHERE email = :email

-- :name exists-user-by-credential :? :1
-- :doc check for existsing user by credential
SELECT * FROM users
WHERE email = :email and password = :password

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id