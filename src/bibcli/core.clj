(ns bibcli.core
  (:gen-class)
  (:require [bibcli.args :as args]))

(defn -main
  [& args]
  (args/print_args args))

(-main "test" "asdf" 1 2 3)
