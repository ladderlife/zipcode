(ns zipcode.core
  (:require [clojure-csv.core :as csv]))

(def zipcode-db (into {} (csv/parse-csv (slurp "resources/zipcode-database.csv"))))

(defn zipcode->state [zipcode] (zipcode-db (str zipcode)))
