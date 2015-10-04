(defproject zipcode "0.1.0"
  :description "Converts Zipcode to State (US only)"
  :url "https://github.com/ladderlife/zipcode"
  :license {:name "The MIT License (MIT)"
            :url "https://opensource.org/licenses/MIT"}
  :signing {:gpg-key "kijunseo@gmail.com"}
  :deploy-repositories [["clojars" {:creds :gpg}]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojure-csv "2.0.1"] ])
