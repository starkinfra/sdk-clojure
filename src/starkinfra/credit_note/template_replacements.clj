(ns starkinfra.credit-note.template-replacements
  "Custom values that fill the placeholders of the contract template of a CreditNote.

  CreditNote.TemplateReplacements has no verbs of its own: sdk-python's
  creditnote/templatereplacements module exposes only the TemplateReplacements
  class CreditNote uses when reading its `:template-replacements` field back
  from the API. A CreditNote is created with a plain map in its
  `:template-replacements` key and reads it back the same way, so there is
  nothing here to call.

  It has no fixed keys: they depend on the contract template.

  ## Parameters (optional):
    - any key [keyword or string]: placeholder name. Write it as a kebab-case keyword, as in the rest of the SDK: `:guarantor-name` is sent as `guarantorName`. A camelCase string key (`\"guarantorName\"`) is sent as written. Keys with nil values are not sent. ex: {:guarantor-name \"Jon Snow\" :guarantor-tax-id \"012.345.678-90\"}")
