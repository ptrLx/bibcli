(ns bibcli.commands
  (:require [bibcli.system :as system]
            [bibcli.git :as git]
            [bibcli.bibtex :as bibtex]))

(defn config
  [{:keys [autocommit autopush] :as _args}]
  (if (boolean? autocommit)
    (system/add_data_config :autocommit autocommit))
  (if (boolean? autopush)
    (system/add_data_config :autopush autopush)))

(defn init_central
  [{:keys [git] :as _args}]
  (system/init_central)
  (if git (git/init_central) ()))

(defn _add_central
  ([path bibtex alias type commit push move]
   (if (not (nil? bibtex))
    ;;  bibtex file provided
     (do (if (not (nil? alias))
           (println "WARNING: Ignoring alias as it is provided in bibtex file.")
           ())
         (if (bibtex/bibtex_valid? bibtex)
           (let [alias (bibtex/alias bibtex)]
             (system/create_central alias)
             (if move
               (do (system/move_to_central alias path)
                   (system/move_to_central alias bibtex))
               (do (system/copy_to_central alias path)
                   (system/move_to_central alias bibtex)))
             (_add_central commit push))
           (println "ERROR: Invalid bibtex file.")))
    ;; no bibtex file provided
     ;; alias provided
     (if (not (nil? alias))
       (do (system/create_central alias)
           (if move
             (system/move_to_central alias path)
             (system/copy_to_central alias path))
           (system/create_file_central alias "bib" "content") ;; todo content: (bibtex/create_dummy (if (not (nil? type) type :misc)))
           (_add_central commit push))
       (println "ERROR: Bibtex file or alias is required."))))

  ([commit push]
   (if (or commit (system/val_from_key_config :commit))
     (do (git/commit_add_res alias)
         (if (or push (system/val_from_key_config :commit))
           (git/push_central)
           ()))
     ())))

(defn add_central
  [{:keys [path bibtex alias type commit push] :as _args}]
  (_add_central path bibtex alias type commit push false))

(defn move_central
  [{:keys [path bibtex alias type commit push] :as _args}]
  (_add_central path bibtex alias type commit push true))

(defn remove_central
  [{:keys [alias] :as _args}]
  (doseq [i alias] (system/remove_central i)))

(defn list_central
  [{:keys [author type] :as _args}]
  (if (string? author)
    (system/list_all_res_from_author author type)
    (system/list_all_res)))

(defn init_local
  [{:keys [alias] :as _args}]
  (system/create_bib-ref alias))

(defn add_local
  [{:keys [alias authors] :as _args}]
;; todo get all aliases from all authors and add to aliases-set
  ;; (let [authors_set (set authors)]

  ;; )
  ;; (def testasdf (
  ;;                map #(system/list_all_res_from_author % nil) authors))

  (system/append_bib-ref alias))

(defn remove_local
  [{:keys [alias] :as _args}]
  (system/remove_aliases_bib-ref alias))

(defn path
  [{:keys [alias] :as _args}]
  ;; (println (system/get_path alias))
  )

(defn generate
  [{:keys [out] :as _args}]
  ;; (system/generate_local out)
  )
