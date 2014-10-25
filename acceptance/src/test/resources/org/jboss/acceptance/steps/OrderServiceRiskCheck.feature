Feature: Order Service - Risk Check

Scenario: 01 - order service accepts low risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount |Items             |
|01 |GBR     |50.00 |iPhone,std-tariff |
When the risk check is performed
Then the responses should be:
|ID |Risk Rating | Recommendation |
|01 |LOW         | ACCEPT         |


Scenario: 02 - order service rejects high risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount  |Items             |
|02 |GBR     |1250.00 |iPhone,std-tariff |
When the risk check is performed
Then the responses should be:
|ID |Risk Rating | Recommendation |
|02 |HIGH        | REJECT         |


Scenario: 03 - order service refers medium risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount |Items             |
|03 |GBR     |150.00 |iPhone,std-tariff |
When the risk check is performed
Then the responses should be:
|ID |Risk Rating | Recommendation |
|03 |MEDIUM      | REFER          |