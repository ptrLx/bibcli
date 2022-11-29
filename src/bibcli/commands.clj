(ns bibcli.commands
  (:require [bibcli.system :as system]
            [bibcli.git :as git]
            [bibcli.bibtex :as bibtex]))

(defn config
  [{:keys [autocommit autopush] :as _args}]
  (if (boolean? autocommit)
    (system/set_autocommit autocommit))
  (if (boolean? autopush)
    (system/set_autopush autopush)))

(defn init_central
  [{:keys [git] :as _args}]
  (system/init_central)
  (if git (git/init_central)))

(defn _add_central
  [path bibtex alias type commit push move]
  ;; (if (not (nil? bibtex))
  ;;   (if (system/path_valid? bibtex)
  ;;     ((if (not (nil? alias))
  ;;        (;; todo use this alias
  ;;         ))
  ;;      ((if (not (contains? bibtex/types type))
  ;;         (;; todo use type :misc
  ;;          )))
  ;;      (;; todo read path file and bibtex file and store in repo
  ;;       ))
  ;;     (;; todo error invalid bibtex file provided
  ;;      ))
  ;;   (if (not (nil? alias))
  ;;     (if (not (nil type))
  ;;       (;; todo prompt editor with alias and type bibtex template
  ;;        )
  ;;       (;; todo use type :misc
  ;;        ))
  ;;     (;; todo error: No bibtex file and no alias is provided.
  ;;      )))

  ;; (if move (;; todo move path to repo
  ;;             ;; todo println
  ;;           )
  ;;     (;; todo copy path to repo
  ;;   ;; todo println
  ;;      ))

  ;; (if (or commit (system/autocommit_is_set))
  ;;   ((git/commit_add_res "TODO_INSERT_ALIAS")
  ;;    (if (or push (system/autopush_is_set))
  ;;      (git/push_central))))
  )

(defn add_central
  [{:keys [path bibtex alias type commit push] :as _args}]
  (_add_central path bibtex alias type commit push false))

(defn move_central
  [{:keys [path bibtex alias type commit push] :as _args}]
  (_add_central path bibtex alias type commit push true))

(defn remove_central
  [{:keys [aliases] :as _args}]
  ;; todo loop through aliases and call system/remove_central for every alias
  (println _args))

(defn list_central
  [{:keys [author type] :as _args}]
  (if (string? author)
    (system/list_all_res_from_author author type)
    (system/list_all_res)))

(defn init_local
  [{:keys [aliases] :as _args}]
  (system/create_bib-ref aliases))

(defn add_local
  [{:keys [aliases authors] :as _args}]
;; todo get all aliases from all authors and add to aliases-set
  (system/append_bib-ref aliases))

(defn remove_local
  [{:keys [aliases] :as _args}]
  (system/remove_local aliases))

(defn path
  [{:keys [alias] :as _args}]
  (println (system/get_path alias)))

(defn generate
  [{:keys [out] :as _args}]
  ;; todo
  (println _args))
