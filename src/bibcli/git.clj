(ns bibcli.git
  (:require [expound.alpha :as expound]))

(defn init_central
  []
  (println "not implemented!"))

(defn fetch
  []
  (println "not implemented!"))

(defn commit_add_res
  [alias]
  (println "not implemented!"))

(defn push_central
  []
  (println "not implemented!"))

(defn central_is_repo?
  []
  (println "not implemented!")
  false)

(expound/def ::CENTRAL-IS-REPO
  #(central_is_repo?)
  "Central repository has been already initialized with git")

(expound/def ::CENTRAL-IS-NOT-REPO
  #(if % (not (central_is_repo?)))
  "Central repository has not been initialized with git")
