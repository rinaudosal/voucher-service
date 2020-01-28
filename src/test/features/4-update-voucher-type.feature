# language: en

@PI-1420 @update-voucher-type
Feature: Update Voucher types

  Background:
    Given exist the voucher types:
      | code            | description    | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | BVOUCHER1MONTHS | 1 Months       | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 |
      | WVOUCHER1MONTHS | 1 Months VDFAU | 9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 | 21/06/2020 |

  Scenario: Voucher type updated
    When the operator wants to update the voucher type 'BVOUCHER1MONTHS':
      | description | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | 1 Months UP | 19.99  | USD      | bumble   | DE      | VDFAU           | shop2 | false   | 01/02/2020 | 21/07/2020 |
    Then the operator update the voucher type correctly

  Scenario: Voucher type not found
    When the operator wants to update the voucher type 'PIPPO':
      | description | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | 1 Months UP | 19.99  | USD      | bumble   | DE      | VDFAU           | shop2 | false   | 01/02/2020 | 21/07/2020 |
    Then the user receive the error 'No Voucher type found'

  Scenario Outline: Voucher type without mandatory fields
    When the operator wants to update the voucher type 'BVOUCHER1MONTHS' without field '<field>':
      | description | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | 1 Months UP | 19.99  | USD      | bumble   | DE      | VDFAU           | shop2 | false   | 01/02/2020 | 21/07/2020 |
    Then the operator receive the error 'Invalid Voucher Type, '<field>' is mandatory'
    Examples:
      | field           |
      | amount          |
      | currency        |
      | merchant        |
      | country         |
      | paymentProvider |
      | shop            |
