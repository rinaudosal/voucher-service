# language: en

@PI-1420 @voucher-available
Feature: Retrieve the List of Voucher types available

  Background:
    Given exist the voucher types:
      |code           |description           |amount|currency|merchant|country|paymentProvider |shop |enabled|startDate |endDate    | Voucher Purchased | Voucher Active |
      |BVOUCHER1MONTHS| 1 Months             |  9.99|INR     |tinder  |IN     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 | 3                 | 3              |
      |BVOUCHER1MONTHS| 1 Months VDFAU       |  9.99|INR     |tinder  |IN     |VDFAU            |shop1|true   |01/01/2020|21/06/2020 | 3                 | 3              |
      |BVOUCHER1MONTHS| 1 Months US          |  9.99|INR     |tinder  |US     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 | 3                 | 3              |
      | VOUCHER1MONTHS| Badoo voucher        |  9.99|USD     |badoo   |IN     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 | 3                 | 5              |
      |AVOUCHER3MONTHS| not in range         | 19.99|USD     |tinder  |IN     |PAYTM            |shop2|true   |01/01/2020|20/02/2020 | 4                 | 2              |
      |BVOUCHER3MONTHS| no available product | 19.99|USD     |tinder  |IN     |PAYTM            |shop2|true   |01/01/2020|20/06/2020 | 4                 | 0              |
      |AVOUCHER6MONTHS| 6 Months             | 39.99|EUR     |tinder  |IN     |PAYTM            |shop1|true   |01/01/2020|21/04/2020 | 5                 | 4              |
      |VOUCHER9MONTHS | Voucher not enabled  | 49.99|USD     |tinder  |IN     |PAYTM            |shop1|false  |05/01/2020|21/02/2020 | 1                 | 1              |

  # Retrieve the voucher types available filtered for merchant
  Scenario: List of available voucher types
    And today is '21/02/2020'
    When the user of the merchant 'tinder' request the product available for the payment provider 'PAYTM' in country 'IN'
    Then the user retrieve the list:
      |code           |description|amount|currency| Voucher Available |
      |AVOUCHER6MONTHS| 6 Months  | 39.99|EUR     | 4                 |
      |BVOUCHER1MONTHS| 1 Months  |  9.99|INR     | 3                 |

  Scenario: Range without vouchers available
    And today is '21/02/2022'
    When the user of the merchant 'tinder' request the product available for the payment provider 'PAYTM' in country 'IN'
    Then the user receive the error 'No Vouchers available, try later'

  Scenario: Merchant without vouchers
    And today is '21/02/2020'
    When the user of the merchant 'me' request the product available for the payment provider 'PAYTM' in country 'IN'
    Then the user receive the error 'No Vouchers available, try later'
