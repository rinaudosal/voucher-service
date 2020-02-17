# language: en

@PI-1424 @list-voucher
Feature: Retrieve the List of Vouchers

  Background:
    Given exist the voucher types:
      | code  | product             | description    |
      | TIN1M | Tinder 1 Month Gold | 1 Months       |
      | TIN3M | Tinder 1 Month Gold | 3 Months Sale! |
    And exist the voucher:
      | code         | type  | status    | userId |
      | V1ACTIVE     | TIN1M | ACTIVE    |        |
      | V1PURCHASEDJ | TIN1M | PURCHASED | JOHN   |
      | V3PURCHASEDJ | TIN3M | PURCHASED | JOHN   |
      | V3REDEEMEDJ  | TIN3M | REDEEMED  | JOHN   |
      | V1PURCHASEDB | TIN1M | PURCHASED | BOB    |

  # Retrieve the voucher types filtered
  Scenario Outline: List of voucher types
    When the operator requires the vouchers with type '<type>', status '<status>' and userId '<userId>'
    Then the user retrieve the list with '<result>' vouchers
    Examples:
      | type  | status    | userId | result                                                      |
      | TIN1M |           |        | V1ACTIVE,V1PURCHASEDJ,V1PURCHASEDB                          |
      | TIN3M |           |        | V3PURCHASEDJ,V3REDEEMEDJ                                    |
      |       | ACTIVE    |        | V1ACTIVE                                                    |
      |       | PURCHASED |        | V1PURCHASEDJ,V3PURCHASEDJ,V1PURCHASEDB                      |
      |       |           | BOB    | V1PURCHASEDB                                                |
      |       |           | JOHN   | V1PURCHASEDJ,V3PURCHASEDJ,V3REDEEMEDJ                       |
      |       | PURCHASED | JOHN   | V1PURCHASEDJ,V3PURCHASEDJ                                   |
      | TIN1M | PURCHASED | JOHN   | V1PURCHASEDJ                                                |
      |       |           |        | V1ACTIVE,V1PURCHASEDJ,V3PURCHASEDJ,V3REDEEMEDJ,V1PURCHASEDB |
      |       |           | MARIO  |                                                             |

  Scenario Outline: Voucher in error
    When the operator requires the vouchers with type '<type>', status '<status>' and userId ''
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | type  | status | errorCode      | errorDescription             |
      | wrong |        | TYPE_NOT_FOUND | Voucher Type wrong not found |
      |       | status | WRONG_STATUS   | Status status is wrong       |
