(ns basic-clj.tokenizer-test
  (:require [clojure.test :refer :all]
    [basic-clj.tokenizer :refer :all]))

(defn- cmd [value]
  {:type :command :value value})

(defn- op [value]
  {:type :operator :value value})

(defn- tvar [value]
  {:type :variable :value value})

(defn- val [value]
  {:type :value :value value})

(deftest test-tokenize
  (is (= (tokenize "") []))
  (is (= (tokenize "ab") [(cmd :ab)]) "Token")
  (is (= (tokenize "ab  cd") [(cmd :ab) (cmd :cd)]) "Spaces between tokens")
  (is (= (tokenize "  ab  cd") [(cmd :ab) (cmd :cd)]) "Leading spaces")
  (is (= (tokenize "\"Hello world\"") [(val "Hello world")]) "String")
  (is (= (tokenize "\"a b\"  \"c d\"") [(val "a b") (val "c d")]) "Multiple strings")
  (is (= (tokenize "3") [(val 3)]) "Integer")
  (is (= (tokenize "3 4") [(val 3) (val 4)]) "Multiple integers")
  (is (= (tokenize "3.5") [(val 3.5)]) "Float")
  (is (= (tokenize "3.") [(val 3.0)]) "Float ends in dot")
  (is (thrown? Exception (tokenize "3a")) "Invalid number")
  (is (= (tokenize "2 + 2") [(val 2) (op :+) (val 2)]) "Binary operator")
  (is (= (tokenize "2+2") [(val 2) (op :+) (val 2)]) "Binary operator. No spaces.")
  (is (= (tokenize "\"a\"+\"b\"") [(val "a") (op :+) (val "b")]) "Binary operator on strings. No spaces.")
  (is (= (tokenize "-1") [(op :-) (val 1)]) "Negative numbers is not a job for the tokenizer")
  (is (= (tokenize "$a") [(tvar :a)]) "Variable")
  (is (= (tokenize "$a + $b") [(tvar :a) (op :+) (tvar :b)]) "Multiple variables")
  (is (= (tokenize "$a+$b") [(tvar :a) (op :+) (tvar :b)]) "Multiple variables. No spaces.")
  (is (thrown? Exception (tokenize "$")) "Empty variable name is not allowed.")
)
