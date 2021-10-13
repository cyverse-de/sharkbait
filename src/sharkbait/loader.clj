(ns sharkbait.loader
  (:require [sharkbait.consts :as consts]
            [sharkbait.folders :as folders]
            [sharkbait.roles :as roles])
  (:import [edu.internet2.middleware.grouper Group]
           [edu.internet2.middleware.grouper.app.loader.ldap LoaderLdapUtils]
           [edu.internet2.middleware.grouper.attr.assign AttributeAssign AttributeAssignGroupDelegate]
           [edu.internet2.middleware.grouper.attr.value AttributeValueDelegate]))

(defn- assign-loader-attribute-def
  "Assigns the loader attribute definition to a group."
  ^AttributeAssign [^Group group]
  (let [^AttributeAssignGroupDelegate delegate (.getAttributeDelegate group)]
    (.assignAttribute delegate (LoaderLdapUtils/grouperLoaderLdapAttributeDefName))
    (.retrieveAssignment delegate nil (LoaderLdapUtils/grouperLoaderLdapAttributeDefName) false true)))

(defn- attribute-values
  "Builds the attribute values for the loader group."
  [folder-names]
  {(LoaderLdapUtils/grouperLoaderLdapTypeName)                "LDAP_GROUP_LIST"
   (LoaderLdapUtils/grouperLoaderLdapFilterName)              "objectClass=posixGroup"
   (LoaderLdapUtils/grouperLoaderLdapQuartzCronName)          "0 0 * * * ?"
   (LoaderLdapUtils/grouperLoaderLdapSearchDnName)            "ou=groups"
   (LoaderLdapUtils/grouperLoaderLdapServerIdName)            "groupSource"
   (LoaderLdapUtils/grouperLoaderLdapSourceIdName)            "ldap"
   (LoaderLdapUtils/grouperLoaderLdapSubjectAttributeName)    "memberUid"
   (LoaderLdapUtils/grouperLoaderLdapSubjectIdTypeName)       "subjectId"
   (LoaderLdapUtils/grouperLoaderLdapExtraAttributesName)     "cn"
   (LoaderLdapUtils/grouperLoaderLdapGroupNameExpressionName) "groups:${groupAttributes['cn']}"
   (LoaderLdapUtils/grouperLoaderLdapReadersName)             "de_grouper"
   (LoaderLdapUtils/grouperLoaderLdapViewersName)             "de_grouper"
   (LoaderLdapUtils/grouperLoaderLdapGroupsLikeName)          (str (:ldap folder-names) ":groups:%")})

(defn create-loader-group
  "Creates the group used to define how groups are loaded from LDAP."
  [session {ldap-names :ldap :as folder-names}]
  (folders/find-folder session ldap-names)
  (let [^Group group                     (roles/create-group session ldap-names consts/ldap-loader-group-name)
        ^AttributeValueDelegate delegate (.getAttributeValueDelegate (assign-loader-attribute-def group))]
    (doseq [[k v] (attribute-values folder-names)]
      (.assignValue delegate k v))))
