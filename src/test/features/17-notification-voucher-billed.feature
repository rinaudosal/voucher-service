# language: en
@DL-409 @notification-voucher-billed
Feature: Notification Voucher Billed

  Background:
    Given exist the voucher types:
      | typeId | description | merchant | shop |
      | TIN1M  | 1 Month     | tinder   | asia |
      | TIN3M  | 3 Months    | tinder   | asia |
      | BUM1M  | 1 Months    | bumble   | asia |
    And exist the voucher:
      | code       | typeId | status    |
      | VOUCHERACT | TIN1M  | ACTIVE    |
      | VOUCHERPUR | TIN1M  | PURCHASED |
      | VOUCHERRED | TIN1M  | REDEEMED  |
      | VOUCHERRES | TIN1M  | RESERVED  |
      | VOUCHERINA | TIN1M  | INACTIVE  |
      | VOUCHER3M  | TIN3M  | PURCHASED |
      | VOUCHERBUM | BUM1M  | PURCHASED |

  Scenario Outline: Notification of Voucher sent corretly
    When the operator wants to notify the voucher '<code>' for the merchant '<merchant>' to the billing system
    Then the operator sent the voucher '<code>' correctly to the billing system
    Examples:
      | code       | merchant |
      | VOUCHERPUR | tinder   |
      | VOUCHERRED | tinder   |
      | VOUCHER3M  | tinder   |
      | VOUCHERBUM | bumble   |

  Scenario Outline: Notification of voucher in error
    When the operator wants to notify the voucher '<voucher>' for the merchant '<merchant>' to the billing system
    Then the operator receive the error code '<errorCode>' and description "<errorDescription>"
    Examples:
      | voucher    | merchant | errorCode             | errorDescription                                                                   |
      | VOUCHERACT | tinder   | WRONG_STATUS          | Cannot sent Voucher with code VOUCHERACT to the billing system, status is ACTIVE   |
      | VOUCHERRES | tinder   | WRONG_STATUS          | Cannot sent Voucher with code VOUCHERRES to the billing system, status is RESERVED |
      | VOUCHERINA | tinder   | WRONG_STATUS          | Cannot sent Voucher with code VOUCHERINA to the billing system, status is INACTIVE |
      | VOUCHERPUR | NE       | VOUCHER_NOT_FOUND     | Voucher VOUCHERPUR not found for merchant NE                                       |
      | VOUCHERPUR | bumble   | VOUCHER_NOT_FOUND     | Voucher VOUCHERPUR not found for merchant bumble                                   |
