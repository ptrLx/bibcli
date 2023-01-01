(ns bibcli.system
  (:require [expound.alpha :as e]
            [babashka.fs :as fs]
            [clojure.data.json :as json]
            [flatland.ordered.map]))

(use 'flatland.ordered.map)

(defn root_folder
  []
  (str (fs/home) "/.bibcli"))
(defn config_json
  []
  (str (fs/home) "/.bibcli.json"))

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

;; Alternativly a macro to private a function is: defn-
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


;;;; BIB TEX PARSING

;; helpers
(defn- bib_check_head [line]
  "Helper function for bib_parser. Will return a map on success or nil on failure."
  
  ;; Match for entrytype and citekey (head)
  ;; Spaces between are currently not allowed 
  (if (re-matches #"^@[a-zA-Z]+\{[a-zA-Z0-9_:-]+,$" line)
    (do

      (comment
      { "entrytype"
       (re-find #"(?<=@)[a-zA-Z]+" line)
       "citekey"
       (re-find #"(?<=\{)[a-zA-Z0-9_:-]+" line) })

      (ordered-map "entrytype"
       (re-find #"(?<=@)[a-zA-Z]+" line)
       "citekey"
       (re-find #"(?<=\{)[a-zA-Z0-9_:-]+" line) )

      )      
    nil
    ))

;; Notice: r-value has to be written as string with double quotes!!!
(defn- bib_get_body_line [line]
  "Helper function for bib_parser. Will return a map on success or nil on failure."
  ;; parse attribute and value from body
  ;; check syntax: ATTRIBUTE = VALUE
  (if (re-matches #"^\s*\t*[a-zA-Z]+\s*\t*=\s*\t*\".*\",*$" line)
    (do
      ;; Attributes should only consists of letters
      ;; Values can consist of all printable ascii letters
      (comment
      { (clojure.string/trim (re-find #"\s*\t*[a-zA-Z]+" line))
       ;(clojure.string/trim (re-find #"(?<=\=)[\x20-\x7E]+" line))
       (clojure.string/replace (re-find #"\".*\"" line) #"\"" "")
       })

      (ordered-map
       { (clojure.string/trim (re-find #"\s*\t*[a-zA-Z]+" line))
       ;(clojure.string/trim (re-find #"(?<=\=)[\x20-\x7E]+" line))
       (clojure.string/replace (re-find #"\".*\"" line) #"\"" "")
       }
       )


      )
    nil
    ))

;; Actual parser
(defn parse_bib_object [coll read_line]
  "This function needs following datastructur as coll: {:current_state 0 :current_line 0 :payload {}} It returns a bib tex object as map in :payload and a :current_line counter to store reading position of the file. Use this function in combination with reduce as follows: (reduce parse_bib_object {:current_state 0 :current_line 0 :payload {}} read_file_as_coll)"
  ;; Check: coll is a map?
    (case (:current_state coll)
      0 (do
          (let [check_head_res (bib_check_head read_line)]
           (if check_head_res
            ;; Return new maps
             (do (assoc coll :current_state (inc (:current_state coll)) :current_line (inc (:current_line coll)) :payload (merge (:payload coll) check_head_res)))
             (do (println "Warning: Missmatch of head! Error in line" (inc (:current_line coll)))))))
      1 (do
          (let [check_body_res (bib_get_body_line read_line)]
            (if check_body_res
            ;; Return new map
              (do (assoc coll :current_line (inc (:current_line coll)) :payload (merge (:payload coll) check_body_res)))
              (if (= "}" read_line)
                ;;end of object reading
                (do (assoc coll :current_state (inc (:current_state coll)) :current_line (inc (:current_line coll))))

                (do (println "Warning: Missmatch of body! Error in line" (inc (:current_line coll))))
                ))))
      ;;2 (do (println "Finished object parsing.") coll)
      2 (do coll)
      ;; else
      "Error: Wrong state!"
    ))

;; "/Users/blacksurface/Desktop/WiSe22_23/MFPM/bibcli/"
;; "test2.bib"
;; To-Do: Reihenfolge beim Output beachten !
;; Fix: Lazy-Seq in normale Seq überführen, dann
;; werden Elemente in Reihenfolge mit conj angehängt
;; Ab einer bestimmten Länge der bibtext objecte fängt es an (mehr als 6 Einträge)
;; hash-map's Reihenfolge kann sich ändern, wenn diese manipuliert wird, z.B. mit assoc
;; Alternative Datenstrukturen eher schlecht
;; -> Map gleich komplett erstellen und auf assoc/dissoc verzichten?
;; Extra Implementierung für orderd-maps
(defn parse_bib_file [path]
  (let [read_file (babashka.fs/read-all-lines path)
        match_indeces (keep-indexed #(when %2 %1) (map bibcli.system/bib_check_head read_file))
        res_object_list (reduce #(conj %1 (reduce parse_bib_object (ordered-map :current_state 0 :current_line %2 :payload (ordered-map)) (drop %2 read_file))) [] match_indeces) 
        ]
    res_object_list
    )
  )

;; !!!
;; To-Do: Do not use it! Clean up here, old ideas
(defn parse_bib_file_proto [path]


(reduce #(conj %1 (reduce bibcli.system/parse_bib_object {:current_state 0 :current_line %2 :payload {}} (drop %2 read_file2))) [] [0 8])

  (keep-indexed #(when %2 %1) (map bibcli.system/bib_check_head read_file2))

  (reduce #(conj %1 (reduce bibcli.system/parse_bib_object {:current_state 0 :current_line %2 :payload {}} (drop %2 read_file2))) [] (keep-indexed #(when %2 %1) (map bibcli.system/bib_check_head read_file2)))

  
  ;; Raed bib file
  (babashka.fs/read-all-lines path)
  ;; Create a list with matches of head
  (map bibcli.system/bib_check_head read_file2)
  ;; Return (lazy-sequence) indexes from values which are logical true
  (keep-indexed #(when %2 %1) res)
  ;; parse object
  (reduce parse_bib_object {:current_state 0 :current_line 0 :payload {}} read_file_as_coll)

  (let [read_file (babashka.fs/read-all-lines path)
        match_indeces (keep-indexed #(when %2 %1) read_file)
        res_object_list (reduce #(conj %1 (reduce parse_bib_object {:current_state 0 :current_line %2 :payload {}} (drop %2 read_file))) [] read_file) 
        ]
    res_object_list
    )
  
  )

;; !!!
;; Do not use it: OLD iterative solution
(defn bib_parser [path]
  "Read a bibtex file and convert each object to a map. The return value will be a list of maps"
  
  ;; Check whether the path exists 
  (if path_valid? (str path)
      (do
        (println "ERROR::bib_parser Path does not exists: " path)
        nil
        )
      ;;else
      true
  )

  (comment
    ;; Merge all body lines together
    (reduce #(merge %1 %2) {"entrykey" "book" "citekey" "test123"} (filter #(when %1 %1) (map bibcli.system/bib_get_body_line file_content)))
    )

  ;; Alternativ for atoms
  ;; (reduce #(merge %1 {(str "test_" %2) %2}) {} [1 2 3])
  
  
  ;; Use a state machine for parsing process
  ;; 0 : check for object head
  ;; 1 : read body from object
  ;; return to 0 by reaching char '}'
  (let [state (atom 0) result_map (atom {}) result_list (atom []) file_content (fs/read-all-lines path)]

    (doseq [line file_content]

      (case @state

        0 (do
            (let [check_head (bib_check_head line)]
              (if check_head
                (do
                  (swap! result_map #(merge %1 check_head))
                  (swap! state inc)
                  )
                )
              )
       ;; To-Do: Cornercase head exists but body not or is empty
       ;(if (= line "}") (do (reset! state) ;;add map to list )     
        1 (do

            (if (= line "}")
              (do (reset! state)
                  (swap! result_list #(conj %1 @result_map))
                  )
            
                
              (let [check_body (bib_get_body_line line)]
                (if check_body
                  (do
                    (swap! result_map #(merge %1 check_body))
                    )
                  )
                )
              )
            )
        )
      )
    )  
  ;; until reaching '}' char
  
  ;; Extract matches as key value pairs
  ;; citykeys should be unique 
  ;; store result in dict citeKEY : value(entrytype)
  )

  ;; WRITE BIB TEX FILE

(defn bib_object_to_string [bib_object]
  "Converting a map which represents a valid bib text to a list of formated strings"

  (let [body_data (dissoc bib_object "entrytype" "citekey")]
    (apply conj
      (conj [] (format "@%s{%s," (bib_object "entrytype") (bib_object "citekey")))
      (conj
       (vec
        (doall (map #(format "%-2s %-10s = \"%s\"" "" %1 %2) (keys body_data) (vals body_data)))
        ) "}" "")))
  )

(defn bib_file [coll]
  "Converting a bib tex map datastructure to a list of string for writing back to file"
  
  (reduce #(apply conj %1 (bibcli.system/bib_object_to_string %2)) [] (reduce #(conj %1 (:payload %2)) [] coll))  
  )
  
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
  #(res_exists? %)
  "alias does not exist in repository")

(e/def ::ALIASES-EXISTS ;;todo macro magic with ::ALIAS-EXISTS
  #(multiple_res_exist? %)
  "alias does not exist in repository")

(e/def ::ALIAS-NOT-EXISTS
  #(not (res_exists? %))
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
  "stub!")

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

(defn test[] "hello world!")
