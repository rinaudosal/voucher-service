# language: en

@PI-1436 @create-voucher
Feature: Create Voucher

  Background:
    Given exist the voucher types:
      | code  | description       | merchant | enabled | startDate  | endDate    |
      | TIN1M | 1 Months          | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3M | 3 Months to be    | tinder   | true    | 01/03/2020 | 31/12/2020 |
      | TIN6M | 6 Months disabled | tinder   | false   | 01/01/2020 | 31/12/2020 |
      | TIN9M | 9 Months expired  | tinder   | true    | 01/01/2020 | 21/01/2020 |
      | BUM9M | 9 Months          | bumble   | true    | 01/01/2020 | 31/12/2020 |
    And exist the voucher:
      | code            | type  |
      | EXISTINGVOUCHER | TIN1M |
    And today is '01/02/2020'

  Scenario Outline: Voucher created
    When the operator wants to create the voucher '<code>' with type '<type>'
    Then the operator create the voucher correctly with '<code>' and type '<type>'
    Examples:
      | code            | type  |
      | NEWVOUCHERCODE  | TIN1M |
      | NEWVOUCHERCODE  | TIN3M |
      | EXISTINGVOUCHER | BUM9M |


  Scenario Outline: Voucher in error
    When the operator wants to create the voucher '<code>' with type '<type>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | code            | type  | errorCode     | errorDescription                                |
      | EXISTINGVOUCHER | TIN1M | ALREADY_EXIST | Voucher with code EXISTINGVOUCHER already exist |
      | EXISTINGVOUCHER | TIN3M | ALREADY_EXIST | Voucher with code EXISTINGVOUCHER already exist |
      | EXISTINGVOUCHER | TIN6M | TYPE_DISABLED | Voucher Type TIN6M is disabled                  |
      | EXISTINGVOUCHER | TIN9M | TYPE_EXPIRED  | Voucher Type TIN9M is expired                   |
      | NEWVOUCHERCODE  | TIN6M | TYPE_DISABLED | Voucher Type TIN6M is disabled                  |
      | NEWVOUCHERCODE  | TIN9M | TYPE_EXPIRED  | Voucher Type TIN9M is expired                   |

  Scenario Outline: Voucher without mandatory fields
    When the operator wants to create the voucher without field '<field>'
    Then the operator receive the error 'Invalid request, parameter '<field>' is mandatory'
    Examples:
      | field |
      | code  |
      | type  |
