(ns bibcli.bibtex_search
  (:require [expound.alpha :as e]
            [babashka.fs :as fs]
            [clojure.data.json :as json]
            [flatland.ordered.map]))

(use 'flatland.ordered.map)
;;;; SEARCH ENGINE
;; Find all .bib files starting from given path

;;;; UTILS SECTION

;; Helper: own predicate for logical true (e.g. nil, false return false)
(defn logical_true? [x]
  (if x true false))

;; Helper: own predicate function
(defn coll-but-not-map?
  [coll]
  (and (coll? coll) 
       (not (map? coll))))

;; Good choice for an macro
;; Recur JVM optimisation with tail recursion
(defn help_unpack_coll [coll]
  "This function will unpack a nested collection of list or vector to a flat collection."
  ((fn unpack_coll [coll]
    (if (some true? (map coll-but-not-map? coll))
       (recur (reduce #(if (coll? %2) (apply conj %1 %2) (conj %1 %2)) [] coll))
      coll)
     )coll))

(defn bib_grep_files [path]
  "Enter the ressource path of aliases. Will return a collection or a nested collection of all files with .bib extension."
  (letfn [
          (filter_dirs [path] (filter babashka.fs/directory? (babashka.fs/list-dir (babashka.fs/expand-home path))))
          (filter_files [path] (filter babashka.fs/regular-file? (babashka.fs/list-dir (babashka.fs/expand-home path))))
          (match_bib_ext [arg] (re-find #".+\.bib$" (str arg)))
          (filter_nil [coll] (filter #(if %1 true false) coll))
          (unpack_nest_coll [coll] (reduce #(if (coll? %2) (apply conj %1 %2) (conj %1 %2)) [] coll))
          ]
    (filter_nil (unpack_nest_coll (map #(map match_bib_ext (filter_files %1)) (filter_dirs path))))
     ))

;;;; HELPER SECTION

;; helper: Check string (case insensitive)
(defn bib_pair_check [key value search]
  (if (re-find (re-pattern (str "(?i)\\b" (str search) "\\b")) (str value)) key nil))

;; helper: Return all keys with matching values
(defn bib_object_value [data value]
  (filter logical_true?
          (map #(apply bib_pair_check (conj %1 value)) (:payload data))))

;; helper: Build key coll a return object
(defn bib_build_match_coll [data coll]
  (let [object_payload (:payload data)]
    (if (not (= 0 (count coll)))
      (do
        (reduce #(conj %1 %2 (get object_payload %2)) 
                [(:source data) (get object_payload "entrytype") (get object_payload "citekey")] coll)
        ))))

;; helper: Return a collection of bib objects. Grep all bib files from aliases located in corresponding ressource path
(defn bib_res_object_coll [path]
  (let [
        bib_files (bib_grep_files path)
        bib_objects (help_unpack_coll (map parse_bib_file bib_files))
        ]
    bib_objects))

;; Main function: search for VALUE
(defn bib_search_res_value [coll s]
  "Enter a collection of bib objects and s (search string) for case insensitive look up of values."
  (letfn [
          (search_bib_value [data value]
            (bib_build_match_coll data (bib_object_value data value)))
          ]
      (filter logical_true? (map #(search_bib_value %1 s) coll))
      ))

;; Main function: search for KEY
(defn bib_search_res_key [coll key]
  "Enter a collection of bib objects and key for look up all bib objects containing this key."
  (letfn [
          (search_bib_key [data key]
            (if (contains? (:payload data) key)
              (bib_build_match_coll data (vector key))))
          ]
    (filter logical_true? (map #(search_bib_key %1 (name key)) coll))
    ))

;; Main function: search both KEY and VALUE
(defn bib_search_res_key_value [coll key s]
  "Enter a collection of bib objects and search by key and s search string."
  (letfn [
          (search_bib_key_value [data key search]
            (let [
                  ret_value (get (:payload data) key)
                  ]
              (if ret_value
                (if (bib_pair_check key ret_value s)
                  (bib_build_match_coll data (vector key)))))) 
          ]
    (filter logical_true? (map #(search_bib_key_value %1 (name key) s) coll))
    ))

;; Interface for search engine
;; Example: (bibcli.system/bib_search path :key "year" :value "1994")
(defn bib_search [path & {:keys [key value]}]
  "With this function you can search all contents of the bib tex files for keywords, values or both. Pass the corresponding ressource path an at least the argument :key, :value or both."
  (cond
    (and key value) (bib_search_res_key_value (bib_res_object_coll path) key value)
    (logical_true? key) (bib_search_res_key (bib_res_object_coll path) key) 
    (logical_true? value) (bib_search_res_value (bib_res_object_coll path) value)
    :else (throw (Exception. "Argument-Error: You have to pass at least :key or :value!"))
))
