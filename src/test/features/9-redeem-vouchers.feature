# language: en

@PI-1431 @redeem-voucher
Feature: Redeem Voucher

  Background:
    Given exist the voucher types:
      | typeId | description    | merchant | enabled | startDate           | endDate             | bypassStatusCheck |
      | TIN1M  | 1 Months       | tinder   | true    | 01/01/2020 08:15:00 | 31/12/2020 08:15:00 | false             |
      | TIN3M  | 3 Months       | tinder   | true    | 01/01/2020 08:15:00 | 31/12/2020 08:15:00 | false             |
      | TIN3D  | 3 Months D     | tinder   | false   | 01/01/2020 08:15:00 | 31/12/2020 08:15:00 | false             |
      | TIN3E  | 3 Months E     | tinder   | false   | 01/01/2019 08:15:00 | 31/12/2019 08:15:00 | false             |
      | TIN1C  | 1 Month Bumble | bumble   | true    | 01/01/2019 08:15:00 | 31/12/2019 08:15:00 | false             |
      | TIN3S  | 3 Months S     | tinder   | true    | 01/01/2019 08:15:00 | 31/12/2019 08:15:00 | true              |

    And exist the voucher:
      | code     | typeId | status    |
      | VOU11PUR | TIN1M  | PURCHASED |
      | VOU12INA | TIN1M  | INACTIVE  |
      | VOU13ACT | TIN1M  | ACTIVE    |
      | VOU14RED | TIN1M  | REDEEMED  |
      | VOU31PUR | TIN3M  | PURCHASED |
      | VOU41PUR | TIN3D  | PURCHASED |
      | VOU51PUR | TIN3E  | PURCHASED |
      | VOU61PUR | TIN3S  | PURCHASED |
      | VOU62ACT | TIN3S  | ACTIVE    |
      | VOU71PUR | TIN1C  | PURCHASED |

  Scenario Outline: Voucher file redeemed
    When the operator wants to 'redeem' the voucher file for the merchant '<merchant>' with the vouchers '<vouchers>'
    Then the operator receive <size> vouchers correctly for merchant '<merchant>'
    Examples:
      | merchant | vouchers                                              | size |
      | tinder   | VOU11PUR,VOU31PUR,VOU41PUR,VOU51PUR,VOU61PUR,VOU62ACT | 6    |
      | bumble   | VOU71PUR                                              | 1    |

  Scenario Outline: Voucher redeem in error
    When the operator wants to 'redeem' the voucher file for the merchant '<merchant>' with the vouchers '<vouchers>'
    Then the operator 'redeem' the 0 vouchers correctly and 1 with error '<errorCode>' and message '<errorDescription>'
    Examples:
      | merchant | vouchers | errorCode         | errorDescription           |
      | tin      | VOU11PUR | VOUCHER_NOT_FOUND | Voucher VOU11PUR not found |

  Scenario: Voucher redeem file malformed
    When the operator wants to redeem the voucher file malformed for merchant 'tinder'
    Then the operator receive the error code 'FILE_MALFORMED' and description 'Error, the file is malformed'

  Scenario Outline: Voucher redeem partial in error
    When the operator wants to 'redeem' the voucher file with 0 vouchers for merchant 'tinder' and the voucher file contain also '<vouchers>'
    Then the operator 'redeem' the 1 vouchers correctly and 1 with error '<errorCode>' and message '<errorMessage>'
    Examples:
      | vouchers          | errorCode         | errorMessage                                          |
      | VOU12INA,VOU11PUR | WRONG_STATUS      | Voucher VOU12INA not redeemed, the status is INACTIVE |
      | VOU13ACT,VOU11PUR | WRONG_STATUS      | Voucher VOU13ACT not redeemed, the status is ACTIVE   |
      | VOU14RED,VOU11PUR | WRONG_STATUS      | Voucher VOU14RED not redeemed, the status is REDEEMED |
      | VOU71PUR,VOU11PUR | VOUCHER_NOT_FOUND | Voucher VOU71PUR not found                            |

  Scenario Outline: Voucher redeem without mandatory fields
    When the operator wants to 'redeem' the voucher without field '<field>'
    Then the operator receive the error 'Invalid request, parameter '<field>' is mandatory'
    Examples:
      | field    |
      | merchant |
      | file     |
