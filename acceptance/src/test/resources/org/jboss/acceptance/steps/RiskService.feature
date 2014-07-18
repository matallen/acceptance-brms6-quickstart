Feature: risk service

Scenario: 01 - risk service accepts low risk orders
Given the order service is deployed
And an order exists with the following details:
|ID |Country |Amount |Items             |
|01 |GBR     |100.00 |iPhone,std-tariff |
When the order is submitted
Then the results should be:
|ID |Risk Rating | Recommendation |
|01 |HIGH        | REJECT         |


#Scenario: 02 - risk service rejects high risk orders
#Given the order service is deployed
#And an order exists with the following details:
#|ID |Country |Amount |Items             |
#|01 |UK      |10.00  |iPhone,std-tariff |
#When the order is submitted
#Then the results should be:
#|ID |Risk Rating | Recommendation |
#|01 |HIGH        | REJECT         |


#Scenario: 03 - risk service refers medium risk orders