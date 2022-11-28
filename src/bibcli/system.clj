(ns bibcli.system
  (:require [expound.alpha :as expound]))

;;* 
;;* central system
;;* 

(defn path_valid?
  [path]
  true)

(expound/def ::PATH-VALID
  #(path_valid? %)
  "invalid path")

(defn init_central
  "Create basic structure of ~/.bibcli"
  []
  (println "not implemented!"))

(defn set_autocommit
  "Enable autocommits in configuration"
  [v]
  (println "not implemented!"))

(defn set_autopush
  "Enable autopush in configuration"
  [v]
  (println "not implemented!"))

(defn autocommit_is_set
  []
  ;; todo
  false)

(defn autopush_is_set
  []
  ;; todo
  false)

(defn alias_exists?
  [alias]
  ;; todo
  false)

(expound/def ::ALIAS-EXISTS
  #(alias_exists? %)
  "alias does not exist in repository")

(expound/def ::ALIAS-NOT-EXISTS
  #(not (alias_exists? %))
  "alias does already exist in repository")

(expound/def ::LIST-ALIAS-EXISTS
  (fn [aliases]
    (;; todo loop through aliases and call alias_exists?. if one does not, return false
     = 1 1))
  "alias not found")

(defn remove_central
  [alias]
;; todo delete folder with alias
  )

(defn list_all_res
  []
;; todo
  )

(defn list_all_res_from_author
  "Scan all resources and return a set of aliases for given author.
   only include resources of this type, if type is not nil"
  [author type]
  ;; todo
  )

(defn get_path
  "Return path of the resource of a given alias"
  [alias]
  ;; todo
  )

;;* 
;;* Local system
;;* 

(defn bib-ref_exists?
  "Checks if a bib-ref file exists in current folder"
  []
  ;; todo
  true)

(expound/def ::BIB-REF-EXISTS
  #(bib-ref_exists?)
  "no bib-ref file found in current folder")

;; todo (spec/def ::CAN-INIT (spec/and (spec/not ::BIB-REF-EXISTS) (LIST-ALIAS-EXISTS)))

(defn create_bib-ref
  "Write aliases per line to ./bib-ref"
  [aliases]
  ;; todo
  )

(defn append_bib-ref
  "Append aliases per line to ./bib-ref. Avoid duplicates"
  ;; todo Avoid duplicates by reading current aliases to a set first.
  [aliases]
  ;; todo
  )

(defn remove_local
  "Remove aliases from bib-ref file if it exists"
  [aliases]
  ;; todo
  )