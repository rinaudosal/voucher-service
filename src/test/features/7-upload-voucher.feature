# language: en

@PI-1436 @upload-voucher
Feature: Upload Voucher

  Background:
    Given exist the voucher types:
      | typeId | description       | merchant | enabled | startDate  					| endDate    					|
      | TIN1M  | 1 Months          | tinder   | true    | 01/01/2020 08:15:00 | 31/12/2020 08:15:00 |
      | TIN3M  | 3 Months to be    | tinder   | true    | 01/03/2020 08:15:00 | 31/12/2020 08:15:00 |
      | TIN6M  | 6 Months disabled | tinder   | false   | 01/01/2020 08:15:00 | 31/12/2020 08:15:00 |
      | TIN9M  | 9 Months expired  | tinder   | true    | 01/01/2020 08:15:00 | 21/01/2020 08:15:00 |
      | BUM9M  | 9 Months          | bumble   | true    | 01/01/2020 08:15:00 | 31/12/2020 08:15:00 |
    And exist the voucher:
      | code            | typeId |
      | EXISTINGVOUCHER | TIN1M  |
    And today is '01/02/2020'

  Scenario Outline: Voucher file uploaded
    When the operator wants to upload the voucher file with <size> vouchers for type '<typeId>'
    Then the operator upload the <size> vouchers correctly for type '<typeId>'
    Examples:
      | typeId | size |
      | TIN1M  | 100  |
      | TIN3M  | 100  |
      | BUM9M  | 100  |
      | TIN1M  | 0    |
      | TIN3M  | 0    |
      | BUM9M  | 0    |

  Scenario Outline: Voucher upload in error
    When the operator wants to upload the voucher file with 100 vouchers for type '<typeId>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | typeId | errorCode      | errorDescription               |
      | PIPPO  | TYPE_NOT_FOUND | Voucher Type PIPPO not found   |
      | TIN6M  | TYPE_DISABLED  | Voucher Type TIN6M is disabled |
      | TIN9M  | TYPE_EXPIRED   | Voucher Type TIN9M is expired  |

  Scenario: Voucher upload file malformed
    When the operator wants to 'upload' the voucher file malformed for type 'TIN1M'
    Then the operator receive the error code 'FILE_MALFORMED' and description 'Error, the file is malformed'

  Scenario Outline: Voucher upload partial in error
    When the operator wants to 'upload' the voucher file with 3 vouchers for type 'TIN1M' and the voucher file contain also 'EXISTINGVOUCHER'
    Then the operator 'upload' the 3 vouchers correctly and 1 with error '<errorCode>' and message '<errorMessage>'
    Examples:
      | errorCode     | errorMessage                          |
      | ALREADY_EXIST | Voucher EXISTINGVOUCHER already exist |

  Scenario Outline: Voucher upload without mandatory fields
    When the operator wants to 'upload' the voucher without field '<field>'
    Then the operator receive the error 'Invalid request, parameter '<field>' is mandatory'
    Examples:
      | field  |
      | typeId |
      | file   |
