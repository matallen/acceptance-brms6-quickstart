Feature: Banking Service - Recurring Payments

Scenario: 01 - Successful recurring payment when funds are available
Given there is an account:
|Account Id |FirstName |Surname |Balance |Overdraft |
|00001      |Mat       |Allen   |100.00  |0.00      |
|00002      |David     |Tucker  |0.00    |0.00      |
When there is a payment scheduled:
|Payment Id |From Account |To Account |Value |
|A          |00001        |00002      |50.00 |
And there is enough funds in the account
Then a payment will be generated
And the account owner will be notified:
|Payment Id |Account Id |Outcome |
|A          |00001      |Success |


Scenario: 02 - Successful recurring payment when funds are not available but there is an overdraft setup
Given there is an account:
|Account Id |FirstName |Surname |Balance |Overdraft |
|00001      |Mat       |Allen   |100.00  |100.00    |
|00002      |David     |Tucker  |0.00    |0.00      |
When there is a payment scheduled:
|Payment Id |From Account |To Account |Value  |
|B          |00001        |00002      |150.00 |
And there is not enough funds in the account
Then a payment will be generated
And the account owner will be notified:
|Payment Id |Account Id |Outcome |
|B          |00001      |Success |


Scenario: 02 - Failed recurring payment when funds are not available no overdraft setup
Given there is an account:
|Account Id |FirstName |Surname |Balance |Overdraft |
|00001      |Mat       |Allen   |100.00  |0.00      |
|00002      |David     |Tucker  |0.00    |0.00      |
When there is a payment scheduled:
|Payment Id |From Account |To Account |Value  |
|C          |00001        |00002      |150.00 |
And there is not enough funds in the account
Then a payment will be generated
And the account owner will be notified:
|Payment Id |Account Id |Outcome |
|C          |00001      |Failure |

