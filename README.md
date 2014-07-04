acceptance-brms
===============


install the "maven-kie-deployer-plugin" from https://github.com/matallen/maven-kie-deployer-plugin


# Structure
rule-domain      - this is where any model classes go that as used in the rules
business-rules   - In src/main/rules is where you write your rules, and write unit tests in src/test/java as usual.
                   This module contains a RuleTestBase which provides easy rule compilation and execution helper methods
                   The "kie-maven-plugin" makes the output of the business-rules module a kjar which can be used by drools6. 
acceptance       - this module contains Cucumber BDD acceptance test statements and a test implementation to get you started.


# to build the project
mvn clean install

# to run acceptance for CI (ie. start containers, deploy apps, run tests and stop containers)
acceptance/mvn clean install -Pbrms,itest -o

# to develop (ie. start containers, deploy apps and pause so a dev can develop tests against the containers)
acceptance/mvn clean package cargo:run -o

head pom.xml - gives you the two commands to copy/paste quickly


# To add more webapps to the acceptance tests
in the cargo plugin config, add a <deployable> entry for your webapps maven GAV (groupId, artifactId and version) and it will be deployed


