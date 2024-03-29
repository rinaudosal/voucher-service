# language: en

@consume-voucher
Feature: Consume Voucher

  Background:
    Given exist the voucher types:
      | typeId  | product              | description           | Promo          | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  				 | endDate    				 | priority |
      | TIN1M   | Tinder 1 Month Gold  | 1 Months              |                | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 22:00:00 | 31/12/2020 22:00:00 | 0        |
      | TIN1S   | Tinder 1 Month Gold  | 1 Months Sale!        | tnd1msale      | 7.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/02/2020 22:00:00 | 18/08/2020 22:00:00 | 1        |
      | TIN1SS  | Tinder 1 Month Gold  | 1 Months Super Sale!! | tnd1msupersale | 5.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/08/2020 22:00:00 | 15/08/2020 22:00:00 | 2        |
      | TIN1VDF | Tinder 1 Month Gold  | 1 Months VDFAU        |                | 9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 22:00:00 | 21/06/2020 22:00:00 | 0        |
      | TIN1US  | Tinder 1 Month Gold  | 1 Months US           |                | 9.99   | INR      | tinder   | US      | PAYTM           | shop1 | true    | 01/01/2020 22:00:00 | 21/06/2020 22:00:00 | 0        |
      | TIN1BD  | Badoo 1 Month Gold   | Badoo voucher         |                | 9.99   | USD      | badoo    | IN      | PAYTM           | shop1 | true    | 01/01/2020 22:00:00 | 21/06/2020 22:00:00 | 0        |
      | TIN3EX  | Tinder 3 Months Gold | not in range          |                | 19.99  | INR      | tinder   | US      | PAYTM           | shop2 | true    | 01/01/2020 22:00:00 | 20/02/2020 22:00:00 | 0        |
      | TIN3NO  | Tinder 3 Months Gold | no available product  |                | 19.99  | INR      | tinder   | IN      | PAYTM           | shop2 | true    | 01/01/2020 22:00:00 | 20/06/2020 22:00:00 | 0        |
      | TIN6M   | Tinder 6 Month Gold  | 6 Months              |                | 39.99  | EUR      | tinder   | IN      | PAYTM           | shop3 | true    | 01/01/2020 22:00:00 | 21/04/2020 22:00:00 | 0        |
      | TIN9D   | Tinder 9 Month Gold  | Voucher not enabled   |                | 49.99  | EUR      | tinder   | US      | PAYTM           | shop3 | false   | 05/01/2020 22:00:00 | 21/02/2020 22:00:00 | 0        |
    And exist the voucher:
      | code          | typeId  | status    |
      | V1ACTIVE      | TIN1M   | ACTIVE    |
      | V1PURCHASED   | TIN1M   | PURCHASED |
      | V1SACTIVE     | TIN1S   | ACTIVE    |
      | V1SPURCHASED  | TIN1S   | PURCHASED |
      | V1SSACTIVE    | TIN1SS  | ACTIVE    |
      | V1SSPURCHASED | TIN1SS  | PURCHASED |
      | V1VDFACTIVE   | TIN1VDF | ACTIVE    |
      | V1USACTIVE    | TIN1US  | ACTIVE    |
      | V1BDACTIVE    | TIN1BD  | ACTIVE    |
      | V3EXACTIVE    | TIN3EX  | ACTIVE    |
      | V6PURCHASED   | TIN6M   | PURCHASED |
      | V9DACTIVE     | TIN9D   | ACTIVE    |

  Scenario Outline: Voucher consumed
    And today is '<billedDate>'
    When the operator wants to consume the voucher billed for shop 'shop1', product 'Tinder 1 Month Gold', country 'IN' and paymentProvider 'PAYTM'
    Then the operator receive the voucher '<code>' correctly
    And notification will be sent to requestor without error
    Examples:
      | billedDate | code       |
      | 13/01/2020 | V1ACTIVE   |
      | 13/02/2020 | V1SACTIVE  |
      | 13/08/2020 | V1SSACTIVE |
      | 16/08/2020 | V1SACTIVE  |

  Scenario Outline: Voucher consume in error
    And today is '<billedDate>'
    When the operator wants to consume the voucher billed for shop '<shop>', product '<product>', country '<country>' and paymentProvider '<paymentProvider>' receiving the error code '<errorCode>' and description '<errorDescription>'
    And notification will be sent to requestor with error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | shop  | product             | country | paymentProvider | billedDate | errorCode      | errorDescription                                                                                              |
      | shop3 | Tinder 1 Month Gold | IN      | PAYTM           | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop3, paymentProvider PAYTM, country IN and product Tinder 1 Month Gold  |
      | shop1 | Tinder 122 Month    | IN      | PAYTM           | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider PAYTM, country IN and product Tinder 122 Month     |
      | shop1 | Tinder 1 Month Gold | AU      | PAYTM           | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider PAYTM, country AU and product Tinder 1 Month Gold  |
      | shop1 | Tinder 1 Month Gold | IN      | VTCPAY          | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider VTCPAY, country IN and product Tinder 1 Month Gold |
      | shop1 | Tinder 1 Month Gold | IN      | PAYTM           | 31/12/2019 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider PAYTM, country IN and product Tinder 1 Month Gold  |
      | shop1 | Tinder 3 Month Gold | IN      | PAYTM           | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider PAYTM, country IN and product Tinder 3 Month Gold  |
      | shop1 | Tinder 6 Month Gold | IN      | PAYTM           | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider PAYTM, country IN and product Tinder 6 Month Gold  |
      | shop1 | Tinder 9 Month Gold | IN      | PAYTM           | 31/01/2020 | TYPE_NOT_FOUND | No Voucher Type available for shop shop1, paymentProvider PAYTM, country IN and product Tinder 9 Month Gold  |
