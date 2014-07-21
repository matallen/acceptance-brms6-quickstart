package org.jboss.rules.tests;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.jboss.rules.RulesTestBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class OrderServiceRulesTest extends RulesTestBase{

	@Test
	@Ignore
	public void testUsingKieBuilder() {
	  loadRules("risk.def,risk.rules");
	  
	  Order order=new Order("1", Country.GBR, 100, new String[]{});
		int rules=fireAllRules(order);
		
		// assertions
		assertEquals("LOW", order.getRisk());
		assertEquals("ACCEPT", order.getRecommendation());
		assertEquals(2, rules);
	}
	
	@Test
	@Ignore
	public void testUsingKModule(){
    KieServices ks = KieServices.Factory.get();
    KieContainer kContainer = ks.getKieClasspathContainer();
//    KieBase kBase = kContainer.getKieBase("risk.kbase");
    KieSession s = kContainer.newKieSession("default.ksession");
    
    if (0==s.getKieBase().getKiePackages().size()) throw new RuntimeException("No rules in kBase!!!");
    
    System.out.println("Rules:");
    for(KiePackage kp:s.getKieBase().getKiePackages()){
      for(Rule r:kp.getRules()){
        System.out.println(r.getName());
      }
    }
    
    Order order=new Order("01", Country.GBR, 100, new String[]{});
    s.insert(order);
    
    AgendaEventListener listener=new AgendaEventListener() {
      public void matchCreated(MatchCreatedEvent event) {}
      public void matchCancelled(MatchCancelledEvent event) {}
      public void beforeMatchFired(BeforeMatchFiredEvent event) {}
      public void afterMatchFired(AfterMatchFiredEvent event) {
        System.err.println("Fired: "+event.getMatch().getRule().getName() +" ["+event.getMatch().getObjects()+"]");
      }
      
      public void agendaGroupPushed(AgendaGroupPushedEvent event) {}
      public void agendaGroupPopped(AgendaGroupPoppedEvent event) {}
      public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
      public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
      public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
      public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
    };
    s.addEventListener((AgendaEventListener)listener);
    
    try{
      int rules = s.fireAllRules();
      System.err.println("Order after rules = "+order);
      assertEquals(2, rules);
    }finally{
      s.removeEventListener((AgendaEventListener)listener);
      s.dispose();
    }
	}
	
	
	private KieContainer kContainer=null;
	private KieScanner kScanner=null;
	@Test
	public void testIntegration(){
	  // TODO: set the system variable to force the use of a specific settings.xml to point to the http://localhost:8080/business-central/maven2
//	  System.setProperty("kie.maven.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/settings.xml");
//	  System.setProperty("kie.maven.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/client-settings.xml");
//	  ResourceBundle properties = ResourceBundle.getBundle("app");
//	  System.setProperty("kie.maven.settings.custom", properties.getString("kie.maven.settings.custom"));//"/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/client-settings.xml");
	  System.out.println("[kie.maven.settings.custom] = "+System.getProperty("kie.maven.settings.custom"));
	  
	  if (null==kScanner){
	    KieServices kieServices = KieServices.Factory.get();
  	  
  	  kContainer = kieServices.newKieContainer(kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT"));
  	  
//  	  kScanner = kieServices.newKieScanner(kContainer);
//  	  kScanner.scanNow();
//  	  kScanner.start(10000l);
	  }
	  
    assertEquals(2, execute());
    assertEquals(2, execute());
    assertEquals(2, execute());
    assertEquals(2, execute());
    
	  System.clearProperty("kie.maven.settings.custom");
	}
	
	private int execute(){
	  long start=System.currentTimeMillis();
    KieSession s = kContainer.newKieSession("risk.ksession");
    System.out.println("Rules:");
    for(KiePackage kp:s.getKieBase().getKiePackages()){
      for(Rule r:kp.getRules()){
        System.out.println(" - "+r.getName());
      }
    }
    
    Order order=new Order("01", Country.GBR, 100, new String[]{});
    s.insert(order);
    
    int result=s.fireAllRules();
    System.out.println("duration (fired "+result+" rules)  = "+(System.currentTimeMillis()-start)+"ms");
    return result;
	}
	
}
