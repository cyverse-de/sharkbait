(use '[clojure.java.shell :only (sh)])
(require '[clojure.string :as string])

;; Eastwood is enabled in this project, but a custom profile needs to be used in order to enable it to
;; work without breaking `lein run` because of the way that Grouper configuration files work. To lint
;; the code in this project, please run the following command:
;;
;; lein with-profiles eastwood eastwood

(defn git-ref
  []
  (or (System/getenv "GIT_COMMIT")
      (string/trim (:out (sh "git" "rev-parse" "HEAD")))
      ""))

(defproject org.cyverse/sharkbait "2.10.0-SNAPSHOT"
  :description "Utility for initializing Grouper."
  :url "https://github.com/iPlantCollaborativeOpenSource/DE/"
  :license {:name "BSD"
            :url "http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt"}
  :manifest {"Git-Ref" ~(git-ref)}
  :uberjar-name "sharkbait-standalone.jar"
  :plugins [[jonase/eastwood "0.2.3"]
            [test2junit "1.2.2"]]
  :dependencies [[edu.internet2.middleware.grouper/grouper "2.2.1"]
                 [honeysql "0.6.2"]
                 [korma "0.4.2"]
                 [net.sf.ehcache/ehcache "2.10.1"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.hibernate/hibernate-core "3.6.10.Final"]
                 [org.hibernate/hibernate-ehcache "3.6.10.Final"]
                 [org.cyverse/common-cli "2.8.0"]
                 [org.cyverse/kameleon "3.0.0"]
                 [postgresql "9.3-1102.jdbc41"]]
  :main sharkbait.core
  :profiles {:eastwood {:resource-paths ["test-resources"]}
             :uberjar {:aot :all}})
