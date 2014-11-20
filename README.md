BDD/ATDD + BRMS6 Quickstart Project
=

### What problem is this project solving
We are encountering many more projects using BDD as a testing framework and also the use of BRMS within applications.
Given a world of infinite possibilities if you asked each customer team to start a BDD project with BRMS they'd each create it slightly differently, therefore it makes it more difficult for *Red Hat* to support and takes time for each team to develop such a framework.
As a consultant you want to "*wow*" your customer as quickly as you can so you can get on with the real nitty-gritty of the functionality the customer actually wants.


###Intentions
The purpose behind this is to provide a starting project with working black-box acceptance testing framework in place for any webapp, whether RESTful service or a GUI. It also provides a "good practice" structure for incorporating business rules into your webapp and has solved the integration issues of how an app "talks" to BRMS and how rules are deployed and tested.

By providing this basic project structure it should:

* Enable developers to get started on the real functionality in a much quicker timeframe
* Provide consistency between BDD + BRMS projects. If we all have a similar structure it means *Red Hat Support* can help with far greater accuracy and speed.
* You can look good in the eyes of your employer/customer for getting the project "up-and-running" so quickly!


The Solution
=

### Pre-conditions
* Install the maven-kie-deployer-plugin that pushes a pre-built kie module into a running BPM Suite

```
git clone https://github.com/matallen/maven-kie-deployer-plugin
cd maven-kie-deployer-plugin
mvn clean install -DskipTests
```

* Install the "business-central" BPM Suite webapp into your local m2 repo because it's not available in a public repo

```
./install/maven-install-business-central-only.sh <your downloaded distro from the Red Hat Portal>
```


### Structure
| Module         | Notes         |
| -------------  |:-------------:|
| rule-domain    | this is where any model classes go that as used in the rules |
| business-rules | In src/main/rules is where you write your rules, and write unit tests in src/test/java as usual. This module contains a RuleTestBase which provides easy rule compilation and execution helper methods. The "kie-maven-plugin" makes the output of the business-rules module a kjar which can be used by drools6. |
| acceptance     | this module contains Cucumber BDD acceptance test statements and a test implementation to get you started. |
| order-service  | this module is for example purposes only. it is a restful webapp that executes rules and is invoked by the cucumber scripts |


### Usage

**Build** - run `mvn clean install` from the project root. It is a multi-module project so it will build all except the self-contained "acceptance" test module.

**Acceptance Test (ie, Jenkins)** - run `acceptance/mvn clean install -Pbrms,acceptance`. The `brms` profile starts and stops the container (Tomcat/Eap) and deploys web apps (BPM Suite + your specified services). The `acceptance` profiles enables the cucumber BDD testing framework to run after the container has started.

**Developing BDD Tests** - run ` acceptance/mvn clean package cargo:run -Pbrms`. This will start the container and deploy BPM Suite + your specified services, leaving them running. You can now run the `RunCucumberTests` class as a JUnit test from Eclipse whilst developing your BDD features/statements.

**Notes**
`head pom.xml` - gives you the two commands above to copy/paste quickly

**Add more webapps to the acceptance tests**
In the cargo plugin config, add a `<deployable>` entry for your webapps maven GAV (groupId, artifactId and version) and it will be deployed


### Testing



### Notes

**Ports** - Port 16080 was chosen for the web port because OpenShift will allows only ports between 15000-35530 to be bound.


**Things to add to this document**
* Emphasise that acceptance testing is a software contract test of _your_ software, not the systems it will integrate with
* acceptance tests should contain _domain language_, not technical terms. ie. do NOT use "should get http 200 from url X" 
* rule-domain should be interfaces that drools rules use to keep the rule-domain jar small (important for brms5s JCR repository). Any other domain jar should depend on rule-domain and contain concrete implementations.  Drools also performs quicker against interfaces with just its fields because of the mvel introspection language.
* acceptance test manages the tests so testing doesnt polute the applications
* acceptance testing should be a collaboration effort between teams - BA, developers, architects and QA
* acceptance tests are *owned* and mostly developed by QA teams, not developers
* acceptance forces the business-central instance to have a "target/repo" repository so BC doesnt pick up artifacts from the build but only the ones actually deployed.
* quickstart in general promotes that kjars should be separate (not embedded) in the applications so they are pulled in at runtime and can be changes by the business on the fly.

