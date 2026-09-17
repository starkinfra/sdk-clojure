(ns starkinfra.issuing-rule
  "The IssuingRule map displays the spending rules of IssuingCards and IssuingHolders.

  IssuingRule has no verbs of its own: sdk-python's __issuingrule.py module
  exposes no create/get/query functions, only the IssuingRule class and the
  private hydration helpers that IssuingHolder and IssuingCard use when
  reading their `:rules` field back from the API. Both of those resources
  send and receive rules as plain maps shaped like this namespace's
  docstring, so there is nothing here to call.

  ## Parameters (required):
    - `:name` [string]: rule name. ex: \"Travel\" or \"Food\"
    - `:amount` [integer]: maximum amount that can be spent in the informed interval. ex: 200000 (= R$ 2000.00)

  ## Parameters (optional):
    - `:interval` [string, default \"lifetime\"]: interval after which the rule amount counter will be reset to 0. ex: \"instant\", \"day\", \"week\", \"month\", \"year\" or \"lifetime\"
    - `:currency-code` [string, default \"BRL\"]: code of the currency that the rule amount refers to. ex: \"BRL\" or \"USD\"
    - `:categories` [list of maps, default []]: merchant categories accepted by the rule. ex: [{:code \"fastFoodRestaurants\"}]
    - `:countries` [list of maps, default []]: countries accepted by the rule. ex: [{:code \"BRA\"}]
    - `:methods` [list of maps, default []]: card purchase methods accepted by the rule. ex: [{:code \"token\"}]
    - `:schedule` [string, default nil]: Optional schedule dictating when the rule can be used. Some examples: \"everyday from 09:00 to 18:00 in America/Sao_Paulo\" - every day, 09:00-18:00 Sao Paulo time; \"every monday, wednesday, friday from 08:00 to 12:00 in America/Sao_Paulo\" - only those weekdays, mornings; \"every saturday, sunday\" - weekends, all day, in UTC
    - `:purposes` [list of strings, default nil]: Optional list of transaction purposes the rule applies to. Options: \"purchase\", \"withdrawal\", \"verification\". The rule then limits only purchases of those purposes; omit it to allow any purposes. Example: [\"purchase\", \"verification\"] if you want us to automatically deny withdrawal.

  ## Attributes (expanded return-only):
    - `:id` [string]: unique id returned when an IssuingRule is created, used to update a specific IssuingRule. ex: \"5656565656565656\"
    - `:counter-amount` [integer]: current rule spent amount. ex: 1000
    - `:currency-symbol` [string]: currency symbol. ex: \"R$\"
    - `:currency-name` [string]: currency name. ex: \"Brazilian Real\"")
