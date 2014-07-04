package org.jboss.acceptance;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(format = {/*"html:target/cucumber-html-report", */"json-pretty:target/cucumber-json-report.json"})
public class RunCucumberTests {

  @Test
  public void runAcceptanceTests(){
  }
}
