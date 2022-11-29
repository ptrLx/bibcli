(ns bibcli.bibtex
  (:require [expound.alpha :as expound]))

(def types #{:article :book :booklet :conference :inbook :incollection :inproceedings
             :manual :mastersthesis :misc :phdthesis :proceedings :techreport :unpublished})

(def fields #{:address :annote :author :booktitle :Email :chapter :crossref :doi :edition :editor
              :howpublished :institution :journal :key :month :note :number :organization :pages
              :publisher :school :series :title :type :volume :year})


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
  "invalid bibtex fiel")
