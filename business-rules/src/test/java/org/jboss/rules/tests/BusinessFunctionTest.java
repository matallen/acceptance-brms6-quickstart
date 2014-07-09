package org.jboss.rules.tests;

import static org.junit.Assert.assertEquals;

import org.jboss.rules.RulesTestBase;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class BusinessFunctionTest extends RulesTestBase{

	@Test
	public void testUsingKieBuilder() {
		loadRules("schedules.S01,schedules.def");
		int rules=fireAllRules();
		assertEquals(2, rules);
	}
	
	@Test
	public void testUsingKModule(){
    KieServices ks = KieServices.Factory.get();
    KieContainer kContainer = ks.getKieClasspathContainer();
    KieBase kBase = kContainer.getKieBase("S01.kbase");
    KieSession s = kContainer.newKieSession("S01.ksession");
    
    System.out.println("Rules:");
    for(KiePackage kp:s.getKieBase().getKiePackages()){
      for(Rule r:kp.getRules()){
        System.out.println(r.getName());
      }
    }
    
    int rules = s.fireAllRules();
    assertEquals(2, rules);
	}
	
	
	
	public void testIntegration(){
	  // TODO: set the system variable to force the use of a specific settings.xml to point to the http://localhost:8080/business-central/maven2
	  
	  KieServices kieServices = KieServices.Factory.get();
	  ReleaseId releaseId = kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT");
	  KieContainer kContainer = kieServices.newKieContainer(releaseId);
	  KieScanner kScanner = kieServices.newKieScanner(kContainer);
	  
	  // poll m2 repo every 10 seconds
	  kScanner.start(10000l);
	}
	
}
