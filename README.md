acceptance-brms6-quickstart
===========================


### Pre-conditions
* Install the maven-kie-deployer-plugin that pushes a pre-built kie module into a running BPM Suite

```git clone https://github.com/matallen/maven-kie-deployer-plugin```

```cd maven-kie-deployer-plugin```

```mvn clean install -DskipTests```

* Install the "business-central" BPM Suite webapp into your local m2 repo because its not available in a public repo

```./install/maven-install-business-central-only.sh <your downloaded distro from the Red Hat Portal>```


### Structure
| Module         | Notes         |
| -------------  |:-------------:|
| rule-domain    | * this is where any model classes go that as used in the rules |
| business-rules | * In src/main/rules is where you write your rules, and write unit tests in src/test/java as usual. This module contains a RuleTestBase which provides easy rule compilation and execution helper methods. The "kie-maven-plugin" makes the output of the business-rules module a kjar which can be used by drools6. |
| acceptance     | this module contains Cucumber BDD acceptance test statements and a test implementation to get you started. |
| order-service  | this module is for example purposes only. it is a restful webapp that executes rules and is invoked by the cucumber scripts |


**to build the project**

```mvn clean install```

**to run acceptance for CI (ie. start containers, deploy apps, run tests and stop containers)**

```acceptance/mvn clean install -Pbrms,itest```

**to develop (ie. start containers, deploy apps and pause so a dev can develop tests against the containers)**

```acceptance/mvn clean package cargo:run -DskipTests```

`head pom.xml` - gives you the two commands to copy/paste quickly


**To add more webapps to the acceptance tests**

in the cargo plugin config, add a <deployable> entry for your webapps maven GAV (groupId, artifactId and version) and it will be deployed


### Notes

** port 16080 was chosed for the web port because OpenShift will only allow ports between 15000-35530 to be bound 


**Things to add to this document**
* Emphasise that acceptance testing is a software contract test of _your_ software, not the systems it will integrate with
* acceptance tests should contain _domain language_, not technical terms. ie. do NOT use "should get http 200 from url X" 
* rule-domain should be interfaces that drools rules use to keep the rule-domain jar small (important for brms5s JCR repository). Any other domain jar should depend on rule-domain and contain concrete implementations.  Drools also performs quicker against interfaces with just its fields because of the mvel introspection language.
* acceptance test manages the tests so testing doesnt polute the applications
* acceptance forces the business-central instance to have a "target/repo" repository so BC doesnt pick up artifacts from the build but only the ones actually deployed.
* quickstart in general promotes that kjars should be separate (not embedded) in the applications so they are pulled in at runtime and can be changes by the business on the fly.




