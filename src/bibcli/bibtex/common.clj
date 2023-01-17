(ns bibcli.bibtex.common)

;; Helper: own predicate function
(defn coll-but-not-map?
  [coll]
  (and (coll? coll)
       (not (map? coll))))

;; Recur JVM optimisation with tail recursion
(defn help_do_flat_coll
  "This function will unpack a nested collection of list or vector to a flat collection."
  [coll]
  ((fn unpack_coll [coll]
     (if (some true? (map coll-but-not-map? coll))
       (recur (reduce #(if (coll? %2) (apply conj %1 %2) (conj %1 %2)) [] coll))
       coll)) coll))

(defn bib_object_to_string
  "Converting a map which represents a valid bib text to a list of formated strings"
  [bib_obj]
  (letfn [(body_line [key value]
            (format "%-2s %-10s = {%s}," "" key value))
          (rm_last_char [s]
            (subs s 0 (- (count s) 1)))]
    (let [body_data (dissoc bib_obj "entrytype" "citekey")
          head (format "@%s{%s," (name (bib_obj "entrytype")) (bib_obj "citekey"))
          body (map #(body_line %1 %2) (keys body_data) (vals body_data))
          tail ["}" ""]]
      (help_do_flat_coll [head (drop-last body) (rm_last_char (last body)) tail]))))

;; OLD VERSION DELETE THIS
(comment
(defn- bib_object_to_string
  "Converting a map which represents a valid bib text to a list of formated strings"
  [bib_object]

  (let [body_data (dissoc bib_object "entrytype" "citekey")]
    (apply conj
           (conj [] (format "@%s{%s," (name (bib_object "entrytype")) (bib_object "citekey")))
           (conj
            (vec
             (doall (map #(format "%-2s %-10s = \"%s\"," "" %1 %2) (keys body_data) (vals body_data)))) "}" ""))))
)
