(ns sharkbait.roles
  (:import [edu.internet2.middleware.grouper FieldType Group GroupFinder GroupSave MembershipFinder]
           [edu.internet2.middleware.grouper.group TypeOfGroup]
           [edu.internet2.middleware.grouper.membership MembershipType]
           [edu.internet2.middleware.grouper.misc SaveMode]))

(defn- create-group*
  "Creates a group or role."
  [session folder group-name group-type]
  (-> (GroupSave. session)
      (.assignName (str folder ":" group-name))
      (.assignTypeOfGroup group-type)
      (.assignSaveMode SaveMode/INSERT_OR_UPDATE)
      (.save)))

(defn create-group
  "Creates a group."
  [session folder group-name]
  (create-group* session folder group-name TypeOfGroup/group))

(defn create-role
  "Creates a Grouper role."
  [session folder role-name]
  (create-group* session folder role-name TypeOfGroup/role))

(defn replace-members
  "Replaces the members of a role."
  [role subjects]
  (.replaceMembers ^Group role subjects))
