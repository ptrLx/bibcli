(ns bibcli.system
  (:require [expound.alpha :as e]
            [babashka.fs :as fs]
            [clojure.data.json :as json]
            [clojure.set :as cset]))

(defn root_folder
  []
  (str (fs/home) "/.bibcli"))
(defn config_json
  []
  (str (fs/home) "/.bibcli.json"))

(defn local_bib-ref_path
  []
  "./bib-ref")

;;;; Utils

;; Returns true if f exists.
(defn path_valid? [path] (fs/exists? path))

(defn res_exists? [alias]
  ;; Return true or false
  (path_valid? (str (root_folder) "/res/" alias)))

(defn multiple_res_exist? [aliases]
  (eval `(and ~@(map res_exists? aliases))))

;;;; General project

(defn init_config
  "Create an empty(!) config file"
  []
  (fs/write-lines (fs/file (config_json)) ["{}"]))

(defn init_central
  "Add initial folder structure"
  []
  (fs/create-dirs (str (root_folder) "/res"))
  (init_config))

(defn ^:private delete_central
  "Delete initial folder structure"
  []
  (fs/delete-tree (root_folder)))

(defn remove_central
  "Remove folder of alias"
  [alias]
  (if
   (res_exists? alias)
    (fs/delete-tree (str (root_folder) "/res/" alias)))
  nil)

(defn create_central
  "Create a folder in central repository"
  [alias]
  (fs/create-dirs (str (root_folder) "/res/" alias "/")))

(defn move_to_central
  "Move a file to central repository"
  [alias path]
  (fs/move path (str (root_folder) "/res/" alias "/")))

(defn copy_to_central
  "Copy a file to central repository"
  [alias path]
  (fs/copy path (str (root_folder) "/res/" alias)))

(defn create_file_central
  [alias filename content]
  (fs/write-lines (fs/file (str (root_folder) "/res/" alias "/" filename)) [content]))

(defn list_aliases
  "Return a list with names of all available aliases
   Check project directory exists"
  []
  (if (fs/exists? (str (root_folder) "/res"))
    (let [FILTER_FOLDER (map str (filter fs/directory? (fs/list-dir (str (root_folder) "/res"))))]
      (map #(clojure.string/replace %1 (str (root_folder) "/res/") "") FILTER_FOLDER))
    nil))

;;;; Handle config content

(defn ^:private read_config
  "Return a dictionary from json config"
  []
  (json/read (clojure.java.io/reader (config_json))))

(defn ^:private keys_of_config
  "Return all keys from config"
  []
  (keys (read_config)))

(defn val_from_key_config
  "Get value from key"
  [key]
  ((read_config) key))

(defn ^:private write-config
  "Write with arg as map to config"
  [arg]
  (if (map? arg)
    (fs/write-lines (fs/file (config_json)) [(json/write-str arg :indent true)])
    nil))

(defn add_data_config
  "Add key-value pair to config json"
  [key value]
  (write-config (assoc (read_config) (if (keyword? key) (name key) key) value)))

(defn del_data_config
  "Delete by key of config json"
  [key]
  (let [readMap (read_config)]
    (if (readMap key) (write-config (dissoc readMap (if (keyword? key) (name key) key))) nil)))

;;;; Bibtex related

(defn bib-ref_exists?
  "Checks if a bib-ref file exists in current folder"
  []
  (fs/exists? (local_bib-ref_path)))

(e/def ::BIB-REF-EXISTS
  (fn [_outfile]
    (bib-ref_exists?))
  "No bib-ref file found in current folder")

(e/def ::BIB-REF-NOT-EXISTS
  (fn [_outfile]
    (not (bib-ref_exists?)))
  "Bib-ref file already exists in current folder. Use `bibcli add` instead")

(defn create_bib-ref
  "Write aliases per line to ./bib-ref"
  [aliases]
  (fs/write-lines (fs/file (local_bib-ref_path)) aliases))

(defn remove_aliases_bib-ref
  "Remove aliases from bib-ref"
  [aliases]
  (let [current_refs (fs/read-all-lines (fs/file (local_bib-ref_path)))
        new_refs (cset/difference (set current_refs) (set aliases))]
    (fs/write-lines (fs/file (local_bib-ref_path)) new_refs)))

(defn add_aliases_bib-ref
  "Add aliases to bib-ref"
  [aliases]
  (let [current_refs (fs/read-all-lines (fs/file (local_bib-ref_path)))
        new_refs (cset/union (set current_refs) (set aliases))]
    (fs/write-lines (fs/file (local_bib-ref_path)) new_refs {:append false})))


;;;; MACRO-FUNCTIONS

(e/def ::PATH-VALID
  #(path_valid? %)
  "invalid path")

(e/def ::ALIAS-EXISTS
  #(res_exists? %)
  "alias does not exist in repository")

(e/def ::ALIASES-EXISTS
  #(multiple_res_exist? %)
  "alias does not exist in repository")

(e/def ::ALIAS-NOT-EXISTS
  #(not (res_exists? %))
  "alias does already exist in repository")

;;;; NOT IMPLEMENTED

;; COMMENT-MB: Ist das notwendig?

(defn get_path
  "Return path of the resource of a given alias"
  [alias]
  ;; todo
  )

;; DUMMY-FUNCTIONS TO GRANT FUNCTIONALLITY


(defn list_all_res
  []
  (do)
  "stub!")

(defn remove_local []
  (do))

(defn get_path []
  (do))
