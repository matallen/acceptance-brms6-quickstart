package org.jboss.acceptance;

import org.junit.Test;
import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
//@Cucumber.Options(format = {/*"html:target/cucumber-html-report", */"json-pretty:target/cucumber-json-report.json"})
@CucumberOptions(monochrome = true, format = {"pretty", "html:target/cucumber"})
public class RunCucumberTests {
  @Test
  public void runAcceptanceTests(){}
  
}
