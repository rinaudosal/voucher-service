# language: en

@PI-1420 @create-voucher-type
Feature: Create Voucher types

  Background:
    Given exist the voucher types:
      | typeId          | product    | description    | Promo | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate           | endDate             | priority | bypassStatusCheck |
      | BVOUCHER1MONTHS | Tinder 1 m | 1 Months       |       | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 | 1        | true              |
      | WVOUCHER1MONTHS | Tinder 3 m | 1 Months VDFAU | sale  | 9.99   | INR      | tinder   | IN      | VDFAU           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 | 0        | false             |

  Scenario: Voucher type created sale period
    When the operator wants to create the voucher type:
      | typeId         | product    | description    | Promo | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  					| endDate    					| priority | baseUrl        |
      | NEWVOUCHERCODE | Tinder 1 m | 1  Months sale |       | 6.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 | 3        | www.tinder.com |
    Then the operator create the voucher type correctly


  Scenario: Voucher type with the same priority
    When the operator wants to create the voucher type:
      | typeId         | product    | description    | Promo | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  					| endDate    					| priority |
      | NEWVOUCHERCODE | Tinder 1 m | 1  Months sale |       | 6.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 | 1        |
    Then the operator receive the error 'Voucher Type exist with the same period'

  Scenario: Voucher type already exist
    When the operator wants to create the voucher type:
      | typeId          | product    | description | Promo | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  					| endDate    					| priority |
      | BVOUCHER1MONTHS | Tinder 6 m | 6 Months    |       | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 | 2        |
    Then the operator receive the error 'Voucher Type already exist'

  Scenario Outline: Voucher type without mandatory fields
    When the operator wants to create the voucher type without field '<field>':
      | code           | product    | description | Promo | amount | currency | merchant | country | paymentProvider | shop  | enabled | startDate  				 | endDate   					 | priority |
      | NEWVOUCHERCODE | Tinder 6 m | 1 Months    |       | 9.99   | INR      | tinder   | IN      | PAYTM           | shop1 | true    | 01/01/2020 08:15:00 | 21/06/2020 08:15:00 | 5        |
    Then the operator receive the error 'Invalid Voucher Type, '<field>' is mandatory'
    Examples:
      | field           |
      | typeId          |
      | amount          |
      | currency        |
      | product         |
      | merchant        |
      | country         |
      | paymentProvider |
      | shop            |
