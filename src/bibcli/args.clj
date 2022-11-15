(ns bibcli.args)

(defn print_args
  "Print all arguments"
  [& args]
  (if (seq args)
    (doseq [arg args]
      (println arg))))

(defn parse_args
  "Parse and verify all arguments and return a map with what to do"
  ;; todo
  {:cmd "init", :git true, :configure_autocommit true, :configure_autopush true}
  ;; {:cmd "add", :autocommit true, :autopush true}
  )