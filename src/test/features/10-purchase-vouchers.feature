# language: en

@PI-1431 @purchase-vouchers
Feature: Purchase Voucher File

  Background:
    Given exist the voucher types:
      | code  | description | merchant | enabled | startDate  | endDate    |
      | TIN1M | 1 Months    | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3M | 3 Months    | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3D | 3 Months D  | tinder   | false   | 01/01/2020 | 31/12/2020 |
      | TIN3E | 3 Months E  | tinder   | true    | 01/01/2019 | 31/12/2019 |
    And exist the voucher:
      | code     | type  | status    |
      | VOU11ACT | TIN1M | ACTIVE    |
      | VOU12INA | TIN1M | INACTIVE  |
      | VOU13PUR | TIN1M | PURCHASED |
      | VOU14RED | TIN1M | REDEEMED  |
      | VOU31ACT | TIN3M | ACTIVE    |
      | VOU41ACT | TIN3D | ACTIVE    |
      | VOU51ACT | TIN3E | ACTIVE    |

  Scenario Outline: Voucher file purchased
    When the operator wants to 'purchase' the voucher file for the type '<type>' with the voucher '<voucher>'
    Then the operator receive the voucher correctly for type '<type>'
    Examples:
      | type  | voucher  |
      | TIN1M | VOU11ACT |
      | TIN3M | VOU31ACT |

  Scenario Outline: Voucher file purchase in error
    When the operator wants to 'purchase' the voucher file for the type '<type>' with the voucher '<voucher>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | type  | voucher  | errorCode      | errorDescription               |
      | PIPPO | VOU11ACT | TYPE_NOT_FOUND | Voucher Type PIPPO not found   |
      | TIN3D | VOU41ACT | TYPE_DISABLED  | Voucher Type TIN3D is disabled |
      | TIN3E | VOU51ACT | TYPE_EXPIRED   | Voucher Type TIN3E is expired  |

  Scenario: Voucher file purchase malformed
    When the operator wants to 'purchase' the voucher file malformed for type 'TIN1M'
    Then the operator receive the error code 'FILE_MALFORMED' and description 'Error, the file is malformed'

  Scenario Outline: Voucher redeem partial in error
    When the operator wants to 'purchase' the voucher file with 0 vouchers for type 'TIN1M' and the voucher file contain also '<vouchers>'
    Then the operator 'purchase' the 1 vouchers correctly and 1 with error '<errorCode>' and message '<errorMessage>'
    Examples:
      | vouchers          | errorCode         | errorMessage                                          |
      | VOU12INA,VOU11ACT | WRONG_STATUS      | Voucher with code VOU12INA is not in ACTIVE state     |
      | VOU13PUR,VOU11ACT | WRONG_STATUS      | Voucher with code VOU13PUR is not in ACTIVE state     |
      | VOU14RED,VOU11ACT | WRONG_STATUS      | Voucher with code VOU14RED is not in ACTIVE state     |
      | VOU31ACT,VOU11ACT | VOUCHER_NOT_FOUND | Voucher VOU31ACT not found for Type TIN1M             |

  Scenario Outline: Voucher redeem without mandatory fields
    When the operator wants to 'redeem' the voucher without field '<field>'
    Then the operator receive the error 'Invalid request, parameter '<field>' is mandatory'
    Examples:
      | field |
      | type  |
      | file  |
