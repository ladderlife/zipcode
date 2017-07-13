(ns zipcode.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as string]))

(defn remove-preceeding-zeros
  [zipcode]
  (re-find #"[^0][0-9]*" (str zipcode)))

(def zipcode-db
  ;; source is Google BigQuery `bigquery-public-data.utility_us.zipcode_area`
  ;; SELECT zipcode, state_code FROM `bigquery-public-data.utility_us.zipcode_area`
  (->> (edn/read-string (slurp (io/resource "zipcode-google-bigquery-database-20170713.edn")))
       (mapv (fn [{:keys [zipcode state_code]}]
               [(remove-preceeding-zeros zipcode)
                (->> (string/split state_code #", ") (map string/trim) set)]))
       (into {})))

(defn zipcode->states [zipcode]
  "returns a set of all states this zipcode is in"
  (let [trimmed (remove-preceeding-zeros zipcode)]
    (get zipcode-db trimmed)))

(defn zipcode->state [zipcode]
  "return the state this zipcode is in (first in alphabetical order when there are more than one)"
  (-> (zipcode->states zipcode)
      sort
      first))
