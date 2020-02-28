# language: en

@PI-1420 @voucher-available
Feature: Retrieve the List of Voucher types available

  Background:
    Given exist the voucher types:
      | typeId          | product               | description           | promo          | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  | endDate    | priority | Voucher Purchased | Voucher Active |
      | STD1MONTHS      | Tinder 1 Month Gold   | 1 Months              |                | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 | 0        | 3                 | 3              |
      | SALE1MONTHS     | Tinder 1 Month Gold   | 1 Months Sale!        | tnd1msale      | 7.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/02/2020 | 28/02/2020 | 1        | 2                 | 1              |
      | SSALE1MONTHS    | Tinder 1 Month Gold   | 1 Months Super Sale!! | tnd1msupersale | 5.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/08/2020 | 15/08/2020 | 2        | 1                 | 2              |
      | BVOUCHER1MONTHS | Tinder 1 Month Bronze | 1 Months VDFAU        |                | 9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 | 21/06/2020 | 0        | 3                 | 3              |
      | BVOUCHER1MONTHS | Tinder 1 Month Bronze | 1 Months US           |                | 9.99   | INR      | tinder   | US      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 | 0        | 3                 | 3              |
      | VOUCHER1MONTHS  | Badoo 1 Month Gold    | Badoo voucher         |                | 9.99   | USD      | badoo    | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/06/2020 | 0        | 3                 | 5              |
      | AVOUCHER3MONTHS | Tinder 3 Months Gold  | not in range          |                | 19.99  | USD      | tinder   | IN      | PAYTM           | shop2 | true    | 01/01/2020 | 20/02/2020 | 0        | 4                 | 2              |
      | BVOUCHER3MONTHS | Tinder 3 Months Gold  | no available product  |                | 19.99  | USD      | tinder   | IN      | PAYTM           | shop2 | true    | 01/01/2020 | 20/06/2020 | 0        | 4                 | 0              |
      | AVOUCHER6MONTHS | Tinder 6 Month Gold   | 6 Months              |                | 39.99  | EUR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 | 21/04/2020 | 0        | 5                 | 4              |
      | VOUCHER9MONTHS  | Tinder 9 Month Gold   | Voucher not enabled   |                | 49.99  | USD      | tinder   | IN      | PAYTM           | shop1 | false   | 05/01/2020 | 21/02/2020 | 0        | 1                 | 1              |

  # Retrieve the voucher types available filtered for merchant
  Scenario: List of available voucher types
    And today is '21/02/2020'
    When the user of the merchant 'tinder' request the product available for the payment provider 'PAYTM' in country 'IN'
    Then the user retrieve the list:
      | typeId          | description    | amount | currency | Voucher Available |
      | AVOUCHER6MONTHS | 6 Months       | 39.99  | EUR      | 4                 |
      | SALE1MONTHS     | 1 Months Sale! | 7.99   | INR      | 1                 |

  Scenario: Range without vouchers available
    And today is '21/02/2022'
    When the user of the merchant 'tinder' request the product available for the payment provider 'PAYTM' in country 'IN'
    Then the user receive the error 'No Vouchers available, try later'

  Scenario: Merchant without vouchers
    And today is '21/02/2020'
    When the user of the merchant 'me' request the product available for the payment provider 'PAYTM' in country 'IN'
    Then the user receive the error 'No Vouchers available, try later'
