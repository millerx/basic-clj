(ns basic-clj.ast)

; Takes tokens from the tokenizer and converts them into a Clojure form to be evaluated.
; Because of the homoiconography of LISP these symbols are a lot like an AST.
; BASIC commands map to functions in the runtime namespace.

(def ^{:private true}
  keyword-to-symbol
  (comp symbol name))

; Converts a command token into a Clojure function symbol.
; token -> symbol
(defn- ast-cmd [token]
  (keyword-to-symbol (:value token)))

; Converts a seq of tokens into a Clojure form.
; tokens -> form
(defn- ast-expr [tokens]
  (map :value tokens))

(defn ast
  "Takes tokens from the tokenizer and converts them into a Clojure form to be evaluated.
Because of the homoiconography of LISP these symbols are a lot like an AST.
BASIC commands map to functions in the runtime namespace."
  [[cmd-token & tokens]]
  (if (nil? cmd-token)
    []
    (conj (ast-expr tokens) (ast-cmd cmd-token))))
