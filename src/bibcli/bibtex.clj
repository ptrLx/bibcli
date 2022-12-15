(ns bibcli.bibtex
  (:require [expound.alpha :as expound]))


(def fields #{:address :annote :author :booktitle :Email :chapter :crossref :doi :edition :editor
              :howpublished :institution :journal :key :month :note :number :organization :pages
              :publisher :school :series :title :type :volume :year})

;; (def types #{:article :book :booklet :conference :inbook :incollection :inproceedings
;;              :manual :mastersthesis :misc :phdthesis :proceedings :techreport :unpublished})

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
;; bibtex structure
  ;; {:type type
  ;;  :alias alias
  ;;  :attributes {:author "Einstein" :title ""}}

(defn tokenize [bibtex]
;; todo
  )



(defn parse_file
  "Read through a bibtex string and return as data"
  [bibtex])

;; (parse_file
;;  "
;;   @Book{abramowitz+stegun,
;;  author    = \"Milton {Abramowitz} and Irene A. {Stegun}\",
;;  title     = \"Handbook of Mathematical Functions with
;;               Formulas, Graphs, and Mathematical Tables\",
;;  publisher = \"Dover\",
;;  year      =  1964,
;;  address   = \"New York City\",
;;  edition   = \"ninth Dover printing, tenth GPO printing\"
;; }
;;   ")


(defn bibtex_valid?
  "Verify a string for being in bibtex formate"
  [bibtex]
  ;; todo
  true)

(expound/def ::BIBTEX-VALID
  #(bibtex_valid? %)
  "Invalid bibtex fiel")

(defn type
  "Get type ob bibtex entry"
  [bibtex]
  ;; todo
  )

(defn alias
  "Get alias ob bibtex entry"
  [bibtex]
  ;; todo
  "test-alias")

(defn print_format
  "todo"
  [type]
  (print "print_format not implemented!")
  (str "type is " type))

;; (defn requirements_satisfied?
;;   [bibtex]

;;   flag bool = false

;;   (let [required ((bibtex_types (bibtex :type)) :require)])

;;   ;; if required props are not present in attirbutes => open editor to fill in manually
;;   )

;; (requirements_satisfied? {:type :book :alias "alias" :attributes {:author "Einstein"}})
