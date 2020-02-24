# language: en

@PI-1424 @list-voucher
Feature: Retrieve the List of Vouchers

  Background:
    Given exist the voucher types:
      | code  | product             | description    | merchant |
      | TIN1M | Tinder 1 Month Gold | 1 Months       | tinder   |
      | TIN3M | Tinder 1 Month Gold | 3 Months Sale! | bumble   |
    And exist the voucher:
      | code         | type  | status    | userId | transactionId |
      | V1ACTIVE     | TIN1M | ACTIVE    |        | TXN1          |
      | V1PURCHASEDJ | TIN1M | PURCHASED | JOHN   |               |
      | V3PURCHASEDJ | TIN3M | PURCHASED | JOHN   | TXN2          |
      | V3REDEEMEDJ  | TIN3M | REDEEMED  | JOHN   | TXN3          |
      | V1PURCHASEDB | TIN1M | PURCHASED | BOB    | TXN4          |

  # Retrieve the voucher types filtered
  Scenario Outline: List of voucher types
    When the operator requires the vouchers with type '<type>', status '<status>', userId '<userId>', merchantId '<merchantId>' and transactionId '<transactionId>'
    Then the user retrieve the list with '<result>' vouchers
    Examples:
      | type  | status    | userId | transactionId | merchantId | result                                                      |
      | TIN1M |           |        |               | tinder     | V1ACTIVE,V1PURCHASEDJ,V1PURCHASEDB                          |
      | TIN3M |           |        |               |            | V3PURCHASEDJ,V3REDEEMEDJ                                    |
      |       | ACTIVE    |        |               | tinder     | V1ACTIVE                                                    |
      |       | PURCHASED |        |               |            | V1PURCHASEDJ,V3PURCHASEDJ,V1PURCHASEDB                      |
      |       |           | BOB    |               |            | V1PURCHASEDB                                                |
      |       |           | JOHN   |               |            | V1PURCHASEDJ,V3PURCHASEDJ,V3REDEEMEDJ                       |
      |       | PURCHASED | JOHN   |               |            | V1PURCHASEDJ,V3PURCHASEDJ                                   |
      |       | PURCHASED | JOHN   | TXN2          |            | V3PURCHASEDJ                                                |
      | TIN1M | PURCHASED | JOHN   |               | tinder     | V1PURCHASEDJ                                                |
      |       |           |        |               |            | V1ACTIVE,V1PURCHASEDJ,V3PURCHASEDJ,V3REDEEMEDJ,V1PURCHASEDB |
      |       |           | MARIO  |               |            |                                                             |

  Scenario Outline: Voucher in error
    When the operator requires the vouchers with type '<type>', status '<status>', userId '', merchantId '' and transactionId ''
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | type  | status | errorCode      | errorDescription             |
      | wrong |        | TYPE_NOT_FOUND | Voucher Type wrong not found |
      |       | status | WRONG_STATUS   | Status status is wrong       |
