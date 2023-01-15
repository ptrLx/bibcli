(ns bibcli.commands
  (:require [bibcli.system :as system]
            [bibcli.bibtex.bibtex :as bibtex]
            [bibcli.bibtex.search :as search]
            [babashka.process :refer [shell]]))

(defn- filter_existence
  [aliases]
  (filter #(if (system/res_exists? %)
             (do true)
             (do (println (str "WARN: Ressource \""  % "\" not found in central repository."))
                 false))
          (set aliases)))

(defn config
  [{:keys [autocommit autopush editor] :as _args}]
  (if (boolean? autocommit)
    (system/add_data_config :autocommit autocommit)
    ())
  (if (boolean? autopush)
    (system/add_data_config :autopush autopush)
    ())
  (if (string? editor)
    (system/add_data_config :editor editor)
    ()))

(defn init_central
  [{:keys [git] :as _args}]
  (if (system/central_exists?)
    (println "WARN: Central repository already exists.")
    (do (system/init_central)
        (if git
          (system/git_init_central)
          ()))))

(defn- open_editor
  [path edit]
  (if (and (not (nil? edit)) edit)

    (shell (str (system/editor_config) " " path))
    (println "Success."))
  ;; (println "File saved.")
  )

(defn- _add_central
  ([path bibtex alias type commit push move edit]
   (if (not (nil? bibtex))
    ;;  bibtex file provided
     (do (if (not (nil? alias))
           (println "WARN: Ignoring alias as it is provided in bibtex file.")
           ())
         (if (bibtex/bibtex_valid? bibtex)
           (let [bibtex_data (bibtex/parse_bib_file bibtex)]
             (if (= (count bibtex_data) 0)
               (println "ERROR: Bibtexfile invalid.")
               (do (if (> (count bibtex_data) 1)
                     (println "WARN: Ignoring multiple bibtex entries in provided bibtex file.")
                     ())
                   (let [bibtex_data_first (bibtex_data 0) alias (bibtex/alias bibtex_data_first)]
                     (if (system/res_exists? alias)
                       (println (str "ERROR: Ressource \"" alias "\" already exists in central repository."))
                       (do (system/create_central alias)
                           (if move
                             (do (system/move_to_central alias path)
                                 (open_editor (system/move_to_central alias bibtex) edit))
                             (do (system/copy_to_central alias path)
                                 (open_editor (system/copy_to_central alias bibtex) edit)))
                           (_add_central commit push alias)))))))
           (println "ERROR: Invalid bibtex file.")))
    ;; no bibtex file provided
     ;; alias provided
     (if (not (nil? alias))
       (do (system/create_central alias)
           (if move
             (system/move_to_central alias path)
             (system/copy_to_central alias path))
           (open_editor (system/create_file_central alias "bib" (bibtex/print_format alias (if (not (nil? type)) type "misc"))) edit)
           (_add_central commit push alias))
       (println "ERROR: Bibtex file or alias is required."))))

  ([commit push alias]
   (if (or commit (system/val_from_key_config "autocommit"))
     (if (system/git_central_is_repo?)
       (do (system/git_commit_add_res alias)
           (if (or push (system/val_from_key_config "autopush"))
             (system/git_push_central)
             ()))
       (if (or commit push)
         (println "WARN: No git repository found in central.")
         ()))
     ())))

(defn add_central
  [{:keys [path bibtex alias type commit push edit] :as _args}]
  (_add_central path bibtex alias type commit push false edit))

(defn move_central
  [{:keys [path bibtex alias type commit push edit] :as _args}]
  (_add_central path bibtex alias type commit push true edit))

(defn remove_central
  [{:keys [alias] :as _args}]
  (doseq [i alias]
    (if (system/res_exists? i)
      (system/remove_central i)
      (println (str "WARN: Alias \"" i "\" does not exist.")))))

(defn list_central
  [{:keys [] :as _args}]
  ;;// (if (string? author)
  ;;//   (println (system/list_all_res_from_author author type))
  ;;//   (println (system/list_all_res)))
  (if (system/central_exists?)
    (doseq [alias (system/list_aliases)]
      (println alias))
    (println "WARN: No central repository found.")))

(defn search_central
  [{:keys [key value] :as _args}]
  (if (and (nil? key) (nil? value))
    (println "ERROR: No key or value was provided.")
    (run! println (search/bib_search_as_pprint (str (system/root_folder) "/res/") :key key :value value))))

(defn init_local
  [{:keys [alias] :as _args}]
  (if (not (system/bib-ref_exists?))
    (system/create_bib-ref alias)
    (do
      (println "WARN: Bib-ref file already exists. Adding aliases...")
      (system/add_aliases_bib-ref (filter_existence alias)))))

(defn add_local
  [{:keys [alias] :as _args}]
  (if (not (nil? alias))
    (if (system/bib-ref_exists?)
      (system/add_aliases_bib-ref (filter_existence alias))
      (do
        (println "WARN: No bib-ref file exists. Creating one...")
        (system/create_bib-ref alias)))
    ()))

(defn remove_local
  [{:keys [alias] :as _args}]
  (if (system/bib-ref_exists?)
    (system/remove_aliases_bib-ref (set alias))
    (println "WARN: No bib-ref file exists.")))

(defn path
  [{:keys [alias] :as _args}]
  (println (system/get_path alias)))

(defn generate
  [{:keys [out] :as _args}]
  (if (system/bib-ref_exists?)
    (system/generate_local out (filter_existence (system/get_current_refs)))
    (println "WARN: No bib-ref file found.")))
