(ns basic-clj.ast-test
  (:require [clojure.test :refer :all]
    [basic-clj.ast :refer :all]
    [basic-clj.tokenizer :refer :all]))

(deftest test-ast
  (is (= (ast []) (list)) "Empty list of tokens.")
  (is (= (ast [(cmd :print)]) '(print)) "Single token.")
  (is (= (ast [(cmd :print) (tval "Hello")]) '(print "Hello")) "Simple print.")
)