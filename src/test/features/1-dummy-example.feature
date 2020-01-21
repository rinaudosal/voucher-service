# language: en

Feature: Retrieve the Voucher types available

  # Retrieve the voucher types available
  Scenario: List of available voucher types
    Given the Maker has started a game
    When the Maker starts a game
    Then the Maker waits for a Breaker to join
