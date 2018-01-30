(ns zipcode.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as string]))

(defn remove-preceeding-zeros
  [zipcode]
  (re-find #"[^0][0-9]*" (str zipcode)))


(defn- process-irs-data
  "pull state-zipcode mappings out of the irs's full SOI csv"
  [in out]
  (->> (slurp in)
      (string/split-lines)
      (drop 1)
      (map (fn [line]
             (as-> line $
               (string/split $ #",")
               (do {:zipcode (nth $ 2), :state_code (nth $ 1)}))))
      (filter (comp not #{"00000" "99999"} :zipcode)) ; dummy values for missing/"other" zipcodes
      (into [])
      (#(with-open [w (clojure.java.io/writer out)]
          (binding [*out* w
                    *print-length* false]
            (pr %))))))

(comment
  (process-irs-data (io/resource "irs-soi-2015.csv") (io/resource "irs-soi-2015.edn")))

(def zipcode-db
  ;; first source is Google BigQuery `bigquery-public-data.utility_us.zipcode_area`
  ;; SELECT zipcode, state_code FROM `bigquery-public-data.utility_us.zipcode_area`
  ;; second source is irs 2015 statement of income data aggregated by state and zipcode
  (->> (edn/read-string (slurp (io/resource "zipcode-google-bigquery-database-20170713.edn")))
       (concat
        (edn/read-string (slurp (io/resource "irs-soi-2015.edn"))))
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
