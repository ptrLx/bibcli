(ns bibcli.main
  (:require [cli-matic.core :as cli]
            [bibcli.commands :as cmd]
            [bibcli.bibtex :as bibtex]
            [bibcli.git :as git]
            [bibcli.system :as system]))

(def ^:private opts_add_move
  [{:option "path"
    :as "path of resource"
    :type :string
    :default :present
    :spec ::system/PATH-VALID}
   {:option "bibtex"
    :short "b"
    :as "provide a bibtex fiel"
    :type :string
    :default nil
    :spec ::system/PATH-VALID}
   {:option "alias"
    :short "a"
    :as "alias name"
    :type :string
    :default nil
    :spec ::system/ALIAS-NOT-EXISTS}
   {:option "type"
    :short "t"
    :as "bibtex type"
    :type (set (keys bibtex/bibtex_types))
    :default nil}
   {:option "commit"
    :short "c"
    :as "commit to git"
    :type :with-flag
    :default false}
   {:option "push"
    :short "p"
    :as "push to git"
    :type :with-flag
    :default false}])

(def CONFIGURATION
  {:app         {:command     "bibcli"
                 :description "a simple tool to manage your bibliography resources locally"
                 :version     "0.0.1"}

   :global-opts []

   :commands    [{:command     "config"
                  :description "configure bibcli"
                  :examples    ["bibcli config --autocommit" "bibcli config --no-autopush"]
                  :opts        [{:option "autocommit"
                                 :as "automated commits to git"
                                 :type :with-flag
                                 :default nil}
                                {:option "autopush"
                                 :as "automated pushes to git"
                                 :type :with-flag
                                 :default nil}]
                  :runs cmd/config}
                 {:command     "initc"
                  :description "initialize the central repository ~/.bibcli"
                  :examples    ["bibcli initc" "bibcli initc --git"]
                  :opts        [{:option "git"
                                 :short "g"
                                 :as "also initialize as git repository"
                                 :type :with-flag
                                 :default false}]
                  :runs cmd/init_central}
                 {:command "addc"
                  :description "add a resource to the central repository"
                  :examples ["bibcli addc --path path/to/res --bibtex path/to/bibtex/file"
                             "bibcli addc --path path/to/res --alias <alias> -cp"]
                  :opts        opts_add_move
                  :runs cmd/add_central}
                 {:command "movec"
                  :description "move a resource to the central repository"
                  :examples ["bibcli movec --path path/to/res --bibtex path/to/bibtex/file"
                             "bibcli movec --path path/to/res --alias <alias> -cp"]
                  :opts        opts_add_move
                  :runs cmd/move_central}
                 {:command "rmc"
                  :description "delete resource from the central repository"
                  :examples ["bibcli rmc -a <alias>"]
                  :opts [{:option "alias"
                          :short "a"
                          :as "remove resource with this alias"
                          :type :string
                          :multiple true
                          :default :present
                          :spec ::system/ALIASES-EXISTS}]
                  :runs cmd/remove_central}
                 {:command "listc"
                  :description "list resources"
                  :examples ["bibcli listc"]
                ;;   :opts        [{:option "author"
                ;;                  :as "list only resources of this author"
                ;;                  :type :string}
                ;;                 {:option "type"
                ;;                  :short "t"
                ;;                  :as "list only resources of this bibtex type"
                ;;                  :type (set (keys bibtex/bibtex_types))
                ;;                  :default nil}]
                  :runs cmd/list_central}
                 {:command     "init"
                  :description "initialize a path as project. This will create a bib-ref file"
                  :examples    ["bibcli init -a <alias 1> -a <alias 2>"]
                  :opts        [{:option "alias"
                                 :short "a"
                                 :as "alias of resource you want to have in this project"
                                 :type :string
                                 :multiple true
                                 :spec  ::system/ALIASES-EXISTS}]
                  :runs cmd/init_local}
                 {:command "add"
                  :description "add resource to current project"
                  :examples ["bibcli add -a <alias 1> -a <alias 2>"
                        ;;      "bibcli add --author einstein"
                             ]
                  :opts [{:option "alias"
                          :short "a"
                          :as "add resource with this alias"
                          :type :string
                          :multiple true
                          :spec ::system/ALIASES-EXISTS}
                        ;;  {:option "author"
                        ;;   :as "add all resources from this authors"
                        ;;   :type :string
                        ;;   :multiple true}
                         ]
                  :runs cmd/add_local}
                 {:command "rm"
                  :description "delete resources from project"
                  :examples ["bibcli rm -a <alias 1> -a <alias 2>"]
                  :opts [{:option "alias"
                          :short "a"
                          :as "delete resources with this alias from current project"
                          :type :string
                          :multiple true
                          :spec ::system/ALIASES-EXISTS}]
                  :runs cmd/remove_local}
                 {:command "path"
                  :description "get path of a resource"
                  :examples ["bibcli path -a <alias>"]
                  :opts [{:option "alias"
                          :short "a"
                          :as "alias of resource"
                          :type :string
                          :spec ::system/ALIAS-EXISTS}]
                  :runs cmd/path}
                 {:command "generate"
                  :description "generate bib-tex output for this project"
                  :examples ["bibcli generate"
                             "bibcli generate -o path/to/bib-file"]
                  :opts [{:option "out"
                          :short "o"
                          :as "output path"
                          :type :string
                          :default "bib"
                          :spec ::system/BIB-REF-EXISTS}]
                  :runs cmd/generate}]})

(defn -main [& _args]
  (cli/run-cmd *command-line-args* CONFIGURATION))
