(ns bibcli.bibtex
  (:require [expound.alpha :as expound]
            ;;// [flatland.ordered.map]
            [babashka.fs :as fs]))


(def fields #{:address :annote :author :booktitle :Email :chapter :crossref :doi :edition :editor
              :howpublished :institution :journal :key :month :note :number :organization :pages
              :publisher :school :series :title :type :volume :year})

;;// (def types #{:article :book :booklet :conference :inbook :incollection :inproceedings
;;//              :manual :mastersthesis :misc :phdthesis :proceedings :techreport :unpublished})

(def bibtex_types {:article {:required #{:author :title :journal :year :volume}
                             :optional #{:number :pages :month :doi :note :key}}
                   :book {:required #{:author :title :publisher :year}
                          :optional #{:volume :series :address :edition :month :note :key :url}}
                   :booklet {:required #{:title}
                             :optional #{:author :howpublished :address :month :year :note :key}}
                   :conference {:required #{:author :title :booktitle :year}
                                :optional #{:editor :volume :series :pages :address :month :organization :publisher :note :key}}
                   :inbook {:required #{:author :title :chapter :publisher :year}
                            :optional #{:volume :series :type :address :edition :month :note :key}}
                   :incollection {:required #{:author :title :booktitle :publisher :year}
                                  :optional #{:editor :volume :series :type :chapter :pages :address :edition :month :note :key}}
                   :inproceedings {:required #{:author :title :booktitle :year}
                                   :optional #{:editor :volume :series :pages :address :month :organization :publisher :note :key}}
                   :manual {:required #{:title}
                            :optional #{:author :organization :address :edition :month :year :note :key}}
                   :mastersthesis {:required #{:author :title :school :year}
                                   :optional #{:type :address :month :note :key}}
                   :misc {:required #{}
                          :optional #{:author :title :howpublished :month :year :note :key}}
                   :phdthesis {:required #{:author :title :school :year}
                               :optional #{:type :address :month :note :key}}
                   :proceedings {:required #{:title :year}
                                 :optional #{:editor :volume :series :address :month :publisher :organization :note :key}}
                   :techreport {:required #{:author :title :institution :year}
                                :optional #{:type :number :address :month :note :key}}
                   :unpublished {:required #{:author :title :note}
                                 :optional #{:month :year :key}}})

(defn bib_type
  "Get type ob bibtex entry"
  [parsed_bibtex]
  (get (:payload parsed_bibtex) "type"))

(defn alias
  "Get alias ob bibtex entry"
  [parsed_bibtex]
  (get (:payload parsed_bibtex) "citekey"))

(defn- bib_object_to_string
  "Converting a map which represents a valid bib text to a list of formated strings"
  [bib_object]

  (let [body_data (dissoc bib_object "entrytype" "citekey")]
    (apply conj
           (conj [] (format "@%s{%s," (name (bib_object "entrytype")) (bib_object "citekey")))
           (conj
            (vec
             (doall (map #(format "%-2s %-10s = \"%s\"" "" %1 %2) (keys body_data) (vals body_data)))) "}" ""))))

(defn- bib_gen_template
  "Enter entrytype, citekey as string and a set of keys."
  [entrytype citekey coll]

  ;; Return a new map with corresponding entrytype and citekey
  ;; and the rest of keys with empty string value ""
  (merge {"entrytype" entrytype  "citekey" citekey}
         (reduce #(assoc %1 (name %2) "") {} coll)))

(defn print_format
  "Generate a bibtex template according to a type"
  [alias type]
  (bib_object_to_string (bib_gen_template type alias (:required ((keyword type) bibtex_types)))))


;;;; BIB TEX PARSING

;; helpers
(defn- bib_check_head
  "Helper function for bib_parser. Will return a map on success or nil on failure."
  [line]

  ;; Match for entrytype and citekey (head)
  ;; Spaces between are currently not allowed 
  (if (re-matches #"^@[a-zA-Z]+\{[a-zA-Z0-9_:-]+,$" line)
    (do

      (comment
        {"entrytype"
         (re-find #"(?<=@)[a-zA-Z]+" line)
         "citekey"
         (re-find #"(?<=\{)[a-zA-Z0-9_:-]+" line)})

      {"entrytype"
       (re-find #"(?<=@)[a-zA-Z]+" line)
       "citekey"
       (re-find #"(?<=\{)[a-zA-Z0-9_:-]+" line)})
    nil))

;; Notice: r-value has to be written as string with double quotes!!!
(defn- bib_get_body_line
  "Helper function for bib_parser. Will return a map on success or nil on failure."
  [line]

  ;; parse attribute and value from body
  ;; check syntax: ATTRIBUTE = VALUE
  (if (re-matches #"^\s*\t*[a-zA-Z]+\s*\t*=\s*\t*\".*\",*$" line)
    (do
      ;; Attributes should only consists of letters
      ;; Values can consist of all printable ascii letters
      (comment
        {(clojure.string/trim (re-find #"\s*\t*[a-zA-Z]+" line))
       ;(clojure.string/trim (re-find #"(?<=\=)[\x20-\x7E]+" line))
         (clojure.string/replace (re-find #"\".*\"" line) #"\"" "")})

      {(clojure.string/trim (re-find #"\s*\t*[a-zA-Z]+" line))
       ;(clojure.string/trim (re-find #"(?<=\=)[\x20-\x7E]+" line))
       (clojure.string/replace (re-find #"\".*\"" line) #"\"" "")})
    nil))

;; Actual parser
(defn parse_bib_object
  "This function needs following datastructur as coll: {:current_state 0 :current_line 0 :payload {}} It returns a bib tex object as map in :payload and a :current_line counter to store reading position of the file. Use this function in combination with reduce as follows: (reduce parse_bib_object {:current_state 0 :current_line 0 :payload {}} read_file_as_coll)"
  [coll read_line]

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

              (do (println "Warning: Missmatch of body! Error in line" (inc (:current_line coll))))))))
      ;;2 (do (println "Finished object parsing.") coll)
    2 (do coll)
      ;; else
    "Error: Wrong state!"))

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

(defn parse_bib_str
  [bib_lines]
  (let [match_indeces (keep-indexed #(when %2 %1) (map bib_check_head bib_lines))
        res_object_list (reduce #(conj %1 (reduce parse_bib_object {:current_state 0 :current_line %2 :payload {}} (drop %2 bib_lines))) [] match_indeces)]
    res_object_list))

(defn parse_bib_file [path]
  (parse_bib_str (fs/read-all-lines path)))

(defn bibtex_valid?
  "Verify a string for being in bibtex formate"
  [bibtex]
  ;; todo
  true)

(expound/def ::BIBTEX-VALID
  #(bibtex_valid? %)
  "Invalid bibtex fiel")
