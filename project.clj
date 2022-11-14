(defproject bibcli "0.1"
  :description "A simple tool to manage your bibliography resources locally."
  :url "https://github.com/ptrLx/bibcli"
  :source-paths ["src"]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/java.jdbc "0.7.8"]]
  :main ^:skip-aot bibcli.core
  ;; :profiles {:uberjar {:aot :all}}
  )
