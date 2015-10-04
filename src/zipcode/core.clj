(ns zipcode.core
  (:require [clojure.java.io :as io]
            [clojure-csv.core :as csv]))

(def zipcode-db (into {} (csv/parse-csv (slurp (io/resource "zipcode-database.csv")))))

(defn zipcode->state [zipcode] (zipcode-db (str zipcode)))
