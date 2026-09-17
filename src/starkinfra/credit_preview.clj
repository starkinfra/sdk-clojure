(ns starkinfra.credit-preview
  "A CreditPreview is used to get information from a credit before taking it.
  This resource can be used to preview credit notes.

  ## Parameters (required):
    - `:credit` [map]: information preview of the informed credit. A map with the CreditNotePreview shape below, when `:type` is \"credit-note\". ex: {:type \"sac\" :nominal-amount 90583 :scheduled \"2022-06-28\" :tax-id \"477.954.506-44\"}
    - `:type` [string]: credit type. ex: \"credit-note\"

  A CreditNotePreview (the `:credit` map, when `:type` is \"credit-note\") carries these fields:
    - `:type` [string]: table type that defines the amortization system. Options: \"sac\", \"price\", \"american\", \"bullet\", \"custom\"
    - `:scheduled` [string]: date of transfer execution. ex: \"2020-03-10\"
    - `:tax-id` [string]: credit receiver's tax ID (CPF or CNPJ). ex: \"20.018.183/0001-80\"
    - `:invoices` [list of maps]: list of Invoice maps to be created and sent to the credit receiver. Only used when `:type` is \"custom\". ex: [{:amount 14500 :due \"2022-08-19\"}]
    - `:nominal-amount` [integer]: amount in cents transferred to the credit receiver, before deductions, for every type including \"custom\". Provide exactly one of `:nominal-amount` or `:amount`; the other value, along with `:tax-amount` and the interest rates, is computed from the invoice schedule. ex: 11234 (= R$ 112.34)
    - `:amount` [integer]: net amount in cents to be disbursed to the credit receiver, for every type including \"custom\". Provide exactly one of `:nominal-amount` or `:amount`; the other value, along with `:tax-amount` and the interest rates, is computed from the invoice schedule.
    - `:nominal-interest` [float]: yearly nominal interest rate of the credit note, in percentage. Required for \"sac\", \"price\", \"american\" and \"bullet\". ex: 12.5
    - `:initial-due` [string]: date of the first invoice. Required for \"sac\", \"price\", \"american\" and \"bullet\". ex: \"2020-03-10\"
    - `:count` [integer]: quantity of invoices for payment. Required for \"american\"; for \"sac\" and \"price\", exactly one of `:count` or `:initial-amount` is required. ex: 12
    - `:initial-amount` [integer]: value of the first invoice in cents. For \"sac\" and \"price\", exactly one of `:count` or `:initial-amount` is required. ex: 1234 (= R$12.34)
    - `:interval` [string]: interval between invoices. Required for \"sac\" and \"price\". ex: \"year\", \"month\"
    - `:rebate-amount` [integer, default nil]: credit analysis fee deducted from lent amount. ex: 11234 (= R$ 112.34)
    - `:interest` [float]: yearly effective interest rate of the credit note, in percentage. ex: 12.5
    - `:tax-amount` [integer]: tax amount included in the CreditNote. ex: 100"
  (:require [starkinfra.settings :refer [credentials]]
            [starkinfra.utils.rest :refer [post-multi]]))

(defn- resource []
  "credit-preview")


(defn create
  "Send a list of CreditPreview maps for processing in the Stark Infra API.

  ## Parameters (required):
    - `previews` [list of maps]: list of CreditPreview maps to be created in the API.

  ## Parameters (optional):
    - `user` [map, default nil]: Project or Organization map returned from starkinfra.user/project or starkinfra.user/organization. Only necessary if starkinfra.settings/user has not been set.

  ## Return:
    - list of CreditPreview maps with updated attributes"
  ([previews]
   (post-multi @credentials (resource) previews {}))

  ([previews user]
   (post-multi user (resource) previews {})))
