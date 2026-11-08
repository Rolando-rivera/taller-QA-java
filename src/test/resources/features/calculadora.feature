Feature: Operaciones con números enteros
  Scenario: Sumar dos enteros positivos
    Given los operandos 4 y 3
    When sumo los operandos
    Then el resultado es 7
  Scenario Outline: Restar dos enteros
    Given los operandos <a> y <b>
    When resto los operandos
    Then el resultado es <resultado>
    Examples:
      | a  | b | resultado |
      |  3 | 5 |        -2 |
      |  0 | 0 |         0 |
      | -2 | 3 |        -5 |
