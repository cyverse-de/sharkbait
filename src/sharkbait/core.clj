(ns sharkbait.core
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]
            [common-cli.core :as cli]
            [sharkbait.db :as db]
            [sharkbait.folders :as folders]
            [sharkbait.loader :as loader]
            [sharkbait.sessions :as sessions]
            [sharkbait.subjects :as subjects]
            [sharkbait.users :as users]))

(def tool-info
  {:desc "Utility for initializing Grouper for use with the DE."
   :app-name "sharkbait"
   :group-id "org.iplantc"
   :art-id "sharkbait"})

(def cli-options
  [["-?" "--help" "Show help." :default false]
   ["-h" "--host HOST" "The database hostname." :default "localhost"]
   ["-p" "--port PORT" "The database port number." :default 5432 :parse-fn #(Integer/parseInt %)]
   ["-d" "--database DATABASE" "The database name." :default "de"]
   ["-U" "--user USER" "The database username." :default "de"]
   ["-v" "--version" "Show the sharkbait version." :default false]
   ["-e" "--environment ENVIRONMENT" "The name of the DE environment." :default "dev"]
   ["-u" "--grouper-user USER" "The username that the DE uses to authenticate to Grouper" :default "de_grouper"]])

(defn- perform-root-actions
  "Performs the actions that require superuser privileges."
  [folder-names de-grouper-user]
  (let [session (sessions/create-grouper-session)]
    (try
      (loader/create-loader-group session folder-names)
      (-> (folders/find-folder session (:de folder-names))
          (folders/grant-privs (subjects/find-subject de-grouper-user true) #{:stem}))
      (finally (sessions/stop-grouper-session session)))))

(defn- load-de-subjects
  "Loads all subjects with entries in the DE database."
  [db-spec]
  (println "Loading DE subjects...")
  (->> (db/list-de-users db-spec)
       (mapv #(string/replace (:username %) #"@.*$" ""))
       (subjects/find-subjects)))

(defn- register-de-users
  "Adds all DE users to the de-users group."
  [session folder-names subjects]
  (println "Registering DE users...")
  (users/register-de-users session (:de-users folder-names) subjects))

(defn- register-de-entities
  "Registers DE entities in Grouper."
  [folder-names session subjects]
  (time (register-de-users session folder-names subjects)))

(defn- perform-de-user-actions
  "Performs the actions that do not require superuser privileges."
  [db-spec folder-names de-grouper-user]
  (let [session (sessions/create-grouper-session de-grouper-user)]
    (try
      (dorun (map (comp (partial folders/find-folder session) folder-names) [:de-users]))
      (register-de-entities folder-names session (time (load-de-subjects db-spec)))
      (finally (sessions/stop-grouper-session session)))))

(defn -main
  [& args]
  (let [{:keys [options]} (cli/handle-args tool-info args (constantly cli-options))
        folder-names      (folders/folder-names (:environment options))
        db-spec           (db/build-spec options)
        de-grouper-user   (:grouper-user options)]
    (with-open [db-conn (jdbc/get-connection db-spec)]
      (perform-root-actions folder-names de-grouper-user)
      (perform-de-user-actions (jdbc/add-connection db-spec db-conn) folder-names de-grouper-user))))
