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
  (if git (git/init_central)))

(defn _add_central
  ([path bibtex alias type commit push move]
  ;;  (if (not (nil? bibtex))
  ;;    (if (bibtex/bibtex_valid? bibtex)
  ;;      (_add_central path (bibtex/alias bibtex) (bibtex/type bibtex) commit push move)
  ;;      (;; todo error invalid bibtex file provided
  ;;       ))
  ;;    (if (not (nil? alias))
  ;;      (if (not (nil type))
  ;;        (_add_central path alias type commit push move)
  ;;        (_add_central path alias :misc commit push move))
  ;;      (;; todo error: No bibtex file and no alias is provided.
  ;;       ))))

  ;; ([path alias type commit push move]
  ;;  (if move
  ;;    (do (;; todo move path to repo
  ;;             ;; todo println
  ;;         )
  ;;        (do (;; todo copy path to repo
  ;;   ;; todo println
  ;;             ))))

  ;;  (if (or commit (system/autocommit_is_set))
  ;;    (do (git/commit_add_res alias)
  ;;        (if (or push (system/autopush_is_set))
  ;;          (git/push_central)))))
   ))

(defn add_central
  [{:keys [path bibtex alias type commit push] :as _args}]
  (_add_central path bibtex alias type commit push false))

(defn move_central
  [{:keys [path bibtex alias type commit push] :as _args}]
  (_add_central path bibtex alias type commit push true))

(defn remove_central
  [{:keys [alias] :as _args}]
  ;; todo loop through aliases and call system/remove_central for every alias
  (println _args))

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
