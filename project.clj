(defproject com.ladderlife/zipcode "0.1.0"
  :description "Converts Zipcode to State (US only)"
  :url "https://github.com/ladderlife/zipcode"
  :license {:name "The MIT License (MIT)"
            :url "https://opensource.org/licenses/MIT"}
  :repositories [["clojars" {:sign-releases false}]]
  :jar-exclusions [#".DS_Store" #"test" #"README" #"LICENSE"]
  :dependencies [[org.clojure/clojure "1.8.0" :scope "provided"]])
