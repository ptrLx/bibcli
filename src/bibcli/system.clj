(ns bibcli.system
  (:require [expound.alpha :as expound]))

(defn path_valid?
  [path]
  false)

(expound/def ::PATH-VALID
  #(path_valid? %)
  "invalid path")

