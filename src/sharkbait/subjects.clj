(ns sharkbait.subjects
  (:require [clojure.string :as string])
  (:import [edu.internet2.middleware.grouper SubjectFinder]
           [edu.internet2.middleware.subject Subject]))

(def find-subject
  "Finds a subject with the given ID. This function is memoized because it appears that searching
  for a subject more than once can cause null pointer exceptions."
  (memoize (fn [subject-id required?]
             (SubjectFinder/findByIdentifier ^String subject-id ^Boolean required?))))

(defn find-subjects
  "Finds all subjects with usernames in the given list of usernames. This function produces some false
  positives, but it was far more efficient than calling searchByIdentifier for each identifier separately
  or calling searchByIdentifiers. This function compensates for the false positives by filtering the result
  set. "
  [ids]
  (let [id-set (set ids)]
    (filter #(contains? id-set (.getId ^Subject %))
            (SubjectFinder/findAll ^String (string/join "," ids)))))
