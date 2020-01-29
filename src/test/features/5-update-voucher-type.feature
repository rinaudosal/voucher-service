# language: en

@PI-1420 @update-voucher-type
Feature: Update Voucher types

  Background:
    Given exist the voucher types:
      | code            |product   | description    |Promo |amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    | order |
      | BVOUCHER1MONTHS |Tinder 1 m| 1 Months       |      |9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 | 1     |
      | WVOUCHER1MONTHS |Tinder 3 m| 1 Months VDFAU | sale |9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 | 21/06/2020 | 0     |

  Scenario: Voucher type updated
    When the operator wants to update the voucher type 'BVOUCHER1MONTHS':
      |product      | description |promo |amount  | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    | order|
      |Tinder 1 m UP| 1 Months UP | p1m  | 19.99  | USD      | bumble   | DE      | VDFAU           | shop2 | false   | 01/02/2020 | 21/07/2020 | 1    |
    Then the operator update the voucher type correctly

  Scenario: Voucher type not found
    When the operator wants to update the voucher type 'PIPPO':
      |product      | description |promo |amount  | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    | order|
      |Tinder 1 m UP| 1 Months UP | p1m  | 19.99  | USD      | bumble   | DE      | VDFAU           | shop2 | false   | 01/02/2020 | 21/07/2020 | 1    |
    Then the user receive the error 'No Voucher type found'

  Scenario Outline: Voucher type without mandatory fields
    When the operator wants to update the voucher type 'BVOUCHER1MONTHS' without field '<field>':
      |product      | description |promo |amount  | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    | order|
      |Tinder 1 m UP| 1 Months UP | p1m  | 19.99  | USD      | bumble   | DE      | VDFAU           | shop2 | false   | 01/02/2020 | 21/07/2020 | 1    |
    Then the operator receive the error 'Invalid Voucher Type, '<field>' is mandatory'
    Examples:
      | field           |
      | amount          |
      | currency        |
      | product         |
      | merchant        |
      | country         |
      | paymentProvider |
      | shop            |
      | order           |
