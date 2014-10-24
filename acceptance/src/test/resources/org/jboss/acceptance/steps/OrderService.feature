Feature: Order Service

Scenario: 01 - order service accepts low risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount |Items             |
|01 |GBR     |50.00 |iPhone,std-tariff |
When the order is submitted
Then the responses should be:
|ID |Risk Rating | Recommendation |
|01 |LOW         | ACCEPT         |


Scenario: 02 - order service rejects high risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount |Items             |
|02 |GBR     |150.00 |iPhone,std-tariff |
When the order is submitted
Then the responses should be:
|ID |Risk Rating | Recommendation |
|02 |MEDIUM      | REFER          |


#Scenario: 03 - order service refers medium risk orders