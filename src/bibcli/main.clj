(ns bibcli.main
  (:require [cli-matic.core :as cli]
            [bibcli.commands :as cmd]))

(def CONFIGURATION
  {:app         {:command     "bibcli"
                 :description "a simple tool to manage your bibliography resources locally"
                 :version     "0.0.1"}

   :global-opts []

   :commands    [{:command     "config"
                  :description "configure bibcli"
                  :examples    ["bibcli configure --autocommit" "bibcli configure --no-autopush"]
                  :opts        [{:option "autocommit"
                                 :as "automated commits to git"
                                 :type :with-flag
                                 :default false}
                                {:option "autopush"
                                 :as "automated pushes to git"
                                 :type :with-flag
                                 :default false}]
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
                  :opts        [{:option "path"
                                 :as "path of resource"
                                 :type :string
                                 :default :present}
                                {:option "bibtex"
                                 :as "provied a bibtex fiel"
                                 :type :string}
                                {:option "alias"
                                 :as "alias name"
                                 :type :keyword}
                                {:option "type"
                                 :as "bibtex type"
                                 :type #{"article" "misc"} ;;todo
                                 :default "misc"}
                                {:option "commit"
                                 :as "commit to git"
                                 :type :with-flag
                                 :default false}
                                {:option "push"
                                 :as "push to git"
                                 :type :with-flag
                                 :default false}]
                  :runs cmd/add_central}
                 {:command "movec"
                  :description "move a resource to the central repository"
                  :opts        [{:option "path"
                                 :as "path of resource"
                                 :type :string
                                 :default :present}
                                {:option "bibtex"
                                 :as "provied a bibtex fiel"
                                 :type :string}
                                {:option "alias"
                                 :as "alias name"
                                 :type :keyword}
                                {:option "type"
                                 :as "bibtex type"
                                 :type #{"article" "misc"} ;;todo
                                 :default "misc"}
                                {:option "commit"
                                 :as "commit to git"
                                 :type :with-flag
                                 :default false}
                                {:option "push"
                                 :as "push to git"
                                 :type :with-flag
                                 :default false}]
                  :runs cmd/move_central}
                 {:command "rmc"
                  :description "delete resource from the central repository"}
                 {:command "listc"
                  :description "list resources"
                  :opts        [{:option "author"
                                 :as "list only resources of this author"
                                 :type :string}
                                {:option "type"
                                 :as "list only resources of this type"}]
                  :runs cmd/remove_central}
                 {:command     "init"
                  :description "initialize a path as project. This will create a bib-ref file"
                  :examples    ["bibcli init"]
                  :opts        [{:option "path"
                                 :as "path that should be used. Leave empty to use current path"
                                 :type :string
                                 :default "."}
                                {:option "resources"
                                 :as "list of resource-aliases you want to have in this project"
                                 :type :keyword
                                 :multiple true}]
                  :runs cmd/init_local}
                 {:command "add"
                  :description "add resource to current projet"
                  :opts [{:option "aliases"
                          :as "todo"
                          :type :keyword
                          :multiple true}
                         {:option "author"
                          :as "todo"
                          :type :string
                          :multiple true}]
                  :runs cmd/add_local}
                 {:command "rm"
                  :description "delete resources from project"
                  :opts [:option "aliases"
                         :as "todo"
                         :type :keyword
                         :multiple true]
                  :runs cmd/remove_local}
                 {:command "path"
                  :description "get path of a resource"
                  :opts [:option "alias"
                         :as "todo"
                         :type :keyword]
                  :runs cmd/path}
                 {:command "generate"
                  :description "generate bib-tex output for this project"
                  :opts [:option "out"
                         :short "o"
                         :as "output path"
                         :type :string
                         :default "bib"]
                  :runs cmd/generate}]})

(defn -main [& _args]
  (cli/run-cmd *command-line-args* CONFIGURATION))
