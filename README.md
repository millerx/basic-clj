# BASIC-clj

BASIC interpreter written in Clojure.

## Usage

```
$ echo 'PRINT "Hello World!"' | lein run
```

## Structure

```
tokenizer.clj:  String to tokens.
ast.clj:        Tokens to Clojure form to be evaluated.
runtime.clj:    Contains BASIC implementation. Functions referenced in ast.clj.
core.clj:       Feed tokens into AST. Eval forms from AST.
```