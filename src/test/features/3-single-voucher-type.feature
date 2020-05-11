# language: en

@PI-1420 @voucher-type
Feature: Retrieve the List of Voucher types available

  Background:
    Given exist the voucher types:
      | typeId          | description    | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  					| endDate  					  |
      | BVOUCHER1MONTHS | 1 Months       | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 |
      | WVOUCHER1MONTHS | 1 Months VDFAU | 9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 |

  # Retrieve the voucher type
  Scenario: Voucher type found
    When the operator requires the voucher type with code 'WVOUCHER1MONTHS'
    Then the user retrieve the voucher type

  Scenario: Voucher type not found
    When the operator requires the voucher type with code 'MARIO'
    Then the user receive the error 'No Voucher type found'

