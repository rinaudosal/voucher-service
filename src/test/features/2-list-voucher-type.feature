# language: en

@PI-1420 @list-voucher-type
Feature: Retrieve the List of Voucher types available

  Background:
    Given exist the voucher types:
      |code           |description           |amount|currency|merchant|country|paymentProvider  |shop |enabled|startDate |endDate    |
      |BVOUCHER1MONTHS| 1 Months             |  9.99|INR     |tinder  |IN     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 |
      |WVOUCHER1MONTHS| 1 Months VDFAU       |  9.99|INR     |tinder  |IN     |VDFAU            |shop1|true   |01/01/2020|21/06/2020 |
      |BVOUCHER1MONTHS| 1 Months US          |  9.99|INR     |tinder  |US     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 |
      | VOUCHER1MONTHS| Badoo voucher        |  9.99|USD     |badoo   |IN     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 |
      |AVOUCHER3MONTHS| not in range         | 19.99|INR     |tinder  |US     |PAYTM            |shop2|true   |01/01/2020|20/02/2020 |
      |BVOUCHER3MONTHS| no available product | 19.99|INR     |tinder  |IN     |OTHER            |shop2|true   |01/01/2020|20/06/2020 |
      |AVOUCHER6MONTHS| 6 Months             | 39.99|EUR     |tinder  |IN     |PAYTM            |shop3|true   |01/01/2020|21/04/2020 |
      |VOUCHER9MONTHS | Voucher not enabled  | 49.99|EUR     |tinder  |US     |PAYTM            |shop3|false  |05/01/2020|21/02/2020 |

  # Retrieve the voucher types filtered
 Scenario Outline: List of voucher types
    When the operator requires the voucher with '<parameter>' '<value>'
    Then the user retrieve the list with <result> Element
   Examples:
     |parameter        |value |result|
     |                 |      |8     |
     |merchant         |tinder|7     |
     |currency         |INR   |5     |
     |country          |US    |3     |
     |paymentProvider  |PAYTM |6     |
     |shop             |shop1 |4     |
     |enabled          |false |1     |
     |enabled          |true  |7     |

  # Retrieve the voucher types filtered
 Scenario: List of voucher types by merchant, country and paymentProvider
    When the operator requires the voucher with merchant 'tinder' and country 'IN' and paymentProvider 'PAYTM'
    Then the user retrieve the list with 2 Element
