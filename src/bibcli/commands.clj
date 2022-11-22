(ns bibcli.commands)

(defn config
  [{:keys [autocommit autopush] :as args}]
  (println args))

(defn init_central
  [{:keys [git] :as args}]
  (println args))

(defn add_central
  [{:keys [path bibtex alias type commit push] :as args}]
  (println args))

(defn move_central
  [{:keys [path bibtex alias type commit push] :as args}]
  (println args))

(defn remove_central
  [{:keys [author type] :as args}]
  (println args))

(defn init_local
  [{:keys [path resources] :as args}]
  (println args))

(defn add_local
  [{:keys [aliases authors] :as args}]
  (println args))

(defn remove_local
  [{:keys [aliases] :as args}]
  (println args))

(defn path
  [{:keys [alias] :as args}]
  (println args))

(defn generate
  [{:keys [out] :as args}]
  (println args))
