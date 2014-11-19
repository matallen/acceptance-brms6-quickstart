Feature: Order Service - New Order

Scenario: 01 - when a new order arrives, a business process is created to manage it
Given the order service is deployed
And a new order is created with the following details:
|ID |Country |Amount  |
|01 |GBR     |10.00   |
When the order is submitted
Then the responses should be:
|ID |Risk Rating |Reason |
|01 |ACCEPT      |       |


Scenario: 02 - Ensure risk rules are met
Given the order service is deployed
And a new order is created with the following details:
|ID |Country |Amount  |
|01 |GBR     |10.00   |
|02 |ITA     |157.00  |
|03 |GBR     |9000.00 |
|04 |AFG     |10.00   |
When the order is submitted
Then the responses should be:
|ID |Risk Rating |Reason                |
|01 |ACCEPT      |                      |
|02 |REFER       |Medium order value    |
|03 |REJECT      |Order amount too high |
|04 |REJECT      |Country not known     |