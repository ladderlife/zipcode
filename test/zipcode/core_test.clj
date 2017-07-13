(ns zipcode.core-test
  (:require [clojure.test :refer :all]
            [zipcode.core :refer :all]))

(deftest zipcode->states-test
  (testing "accepts int and string as an input, removes preceeding zeros"
    (is (= #{"CA"} (zipcode->states 94061)))
    (is (= #{"CA" "OR"} (zipcode->states "97635")))
    (is (= #{"PR"}
           (zipcode->states "00601")
           (zipcode->states "0601")
           (zipcode->states "601")
           (zipcode->states 601)))))

(deftest zipcode->state-test
  (testing "works like zipcode->states but returns only one state"
    (is (= "CA" (zipcode->state 94061)))
    (is (= "CA" (zipcode->state "97635")))
    (is (= "PR"
           (zipcode->state "00601")
           (zipcode->state "0601")
           (zipcode->state "601")
           (zipcode->state 601)))))
