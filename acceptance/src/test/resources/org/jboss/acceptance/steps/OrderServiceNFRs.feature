#Feature: Order Service NFRs
#
#Scenario: 01 - The system must handle 1000 risk check requests within 60 seconds
#Given the order service is deployed
#And 1000 random orders are generated
#|ID          |Random Country |Amount Range |
#|<generated> |<generated>    |10.00-500.00 |
#When the risk checks are submitted with a concurrency of 10
#Then all responses should be returned within 60 seconds

