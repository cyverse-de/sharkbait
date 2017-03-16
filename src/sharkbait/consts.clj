(ns sharkbait.consts)

;; Various folders used by the DE.
(def folder-format-strings
  {:ldap     "iplant:ldap"
   :de       "iplant:de:%s"
   :de-users "iplant:de:%s:users"})

;; Groups used by the DE.
(def ldap-loader-group-name "ldap-group-loader")

;; Roles used by the DE.
(def de-users-role-name "de-users")
