# language: en

@PI-1436 @purchase-voucher
Feature: Purchase Voucher

  Background:
    Given exist the voucher types:
      | code  | description       | merchant | enabled | startDate  | endDate    |
      | TIN1M | 1 Months          | tinder   | true    | 01/01/2020 | 31/12/2020 |
      | TIN3M | 3 Months to be    | tinder   | true    | 01/03/2020 | 31/12/2020 |
      | TIN6M | 6 Months disabled | tinder   | false   | 01/01/2020 | 31/12/2020 |
      | TIN9M | 9 Months expired  | tinder   | true    | 01/01/2020 | 21/01/2020 |
    And exist the voucher:
      | code              | type  | status    | purchaseDate |
      | VOUCHER1ACTIVE    | TIN1M | ACTIVE    | 31/01/2020   |
      | VOUCHER1PURCHASED | TIN1M | PURCHASED | 31/01/2020   |
      | VOUCHER3ACTIVE    | TIN3M | ACTIVE    | 31/01/2020   |
      | VOUCHER3PURCHASED | TIN3M | PURCHASED | 31/01/2020   |
      | VOUCHER6ACTIVE    | TIN6M | ACTIVE    | 31/01/2020   |
      | VOUCHER6PURCHASED | TIN6M | PURCHASED | 31/01/2020   |
      | VOUCHER9ACTIVE    | TIN9M | ACTIVE    | 31/01/2020   |
      | VOUCHER9PURCHASED | TIN9M | PURCHASED | 31/01/2020   |
    And today is '01/02/2020'

  Scenario Outline: Voucher purchased
    When the operator wants to purchase the voucher '<code>'
    Then the operator purchase the voucher '<code>' correctly
    Examples:
      | code           |
      | VOUCHER1ACTIVE |
      | VOUCHER9ACTIVE |

  Scenario Outline: Voucher purchase in error
    When the operator wants to purchase the voucher '<code>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | code              | errorCode              | errorDescription                                           |
      | VOUCHER1PURCHASED | VOUCHER_NOT_ACTIVE     | Voucher with code VOUCHER1PURCHASED is not in ACTIVE state |
      | VOUCHER3ACTIVE    | TYPE_NOT_YET_AVAILABLE | Voucher Type TIN3M is not yet available                    |
      | VOUCHER3PURCHASED | VOUCHER_NOT_ACTIVE     | Voucher with code VOUCHER3PURCHASED is not in ACTIVE state |
      | VOUCHER6ACTIVE    | TYPE_DISABLED          | Voucher Type TIN6M is disabled                             |
      | VOUCHER6PURCHASED | VOUCHER_NOT_ACTIVE     | Voucher with code VOUCHER6PURCHASED is not in ACTIVE state |
      | VOUCHER9PURCHASED | VOUCHER_NOT_ACTIVE     | Voucher with code VOUCHER9PURCHASED is not in ACTIVE state |
