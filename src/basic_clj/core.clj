(ns basic-clj.core
  (require [basic-clj.tokenizer :refer :all]
  [basic-clj.ast :refer :all]))

; When using 'lein run' args starts with the arguments.
; lein run foo -> ("foo")
; TODO: Fix PRINT to be a println. Introduce a runtime file with b-print
(defn -main
  "BASIC Interpreter"
  [& args]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]
    (eval (ast (tokenize line)))))
