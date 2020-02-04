# language: en

@PI-1431 @redeem-voucher
Feature: Redeem Voucher

  Background:
    Given exist the voucher types:
      | code  | description | merchant | enabled | startDate  | endDate    |
      | TIN1M | 1 Months    | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3M | 3 Months    | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3D | 3 Months D  | tinder   | false   | 01/01/2020 | 31/12/2020 |
      | TIN3E | 3 Months E  | tinder   | false   | 01/01/2019 | 31/12/2019 |
    And exist the voucher:
      | code     | type  | status    |
      | VOU11PUR | TIN1M | PURCHASED |
      | VOU12INA | TIN1M | INACTIVE  |
      | VOU13ACT | TIN1M | ACTIVE    |
      | VOU14RED | TIN1M | REDEEMED  |
      | VOU31PUR | TIN3M | PURCHASED |
      | VOU41PUR | TIN3D | PURCHASED |
      | VOU51PUR | TIN3E | PURCHASED |

  Scenario Outline: Voucher file redeemed
    When the operator wants to redeem the voucher file for the type '<type>' with the voucher '<voucher>'
    Then the operator redeem the voucher correctly for type '<type>'
    Examples:
      | type  | voucher  |
      | TIN1M | VOU11PUR |
      | TIN3M | VOU31PUR |
      | TIN3D | VOU41PUR |
      | TIN3E | VOU51PUR |

  Scenario Outline: Voucher redeem in error
    When the operator wants to redeem the voucher file for the type '<type>' with the voucher '<voucher>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | type  | voucher  | errorCode         | errorDescription                                      |
      | PIPPO | VOU11PUR | TYPE_NOT_FOUND    | Voucher Type PIPPO not found                          |
      | TIN1M | VOU12INA | WRONG_STATUS      | Voucher VOU12INA not redeemed, the status is INACTIVE |
      | TIN1M | VOU13ACT | WRONG_STATUS      | Voucher VOU13ACT not redeemed, the status is ACTIVE   |
      | TIN1M | VOU14RED | WRONG_STATUS      | Voucher VOU14RED not redeemed, the status is REDEEMED |
      | TIN1M | VOU31PUR | VOUCHER_NOT_FOUND | Voucher VOU31PUR not found for Type TIN1M             |

  Scenario: Voucher redeem file malformed
    When the operator wants to 'redeem' the voucher file malformed for type 'TIN1M'
    Then the operator receive the error code 'FILE_MALFORMED' and description 'Error, the file is malformed'

#  Scenario: Voucher upload partial in error
#    When the operator wants to upload the voucher file with 3 vouchers for type 'TIN1M' and the voucher file contain also 'EXISTINGVOUCHER'
#    Then the operator upload the 3 vouchers correctly and 1 with error 'Voucher with code 'EXISTINGVOUCHER' already exist'

  Scenario Outline: Voucher redeem without mandatory fields
    When the operator wants to 'redeem' the voucher without field '<field>'
    Then the operator receive the error 'Invalid request, parameter '<field>' is mandatory'
    Examples:
      | field |
      | type  |
      | file  |
