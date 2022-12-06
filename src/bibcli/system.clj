(ns bibcli.system
  (:require [expound.alpha :as expound]
            [babashka.fs :as fs]
            [clojure.data.json :as json]))

;;;; General project

(defn init_central []
  ;; Add initial folder structure
  (fs/create-dirs (str (fs/home) "/.bibcli/res")))

(defn init_config []
  ;; Create an empty(!) config file
  (fs/create-file (str (fs/home) "/.bibcli.conf")))

(defn delete_central []
  ;; Delete initial folder structure
  (fs/delete-tree (str (fs/home) "/.bibcli")))

(defn list_aliases []
  ;; Return a list with names of all available aliases
  ;; Check project directory exists
  (if (fs/exists? (str (fs/home) "/.bibcli/res"))
    (let [FILTER_FOLDER (map str (filter fs/directory? (fs/list-dir (str (fs/home) "/.bibcli/res"))))]
      (map #(clojure.string/replace %1 (str (fs/home) "/.bibcli/res/") "") FILTER_FOLDER))
    nil))

;;;; Handle config content

(defn read_config []
  ;; Return a dictionary from json config
  (json/read (clojure.java.io/reader (str (fs/home) "/.bibcli.conf"))))

;; Return all keys from config
(defn keys_of_config [] (keys (read_config)))

;; Get value from key
(defn val_from_key_config [key] ((read_config) key))

;; Write with arg as map to config
(defn write-config [arg]
  (if (map? arg)
    (fs/write-lines (fs/file (str (fs/home) "/.bibcli.conf")) [(json/write-str arg :indent true)])
    nil))

;; Add key-value pair to config json
(defn add_data_config [key value]
  (write-config (assoc (read_config) key value)))

;; Delete by key of config json
(defn del_data_config [key]
  (let [readMap (read_config)]
    (if (readMap key) (write-config (dissoc readMap key)) nil)))

;;;; Utils

;; Returns true if f exists.
(defn path_valid? [path] (fs/exists? path))

(defn alias_exists? [alias]
  ;; Return true or false
  (path_valid? (str (fs/home) "/.bibcli/res/" alias)))

;;;; Bibtex related

(defn bib-ref_exists?
  "Checks if a bib-ref file exists in current folder"
  []
  ;; todo
  true)

(expound/def ::BIB-REF-EXISTS
  (fn [_outfile]
    (bib-ref_exists?))
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

(defn remove_aliases_bib-ref
  "Remove aliases from bib-ref file if it exists"
  [aliases]
  ;; todo
  )

;;;; MACRO-FUNCTIONS

(expound/def ::PATH-VALID
  #(path_valid? %)
  "invalid path")

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

;;;; NOT IMPLEMENTED

;; COMMENT_MB: Das fällt unter Kategorie Bibtex?
(defn list_all_res_from_author
  "Scan all resources and return a set of aliases for given author.
   only include resources of this type, if type is not nil"
  [author type]
  ;; todo
  )

;; COMMENT-MB: Ist das notwendig?
(defn get_path
  "Return path of the resource of a given alias"
  [alias]
  ;; todo
  )

;; DUMMY-FUNCTIONS TO GRANT FUNCTIONALLITY
(defn set_autocommit []
  (do))

(defn set_autopush []
  (do))

(defn list_all_res []
  (do))

(defn remove_local []
  (do))

(defn get_path []
  (do))




