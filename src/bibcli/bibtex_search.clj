(ns bibcli.bibtex_search
  (:require
   [babashka.fs :as fs]
   [bibcli.bibtex :as bibtex]
  ;;  [flatland.ordered.map]
   ))

;; (use 'flatland.ordered.map)
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
(defn help_do_flat_coll
  "This function will unpack a nested collection of list or vector to a flat collection."
  [coll]
  ((fn unpack_coll [coll]
     (if (some true? (map coll-but-not-map? coll))
       (recur (reduce #(if (coll? %2) (apply conj %1 %2) (conj %1 %2)) [] coll))
       coll)) coll))

(defn bib_grep_files
  "Enter the ressource path of aliases. Will return a collection or a nested collection of all files with .bib extension."
  [path]
  (letfn [(filter_dirs [path] (filter babashka.fs/directory? (babashka.fs/list-dir (babashka.fs/expand-home path))))
          (filter_files [path] (filter babashka.fs/regular-file? (babashka.fs/list-dir (babashka.fs/expand-home path))))
          (match_bib_ext [arg] (re-find #"^(?i)\bbib\b$|.+\.bib$" (str arg)))
          (filter_nil [coll] (filter #(if %1 true false) coll))
          (unpack_nest_coll [coll] (reduce #(if (coll? %2) (apply conj %1 %2) (conj %1 %2)) [] coll))]
    (filter_nil (unpack_nest_coll (map #(map match_bib_ext (filter_files %1)) (filter_dirs path))))))

;;;; HELPER SECTION

;; proto: helper: Extended version: controll case insensitive and 
(defn bib_re_create_pattern
  "Create regex pattern by search string and further options like :case-insensitive true and/or :exact-match true"
  [search & {:keys [case-insensitive exact-match]}]
    (let [
          ci_mode (if case-insensitive "(?i)" "")
          em_mode (if exact-match "\\b<search_word>\\b" "<search_word>")
          re_string (clojure.string/replace (str ci_mode em_mode) #"<search_word>" search)
          ]
      (re-pattern re_string)
    ))

(defn bib_pair_check_ext
  "Extend bib_pair_check to controll options like case-insensitive and exact-matching"
  [key value search & {:keys [case-insensitive exact-match]}]
  
  (if (re-find (bib_re_create_pattern search :case-insensitive case-insensitive :exact-match exact-match) (str value))
    key nil))

;; helper: Check string: Static default case-insensitive and not exact-matching. To get own controll of options, please use bib_pair_check_ext.
(defn bib_pair_check [key value search]
  (if (re-find (bib_re_create_pattern search :case-insensitive true :exact-match false) (str value)) key nil))

;; OLD VERSION! Clean this up.
;; helper: Check string (case insensitive)
(comment
  (defn bib_pair_check [key value search]
  (if (re-find (re-pattern (str "(?i)\\b" (str search) "\\b")) (str value)) key nil))
  )

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
                [(:source data) "entrytype" (get object_payload "entrytype") "citekey" (get object_payload "citekey")] coll)))))

;; helper: Return a collection of bib objects. Grep all bib files from aliases located in corresponding ressource path
(defn bib_res_object_coll [path]
  (let [bib_files (bib_grep_files path)
        bib_objects (help_do_flat_coll (map bibtex/parse_bib_file bib_files))]
    bib_objects))

;; Main function: search for VALUE
(defn bib_search_res_value
  "Enter a collection of bib objects and s (search string) for case insensitive look up of values."
  [coll s]
  (letfn [(search_bib_value [data value]
            (bib_build_match_coll data (bib_object_value data value)))]
    (filter logical_true? (map #(search_bib_value %1 s) coll))))

;; Main function: search for KEY
(defn bib_search_res_key
  "Enter a collection of bib objects and key for look up all bib objects containing this key."
  [coll key]
  (letfn [(search_bib_key [data key]
            (if (contains? (:payload data) key)
              (bib_build_match_coll data (vector key))))]
    (filter logical_true? (map #(search_bib_key %1 (name key)) coll))))

;; Main function: search both KEY and VALUE
(defn bib_search_res_key_value
  "Enter a collection of bib objects and search by key and s search string."
  [coll key s]
  (letfn [(search_bib_key_value [data key search]
            (let [ret_value (get (:payload data) key)]
              (if ret_value
                (if (bib_pair_check key ret_value s)
                  (bib_build_match_coll data (vector key))))))]
    (filter logical_true? (map #(search_bib_key_value %1 (name key) s) coll))))

;; Interface for search engine
;; Example: (bibcli.system/bib_search path :key "year" :value "1994")
(defn bib_search
  "With this function you can search all contents of the bib tex files for keywords, values or both. Pass the corresponding ressource path an at least the argument :key, :value or both."
  [path & {:keys [key value]}]
  (cond
    (and key value) (bib_search_res_key_value (bib_res_object_coll path) key value)
    (logical_true? key) (bib_search_res_key (bib_res_object_coll path) key)
    (logical_true? value) (bib_search_res_value (bib_res_object_coll path) value)
    :else (throw (Exception. "Argument-Error: You have to pass at least :key or :value!"))))


(defn bib_create_pprint_coll
   "Input a collection of search result. This function will return a pretty print of strings as collection."
  [coll]
  (letfn [(create_pprint_item [coll]
            (let [
                  prefix ["- - -" "Path:" (nth coll 0) ""]
                  bib_obj (apply hash-map (drop 1 coll))
                  str_bib_obj (bibcli.bibtex/bib_object_to_string bib_obj)
                  ]
              (help_do_flat_coll (apply conj [prefix str_bib_obj]))
           ))]
    (map create_pprint_item coll)
    ))

(defn bib_search_as_pprint
  "This function is a pretty print wrapper of bib_search. :print-now true will immediately do an side-effect print. As false this function return pretty strings as flat collection"
  [path & {:keys [key value print-now]}]
  (let [
        res_search_coll (bib_search path :key key :value value)
        pprint_coll (bib_create_pprint_coll res_search_coll)
        pprint_flat_coll (help_do_flat_coll pprint_coll)
        ]
    ;; run! has the same functionality like:
    ;; (reduce #(println %2) "" ["Hello" "my" "friends"])
    (if print-now (run! println pprint_flat_coll)
        pprint_flat_coll)
    ))

;; - - -test
(def path "/Users/blacksurface/Desktop/bib_proj/res")
