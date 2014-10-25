Feature: Order Service - New Order

Scenario: 01 - when a new order arrives, a business process is created to manage it
Given the order service is deployed
And a new order is created with the following details:
|ID |Country |Amount |Items             |
|01 |GBR     |10.00  |iPhone,std-tariff |
When the order is submitted


#Then the responses should be:
#|ID |Risk Rating | Recommendation |
#|01 |LOW         | ACCEPT         |
