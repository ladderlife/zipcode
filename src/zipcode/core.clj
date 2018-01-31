(ns zipcode.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.set :as set]
            [clojure.string :as string]))

(defn remove-preceeding-zeros
  [zipcode]
  (re-find #"[^0][0-9]*" (str zipcode)))

(defn- process-irs-data
  "pull state-zipcode mappings out of the irs's full SOI csv"
  [in out]
  (let [db (->> (slurp in)
                (string/split-lines)
                (drop 1)
                (map (fn [line]
                       (as-> line $
                         (string/split $ #",")
                         (do [(remove-preceeding-zeros (nth $ 2)) (nth $ 1)]))))
                (remove (comp #(contains? #{nil "99999"} %) first)) ; dummy values for missing/"other" zipcodes
                (group-by first)
                (map (fn [[k v]]
                       [k (->> v (map second) (set))]))
                (into {}))]
    (with-open [w (clojure.java.io/writer out)]
      (binding [*out* w
                *print-length* false]
        (pr db)))))

(comment
  ;; data source is the irs public data website
  ;; https://www.irs.gov/statistics/soi-tax-stats-individual-income-tax-statistics-zip-code-data-soi
  (process-irs-data (io/resource "15zpallnoagi.csv") (io/resource "irs-soi-2015.edn")))

(def zipcode-irs-db
  (edn/read-string (slurp (io/resource "irs-soi-2015.edn"))))

(def zipcode-bq-db
  ;; source is Google BigQuery `bigquery-public-data.utility_us.zipcode_area`
  ;; SELECT zipcode, state_code FROM `bigquery-public-data.utility_us.zipcode_area`
  (->> (edn/read-string (slurp (io/resource "zipcode-google-bigquery-database-20170713.edn")))
       (mapv (fn [{:keys [zipcode state_code]}]
               [(remove-preceeding-zeros zipcode)
                (->> (string/split state_code #", ") (map string/trim) set)]))
       (into {})))

(def zipcode-db
  (merge-with set/union zipcode-bq-db zipcode-irs-db))

(defn zipcode->states [zipcode]
  "returns a set of all states this zipcode is in"
  (let [trimmed (remove-preceeding-zeros zipcode)]
    (get zipcode-db trimmed)))

(defn zipcode->state [zipcode]
  "return the state this zipcode is in (first in alphabetical order when there are more than one)"
  (-> (zipcode->states zipcode)
      sort
      first))
