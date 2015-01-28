Feature: Order Service - New Order

Scenario: 01 - UK orders should be risk checked
Given the order service is deployed
And new orders are created with the following details:
|ID |Country |Amount  |
|01 |GBR     |100.00  |
When the orders are submitted
Then the responses should be:
|ID |Risk Rating |Reason |
|01 |ACCEPT      |       |


Scenario: 02 - All Country risk thresholds should be applied correctly
Given the order service is deployed
And new orders are created with the following details:
|ID |Country |Amount  |
|01 |GBR     |150.00  |
|02 |GBR     |350.00  |
|03 |FRA     |200.00  |
|04 |FRA     |251.00  |
|05 |USA     |299.00  |
|06 |USA     |300.00  |
|07 |ITA     |250.00  |
When the orders are submitted
Then the responses should be:
|ID |Risk Rating |Reason                |
|01 |ACCEPT      |                      |
|02 |REFER       |Order amount too high |
|03 |ACCEPT      |                      |
|04 |REFER       |Order amount too high |
|05 |ACCEPT      |                      |
|06 |ACCEPT      |                      |
|07 |REFER       |Country unknown       |
