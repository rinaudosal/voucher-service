# language: en

@reserve-voucher
Feature: Reserve Voucher

  Background:
    Given exist the voucher types:
      | typeId | description           | enabled | startDate  | endDate    | baseUrl           |
      | TIN1M  | 1 Months              | true    | 01/01/2020 | 31/12/2020 | www.test.com/vip/ |
      | TIN1S  | 1 Months Sale!        | true    | 01/02/2020 | 18/08/2020 | www.test.com/vip/ |
      | TIN1SS | 1 Months Super Sale!! | true    | 01/08/2020 | 15/08/2020 | www.test.com/vip/ |
      | TIN3EX | not in range          | true    | 01/01/2020 | 20/02/2020 | www.test.com/vip/ |
      | TIN3NO | no available product  | true    | 01/01/2020 | 20/06/2020 | www.test.com/vip/ |
      | TIN9D  | Voucher not enabled   | false   | 05/01/2020 | 21/02/2020 | www.test.com/vip/ |
    And exist the voucher:
      | code          | typeId | status    |
      | V1ACTIVE      | TIN1M  | ACTIVE    |
      | V1PURCHASED   | TIN1M  | PURCHASED |
      | V1SACTIVE     | TIN1S  | ACTIVE    |
      | V1SPURCHASED  | TIN1S  | PURCHASED |
      | V1SSACTIVE    | TIN1SS | ACTIVE    |
      | V1SSPURCHASED | TIN1SS | PURCHASED |
      | V3EXACTIVE    | TIN3EX | ACTIVE    |
      | V9DACTIVE     | TIN9D  | ACTIVE    |

  Scenario Outline: Voucher reserved
    And today is '<billedDate>'
    When the operator wants to reserve the voucher to bill for typeId '<typeId>'
    Then the operator reserve the voucher '<code>' correctly for typeId '<typeId>'
    Examples:
      | billedDate | code       | typeId |
      | 13/01/2020 | V1ACTIVE   | TIN1M  |
      | 13/02/2020 | V1ACTIVE   | TIN1M  |
      | 13/08/2020 | V1ACTIVE   | TIN1M  |
      | 13/02/2020 | V1SACTIVE  | TIN1S  |
      | 13/08/2020 | V1SACTIVE  | TIN1S  |
      | 13/08/2020 | V1SSACTIVE | TIN1SS |


  Scenario Outline: Voucher reserved in error
    And today is '<billedDate>'
    When the operator wants to reserve the voucher to bill for typeId '<typeId>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | typeId | billedDate | errorCode              | errorDescription                        |
      | TIN1M  | 13/01/2019 | TYPE_NOT_YET_AVAILABLE | Voucher Type TIN1M is not yet available |
      | TIN1M  | 13/01/2021 | TYPE_EXPIRED           | Voucher Type TIN1M is expired           |
      | TIN9D  | 13/01/2020 | TYPE_DISABLED          | Voucher Type TIN9D is disabled          |
      | WRONG  | 13/01/2020 | TYPE_NOT_FOUND         | Voucher Type WRONG not found            |
      | WRONG  | 13/02/2020 | TYPE_NOT_FOUND         | Voucher Type WRONG not found            |
      | WRONG  | 13/08/2020 | TYPE_NOT_FOUND         | Voucher Type WRONG not found            |
