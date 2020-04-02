# language: en

@PI-1431 @redeem-voucher
Feature: Redeem Voucher

  Background:
    Given exist the voucher types:
      | typeId | description | merchant | enabled | startDate  | endDate    |
      | TIN1M  | 1 Months    | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3M  | 3 Months    | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3D  | 3 Months D  | tinder   | false   | 01/01/2020 | 31/12/2020 |
      | TIN3E  | 3 Months E  | tinder   | false   | 01/01/2019 | 31/12/2019 |
    And exist the voucher:
      | code     | typeId | status    |
      | VOU11PUR | TIN1M  | PURCHASED |
      | VOU12INA | TIN1M  | INACTIVE  |
      | VOU13ACT | TIN1M  | ACTIVE    |
      | VOU14RED | TIN1M  | REDEEMED  |
      | VOU31PUR | TIN3M  | PURCHASED |
      | VOU41PUR | TIN3D  | PURCHASED |
      | VOU51PUR | TIN3E  | PURCHASED |

  Scenario Outline: Voucher file redeemed
    When the operator wants to 'redeem' the voucher file for the type '<typeId>' with the voucher '<voucher>'
    Then the operator receive the voucher correctly for type '<typeId>'
    Examples:
      | typeId | voucher  |
      | TIN1M  | VOU11PUR |
      | TIN3M  | VOU31PUR |
      | TIN3D  | VOU41PUR |
      | TIN3E  | VOU51PUR |

  Scenario Outline: Voucher redeem in error
    When the operator wants to 'redeem' the voucher file for the type '<typeId>' with the voucher '<voucher>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | typeId | voucher  | errorCode      | errorDescription             |
      | PIPPO  | VOU11PUR | TYPE_NOT_FOUND | Voucher Type PIPPO not found |

  Scenario: Voucher redeem file malformed
    When the operator wants to 'redeem' the voucher file malformed for type 'TIN1M'
    Then the operator receive the error code 'FILE_MALFORMED' and description 'Error, the file is malformed'

  Scenario Outline: Voucher redeem partial in error
    When the operator wants to 'redeem' the voucher file with 0 vouchers for type 'TIN1M' and the voucher file contain also '<vouchers>'
    Then the operator 'redeem' the 1 vouchers correctly and 1 with error '<errorCode>' and message '<errorMessage>'
    Examples:
      | vouchers          | errorCode         | errorMessage                                          |
      | VOU12INA,VOU11PUR | WRONG_STATUS      | Voucher VOU12INA not redeemed, the status is INACTIVE |
      | VOU13ACT,VOU11PUR | WRONG_STATUS      | Voucher VOU13ACT not redeemed, the status is ACTIVE   |
      | VOU14RED,VOU11PUR | WRONG_STATUS      | Voucher VOU14RED not redeemed, the status is REDEEMED |
      | VOU31PUR,VOU11PUR | VOUCHER_NOT_FOUND | Voucher VOU31PUR not found for Type TIN1M             |

  Scenario Outline: Voucher redeem without mandatory fields
    When the operator wants to 'redeem' the voucher without field '<field>'
    Then the operator receive the error 'Invalid request, parameter '<field>' is mandatory'
    Examples:
      | field  |
      | typeId |
      | file   |
