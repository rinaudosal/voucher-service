# language: en

@PI-1420 @create-voucher-type
Feature: Create Voucher types

  Background:
    Given exist the voucher types:
      | code            | description    | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | BVOUCHER1MONTHS | 1 Months       | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 |
      | WVOUCHER1MONTHS | 1 Months VDFAU | 9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 | 21/06/2020 |

  Scenario: Voucher type created
    When the operator wants to create the voucher type:
      | code           | description | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | NEWVOUCHERCODE | 1 Months    | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 |
    Then the operator create the voucher type correctly


  Scenario: Voucher type already exist
    When the operator wants to create the voucher type:
      | code            | description | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | BVOUCHER1MONTHS | 1 Months    | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 |
    Then the operator receive the error 'Voucher Type already exist'

  Scenario Outline: Voucher type without mandatory fields
    When the operator wants to create the voucher type without field '<field>':
      | code           | description | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    |
      | NEWVOUCHERCODE | 1 Months    | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 |
    Then the operator receive the error 'Invalid Voucher Type, '<field>' is mandatory'
    Examples:
      | field           |
      | code            |
      | amount          |
      | currency        |
      | merchant        |
      | country         |
      | paymentProvider |
      | shop            |
