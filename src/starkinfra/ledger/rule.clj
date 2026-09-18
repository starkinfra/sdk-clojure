(ns starkinfra.ledger.rule
  "The Ledger.Rule map modifies the behavior of Ledger maps when passed as an
  argument upon their creation or update.

  Ledger.Rule has no verbs of its own: sdk-python's ledger/rule module exposes
  only the Rule class and the hydration helper Ledger and LedgerTransaction use
  when reading their `:rules` field back from the API. Both send and receive
  rules as plain maps shaped like this namespace's docstring, so there is
  nothing here to call.

  ## Parameters (required):
    - `:key` [string]: Rule to be customized, describes what Ledger behavior will be altered. ex: \"minimumBalance\", \"maximumBalance\"
    - `:value` [integer]: Value of the rule. ex: 1000")
