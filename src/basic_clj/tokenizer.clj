(ns basic-clj.tokenizer)

; Tokenizers are tedious and this one is just good enough for me.
; Better off replacing with https://github.com/Engelberg/instaparse or https://cljcc.com/

; tokenizer state is {:str :token-start :mode :token-type :value-type :tokens}
; mode is :rem :string :symbol :operator :whitespace
; token-type is :command :operator :variable :value
; value-type is :string :integer :float

; Starts a token of the given type at index and returns the new state.
; Throws an error if a token is already started.
; [state index :mode <mode> ...] -> state
(defn- start-token
  [state index & {:as new-state}]
  (if (< (:token-start state) 0)
    (merge (assoc state
      :token-start index) new-state)
    (throw (ex-info "Token already started."
      {:state state :index index :new-state new-state}))))

; Normalize keywords in lower-case.
(defn- to-keyword [str]
  (keyword (.toLowerCase str)))

; Converts state into a token value.
; state token-end -> token-value
(defn- to-token-value
  [{:keys [str token-start token-type value-type] :as state} token-end]
  (let [token-str (.substring str token-start token-end)]
    (case token-type
      :command (to-keyword token-str)
      :operator (to-keyword token-str)
      :variable (if (.isEmpty token-str)
        (throw (ex-info "Empty variable name." {:state state}))
        (to-keyword token-str))
      :value (case value-type
        :integer (Integer/parseInt token-str)
        :float (Float/parseFloat token-str)
        :string token-str
        :else (throw (ex-info "Unknown value-type." {:value-type value-type :state state})))
      :else (throw (ex-info "Unknown token-type." {:token-type token-type :state state})))))

; Converts the current state into a token.
; Performs a substring between token-start and token-end.
; [state token-end] -> token
(defn- to-token
  [state token-end]
  {:type (:token-type state)
   :value (to-token-value state token-end)})

; Conjs a token to the :tokens key of the given state. Performs a substring between
; :token-start and the given token-end.
; [state token-end new-mode] -> state
(defn- conj-token
  [state token-end new-mode]
  (let [new-token (to-token state token-end)]
    (assoc state
      :tokens (conj (:tokens state) new-token)
      :token-start -1
      :mode new-mode)))

(defn- is-operator [c]
  (#{\+ \- \* \/ \= \%} c))

; Creates the next state based on the index.
; [state i] -> state
(defn- tokenize-step
  [state i]
  ;(prn ">> tokenize-step" state i)
  (let [c (get (:str state) i)]
    (case (:mode state)
      :whitespace
        (cond
          (Character/isSpaceChar c)
            state
          (= c \")
            ; Start token at i+1 to omit opening quote.
            (start-token state (inc i) :mode :string :token-type :value :value-type :string)
          (= c \$)
            ; Start token at i+1 to omit dollar sign indicating a variable.
            (start-token state (inc i) :mode :symbol :token-type :variable)
          (is-operator c)
            (start-token state i :mode :operator :token-type :operator)
          (Character/isDigit c)
            (start-token state i :mode :symbol :token-type :value :value-type :integer)
          (Character/isJavaIdentifierStart c)
            (start-token state i :mode :symbol :token-type :command)
          :else (throw (ex-info "Invalid start of symbol." {:char c :index i})))
      :string
        (if (= c \")
          (conj-token state i :whitespace)
          state)
      :operator
        (cond
          (is-operator c)
            state
          :else
            ; conj the operator. Look at this same token as if we came from whitespace.
            (tokenize-step (conj-token state i :whitespace) i))
      :symbol
        (cond
          (Character/isSpaceChar c)
            (conj-token state i :whitespace)
          (is-operator c)
            ; conj the current integer token and start an operator token.
            (start-token (conj-token state i :whitespace) i :mode :operator :token-type :operator)
          (and (= :integer (:value-type state)) (= c \.))
            (assoc state :value-type :float)
          :else state)
      (throw (ex-info "Unknown mode." {:mode (:mode state)}))
    )))

; Produces a list of tokens from the final state after reducing over a line of BASIC code.
; [state] -> (tokens)
(defn- eol
  [{:keys [str token-start] :as state}]
  ;(prn ">> eol" state)
  (:tokens (if (>= token-start 0)
    (conj-token state (.length str) :whitespace)
    state)))

(defn tokenize
  "Tokenize a string and return a list of tokens."
  [str]
  (eol (reduce tokenize-step
    {:str str :token-start -1 :mode :whitespace :tokens []}
    (range (.length str)))))

; Functions for creating tokens for testing.
(defn cmd [value]
  {:type :command :value value})

(defn op [value]
  {:type :operator :value value})

(defn tvar [value]
  {:type :variable :value value})

(defn tval [value]
  {:type :value :value value})
