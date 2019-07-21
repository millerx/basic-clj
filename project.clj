(defproject basic-clj "0.0.1-SNAPSHOT"
  :description "BASIC interpreter."
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :repl-options {
    :init-ns basic-clj.ast
    :init (require '[basic-clj.tokenizer :refer :all])}
  :main basic-clj.core)
