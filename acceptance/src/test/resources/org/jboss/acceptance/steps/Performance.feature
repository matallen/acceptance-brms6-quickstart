Feature: Performance

Scenario: 01 - The system handles 1000 requests within 10 seconds
Given the order service is deployed
And 1000 random orders are generated
|ID          |Random Country |Amount Range |
|<generated> |<generated>    |10.00-500.00 |
When the orders are submitted with a concurrency of 10
Then all responses should be returned within 20 seconds

