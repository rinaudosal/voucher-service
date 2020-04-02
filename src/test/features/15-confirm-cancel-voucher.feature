# language: en

@confirm-cancel-voucher
Feature: Confirm or Cancel Voucher Reserved

  Background:
    Given exist the voucher types:
      | typeId | description         | enabled | startDate  | endDate    | shop       |
      | TIN1M  | 1 Months            | true    | 01/01/2020 | 31/12/2020 | my_shop_id |
      | TIN9D  | Voucher not enabled | false   | 01/01/2020 | 31/12/2020 | my_shop_id |
    And exist the voucher:
      | code        | typeId | status    | activationUrl | reserveDate | transactionId |
      | V1ACTIVE    | TIN1M  | ACTIVE    |               |             |               |
      | V1RESERVED  | TIN1M  | RESERVED  | vsfv          | 31/12/2020  | txt1          |
      | V1PURCHASED | TIN1M  | PURCHASED | fre           |             |               |
      | V9DRESERVED | TIN9D  | RESERVED  | vcsfd         | 31/12/2020  | txt1          |

  Scenario Outline: Confirm or Cancel correctly
    And today is '<billedDate>'
    When the operator wants to '<operation>' the voucher '<code>' reserved for typeId '<typeId>' and transactionId '<transactionId>'
    Then the operator '<operation>' the voucher '<code>' correctly for typeId '<typeId>'
    Examples:
      | operation | billedDate | code       | typeId | transactionId |
      | SUCCESS   | 13/01/2020 | V1RESERVED | TIN1M  | txt1          |
      | SUCCESS   | 13/01/2021 | V1RESERVED | TIN1M  | txt1          |
      | FAILED    | 13/01/2020 | V1RESERVED | TIN1M  | txt1          |
      | FAILED    | 13/01/2021 | V1RESERVED | TIN1M  | txt1          |

  Scenario Outline: Confirm or Cancel in error
    And today is '<billedDate>'
    When the operator wants to '<operation>' the voucher '<code>' reserved for typeId '<typeId>' and transactionId '<transactionId>'
    Then the operator receive the error code '<errorCode>' and description '<errorDescription>'
    Examples:
      | operation | code        | typeId | transactionId | billedDate | errorCode            | errorDescription                                       |
      | SUCCESS   | V1RESERVED  | WRONG  | txt1          | 13/01/2020 | TYPE_NOT_FOUND       | Voucher Type WRONG not found                           |
      | FAILED    | V1RESERVED  | WRONG  | txt1          | 13/01/2020 | TYPE_NOT_FOUND       | Voucher Type WRONG not found                           |
      | SUCCESS   | WRONG       | TIN1M  | txt1          | 13/01/2020 | TYPE_NOT_FOUND       | Voucher WRONG not found for type TIN1M                 |
      | FAILED    | WRONG       | TIN1M  | txt1          | 13/01/2020 | TYPE_NOT_FOUND       | Voucher WRONG not found for type TIN1M                 |
      | SUCCESS   | V1ACTIVE    | TIN1M  | txt1          | 13/01/2020 | WRONG_STATUS         | Voucher with code V1ACTIVE is not in RESERVED state    |
      | FAILED    | V1ACTIVE    | TIN1M  | txt1          | 13/01/2020 | WRONG_STATUS         | Voucher with code V1ACTIVE is not in RESERVED state    |
      | SUCCESS   | V1PURCHASED | TIN1M  | txt1          | 13/01/2020 | WRONG_STATUS         | Voucher with code V1PURCHASED is not in RESERVED state |
      | FAILED    | V1PURCHASED | TIN1M  | txt1          | 13/01/2020 | WRONG_STATUS         | Voucher with code V1PURCHASED is not in RESERVED state |
      | SUCCESS   | V9DRESERVED | TIN9D  | txt1          | 13/01/2020 | TYPE_DISABLED        | Voucher Type TIN9D is disabled                         |
      | FAILED    | V9DRESERVED | TIN9D  | txt1          | 13/01/2020 | TYPE_DISABLED        | Voucher Type TIN9D is disabled                         |
      | SUCCESS   | V1RESERVED  | TIN1M  | txt2          | 13/01/2020 | WRONG_TRANSACTION_ID | Transaction id txt2 is different of reserved txt1      |
      | FAILED    | V1RESERVED  | TIN1M  | txt2          | 13/01/2020 | WRONG_TRANSACTION_ID | Transaction id txt2 is different of reserved txt1      |
