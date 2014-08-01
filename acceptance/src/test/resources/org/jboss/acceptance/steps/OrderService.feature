Feature: order service

Scenario: 01 - order service accepts low risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount |Items             |
|02 |GBR     |100.00 |iPhone,std-tariff |
When the order is submitted
Then the results should be:
|ID |Risk Rating | Recommendation |
|02 |HIGH        | REJECT         |


#Scenario: 02 - order service rejects high risk orders

#Scenario: 03 - order service refers medium risk orders