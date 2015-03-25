Feature: Order Service - New Order

Scenario: 01 - UK orders should be risk checked 
Given the order service is deployed
And new orders are created with the following details:
|ID |Country |Amount  |
|01 |GBR     |150.00  |
|02 |GBR     |350.00  |
When the orders are processed
Then the risk responses should be:
|ID |Risk Rating |Reason                |
|01 |ACCEPT      |                      |
|02 |REFER       |Order amount too high |


#Scenario: 02 - Ensure risk rules are met
#Given the order service is deployed
#And new orders are created with the following details:
#|ID |Country |Amount  |
#|01 |GBR     |10.00   |
#|02 |DEU     |157.00  |
#|03 |GBR     |9000.00 |
#|04 |AFG     |10.00   |
#When the orders are processed
#Then the risk responses should be:
#|ID |Risk Rating |Reason                |
#|01 |ACCEPT      |                      |
#|02 |ACCEPT      |                      |
#|03 |REFER       |Order amount too high |
#|04 |REFER       |                      |