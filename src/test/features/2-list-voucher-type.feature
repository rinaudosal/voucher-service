# language: en

@PI-1420 @list-voucher-type
Feature: Retrieve the List of Voucher types available

  Background:
    Given exist the voucher types:
      |typeId           |product              |description          |Promo          |amount|currency|merchant|country|paymentProvider  |shop |enabled|startDate |endDate    |priority |
      |STD1MONTHS     |Tinder 1 Month Gold  |1 Months             |               |  9.99|INR     |tinder  |IN     |PAYTM            |shop1|true   |01/01/2020|31/12/2020 |0        |
      |SALE1MONTHS    |Tinder 1 Month Gold  |1 Months Sale!       | tnd1msale     |  7.99|INR     |tinder  |IN     |PAYTM            |shop1|true   |01/02/2020|28/02/2020 |1        |
      |SSALE1MONTHS   |Tinder 1 Month Gold  |1 Months Super Sale!!| tnd1msupersale|  5.99|INR     |tinder  |IN     |PAYTM            |shop1|true   |01/08/2020|15/08/2020 |2        |
      |WVOUCHER1MONTHS|Tinder 1 Month Gold  |1 Months VDFAU       |               |  9.99|INR     |tinder  |IN     |VDFAU            |shop1|true   |01/01/2020|21/06/2020 |0        |
      |BVOUCHER1MONTHS|Tinder 1 Month Gold  |1 Months US          |               |  9.99|INR     |tinder  |US     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 |0        |
      | VOUCHER1MONTHS|Badoo 1 Month Gold   |Badoo voucher        |               |  9.99|USD     |badoo   |IN     |PAYTM            |shop1|true   |01/01/2020|21/06/2020 |0        |
      |AVOUCHER3MONTHS|Tinder 3 Months Gold |not in range         |               | 19.99|INR     |tinder  |US     |PAYTM            |shop2|true   |01/01/2020|20/02/2020 |0        |
      |BVOUCHER3MONTHS|Tinder 3 Months Gold |no available product |               | 19.99|INR     |tinder  |IN     |OTHER            |shop2|true   |01/01/2020|20/06/2020 |0        |
      |AVOUCHER6MONTHS|Tinder 6 Month Gold  |6 Months             |               | 39.99|EUR     |tinder  |IN     |PAYTM            |shop3|true   |01/01/2020|21/04/2020 |0        |
      |VOUCHER9MONTHS |Tinder 9 Month Gold  |Voucher not enabled  |               | 49.99|EUR     |tinder  |US     |PAYTM            |shop3|false  |05/01/2020|21/02/2020 |0        |

  # Retrieve the voucher types filtered
 Scenario Outline: List of voucher types
    When the operator requires the voucher with '<parameter>' '<value>'
    Then the user retrieve the list with <result> Element
   Examples:
     |parameter        |value |result|
     |                 |      |10    |
     |merchant         |tinder|9     |
     |currency         |INR   |7     |
     |country          |US    |3     |
     |paymentProvider  |PAYTM |8     |
     |shop             |shop1 |6     |
     |enabled          |false |1     |
     |enabled          |true  |9     |

  # Retrieve the voucher types filtered
 Scenario: List of voucher types by merchant, country and paymentProvider
    When the operator requires the voucher with merchant 'tinder' and country 'IN' and paymentProvider 'PAYTM'
    Then the user retrieve the list with 4 Element
