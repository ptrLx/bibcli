(ns bibcli.system
  (:require [expound.alpha :as e]
            [babashka.fs :as fs]
            [clojure.data.json :as json]))

;;;; General project

(defn root_folder
  []
  (str (fs/home) "/.bibcli"))
(defn config_json
  []
  (str (fs/home) "/.bibcli.json"))

(defn init_central
  "Add initial folder structure"
  []
  (fs/create-dirs (str (root_folder) "/res")))

(defn init_config
  "Create an empty(!) config file"
  []
  (fs/create-file (config_json)))

(defn delete_central
  "Delete initial folder structure"
  []
  (fs/delete-tree (root_folder)))

(defn list_aliases
  "Return a list with names of all available aliases
   Check project directory exists"
  []
  (if (fs/exists? (str (root_folder) "/res"))
    (let [FILTER_FOLDER (map str (filter fs/directory? (fs/list-dir (str (root_folder) "/res"))))]
      (map #(clojure.string/replace %1 (str (root_folder) "/res/") "") FILTER_FOLDER))
    nil))

;;;; Handle config content

(defn read_config
  "Return a dictionary from json config"
  []
  (json/read (clojure.java.io/reader (config_json))))

(defn keys_of_config
  "Return all keys from config"
  []
  (keys (read_config)))

(defn val_from_key_config
  "Get value from key"
  [key]
  ((read_config) key))

(defn write-config
  "Write with arg as map to config"
  [arg]
  (if (map? arg)
    (fs/write-lines (fs/file (config_json)) [(json/write-str arg :indent true)])
    nil))

(defn add_data_config
  "Add key-value pair to config json"
  [key value]
  (write-config (assoc (read_config) key value)))

(defn del_data_config
  "Delete by key of config json"
  [key]
  (let [readMap (read_config)]
    (if (readMap key) (write-config (dissoc readMap key)) nil)))

;;;; Utils

;; Returns true if f exists.
(defn path_valid? [path] (fs/exists? path))

(defn alias_exists? [alias]
  ;; Return true or false
  (path_valid? (str (root_folder) "/res/" alias)))

;;;; Bibtex related

(defn bib-ref_exists?
  "Checks if a bib-ref file exists in current folder"
  []
  ;; todo
  true)

(e/def ::BIB-REF-EXISTS
  (fn [_outfile]
    (bib-ref_exists?))
  "No bib-ref file found in current folder")

(e/def ::BIB-NOT-REF-EXISTS
  (fn [_outfile]
    (not (bib-ref_exists?)))
  "Bib-ref file already exists in current folder. Use `bibcli add` instead")

;; (s/def ::CAN-INIT
;;   (s/and (vector? ::BIB-NOT-REF-EXISTS)))

;; (e/def ::CAN-INIT
;;   (fn [a]
;;     (println a)
;;     true)
;;   "test")

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

(e/def ::PATH-VALID
  #(path_valid? %)
  "invalid path")

(e/def ::ALIAS-EXISTS
  #(alias_exists? %)
  "alias does not exist in repository")

(e/def ::ALIAS-NOT-EXISTS
  #(not (alias_exists? %))
  "alias does already exist in repository")

(e/def ::LIST-ALIAS-EXISTS
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




