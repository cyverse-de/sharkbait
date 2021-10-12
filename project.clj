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
  :plugins [[lein-ancient "0.7.0"]
            [lein-cljfmt "0.8.0"]
            [jonase/eastwood "0.9.9"]
            [test2junit "1.4.2"]]
  :dependencies [[edu.internet2.middleware.grouper/grouper "2.5.29"
                  :exclusions [javax.transaction/jta
                               org.wso2.charon/org.wso2.charon.core
                               org.wso2.charon/org.wso2.charon.samples]]
                 [javax.transaction/jta "1.1"]
                 [honeysql "1.0.461"]
                 [korma "0.4.3"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.cyverse/common-cli "2.8.1"]
                 [org.cyverse/kameleon "3.0.6"]
                 [postgresql "9.3-1102.jdbc41"]]
  :main sharkbait.core
  :profiles {:eastwood {:resource-paths ["test-resources"]}
             :uberjar {:aot :all}})
