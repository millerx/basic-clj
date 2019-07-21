(ns basic-clj.tokenizer-test
  (:require [clojure.test :refer :all]
    [basic-clj.tokenizer :refer :all]))

(deftest test-tokenize
  (is (= (tokenize "") []))
  (is (= (tokenize "ab") [(cmd :ab)]) "Token")
  (is (= (tokenize "ab  cd") [(cmd :ab) (cmd :cd)]) "Spaces between tokens")
  (is (= (tokenize "  ab  cd") [(cmd :ab) (cmd :cd)]) "Leading spaces")
  (is (= (tokenize "\"Hello world\"") [(tval "Hello world")]) "String")
  (is (= (tokenize "\"a b\"  \"c d\"") [(tval "a b") (tval "c d")]) "Multiple strings")
  (is (= (tokenize "3") [(tval 3)]) "Integer")
  (is (= (tokenize "3 4") [(tval 3) (tval 4)]) "Multiple integers")
  (is (= (tokenize "3.5") [(tval 3.5)]) "Float")
  (is (= (tokenize "3.") [(tval 3.0)]) "Float ends in dot")
  (is (thrown? Exception (tokenize "3a")) "Invalid number")
  (is (= (tokenize "2 + 2") [(tval 2) (op :+) (tval 2)]) "Binary operator")
  (is (= (tokenize "2+2") [(tval 2) (op :+) (tval 2)]) "Binary operator. No spaces.")
  (is (= (tokenize "\"a\"+\"b\"") [(tval "a") (op :+) (tval "b")]) "Binary operator on strings. No spaces.")
  (is (= (tokenize "-1") [(op :-) (tval 1)]) "Negative numbers is not a job for the tokenizer")
  (is (= (tokenize "$a") [(tvar :a)]) "Variable")
  (is (= (tokenize "$a + $b") [(tvar :a) (op :+) (tvar :b)]) "Multiple variables")
  (is (= (tokenize "$a+$b") [(tvar :a) (op :+) (tvar :b)]) "Multiple variables. No spaces.")
  (is (thrown? Exception (tokenize "$")) "Empty variable name is not allowed.")
  (is (= (tokenize "PRINT \"HELLO \" $A") [(cmd :print) (tval "HELLO ") (tvar :a)]) "Convert commands and variables to lower-case."))
