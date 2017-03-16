(ns sharkbait.users
  (:require [sharkbait.consts :as consts]
            [sharkbait.roles :as roles]))

(defn register-de-users
  [session de-users-folder subjects]
  (let [de-users-role (roles/create-role session de-users-folder consts/de-users-role-name)]
    (roles/replace-members de-users-role subjects)))
