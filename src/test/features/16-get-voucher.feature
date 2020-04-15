# language: en

@PI-1739 @get-voucher
Feature: Get Voucher

  Background:
    Given exist the voucher types:
      | typeId | description | shop       |
      | TIN1M  | 1 Months    | my_shop_id |
    And exist the voucher:
      | code       | typeId | status    |
      | VOUCHERACT | TIN1M  | ACTIVE    |
      | VOUCHERPUR | TIN1M  | PURCHASED |
      | VOUCHERRED | TIN1M  | REDEEMED  |
      | VOUCHERRES | TIN1M  | RESERVED  |
      | VOUCHERINA | TIN1M  | INACTIVE  |

  Scenario Outline: Voucher get correctly
    When the operator want to gets the voucher '<code>' with type '<typeId>'
    Then the operator gets the voucher correctly with '<code>' and type '<typeId>'
    Examples:
      | code       | typeId |
      | VOUCHERPUR | TIN1M  |
      | VOUCHERRED | TIN1M  |
      | VOUCHERRES | TIN1M  |


  Scenario Outline: Voucher get in error
    When the operator want to gets the voucher '<code>' with type '<typeId>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | code       | typeId | errorCode      | errorDescription                           |
      | VOUCHERACT | TIN1M  | WRONG_STATUS   | Voucher with code VOUCHERACT is not Billed |
      | VOUCHERINA | TIN1M  | WRONG_STATUS   | Voucher with code VOUCHERINA is not Billed |
      | WRONG      | TIN1M  | TYPE_NOT_FOUND | Voucher WRONG not found for type TIN1M     |
      | VOUCHERPUR | WRONG  | TYPE_NOT_FOUND | Voucher Type WRONG not found               |
