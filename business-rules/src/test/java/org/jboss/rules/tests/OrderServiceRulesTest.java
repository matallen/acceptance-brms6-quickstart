package org.jboss.rules.tests;

import static org.junit.Assert.assertEquals;

import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.jboss.rules.RulesTestBase;
import org.jboss.rules.SysErrAgendaEventListener;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class OrderServiceRulesTest extends RulesTestBase{

  @Test
  public void test_lowRisk() {
    loadKieSession("defaultKieBase.session");
    
    Order order=new Order("1", Country.GBR, 50, new String[]{});
    int rules=fireAllRules(order);
    
    assertEquals("LOW", order.getRisk());
    assertEquals("ACCEPT", order.getRecommendation());
    assertEquals(2, rules);
  }
  
  @Test
  public void test_highRiskG8() {
    loadKieSession("defaultKieBase.session");
    
    Order order=new Order("1", Country.GBR, 200, new String[]{});
    int rules=fireAllRules(order);
    
    assertEquals("HIGH", order.getRisk());
    assertEquals("REJECT", order.getRecommendation());
    assertEquals(1, rules);
  }
  
  @Test
  public void test_highRiskHighValue() {
    loadKieSession("defaultKieBase.session");
    
    Order order=new Order("1", Country.AFG, 200, new String[]{});
    int rules=fireAllRules(order);
    
    assertEquals("HIGH", order.getRisk());
    assertEquals("REJECT", order.getRecommendation());
    assertEquals(2, rules);
  }
  
//  
//	@Test
//	public void test_lowRiskx() {
//	  compileAndLoadKieSession("order.def,order.rules");
//	  
//	  Order order=new Order("1", Country.GBR, 100, new String[]{});
//		int rules=fireAllRules(order);
//		
//		assertEquals("LOW", order.getRisk());
//		assertEquals("ACCEPT", order.getRecommendation());
//		assertEquals(2, rules);
//	}
//	
//	
//	@Test
//	public void test_KModule__shouldFireInitAndAcceptG8Rule(){
//	  
//	  loadKieSession("defaultKieBase.session");
//    
//	  Order order=new Order("01", Country.GBR, 100, new String[]{});
//    
//	  session.insert(order);
//    
//    fireAllRules(order);
//    
//    AgendaEventListener listener=new SysErrAgendaEventListener();
//    session.addEventListener((AgendaEventListener)listener);
//    
//    try{
//      int rules = session.fireAllRules();
//      System.err.println("Order after rules = "+order);
//      assertEquals(2, rules);
//    }finally{
//      session.removeEventListener((AgendaEventListener)listener);
//      session.dispose();
//    }
//	}
//	
//	private KieContainer kContainer=null;
//	private KieScanner kScanner=null;
//	@Test
//	@Ignore
//	public void testIntegration(){
//	  // TODO: set the system variable to force the use of a specific settings.xml to point to the http://localhost:8080/business-central/maven2
////	  System.setProperty("kie.maven.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/settings.xml");
////	  System.setProperty("kie.maven.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/client-settings.xml");
//	  System.out.println("[kie.maven.settings.custom] = "+System.getProperty("kie.maven.settings.custom"));
//	  
//	  if (null==kScanner){
//	    KieServices kieServices = KieServices.Factory.get();
//  	  kContainer = kieServices.newKieContainer(kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT"));
//	  }
//	  
//    assertEquals(3, execute());
//    assertEquals(3, execute());
//    assertEquals(3, execute());
//    assertEquals(3, execute());
//    
//	  System.clearProperty("kie.maven.settings.custom");
//	}
//	
//	private int execute(){
//	  long start=System.currentTimeMillis();
//    KieSession s = kContainer.newKieSession(kSessionId);
//    System.out.println("Rules:");
//    for(KiePackage kp:s.getKieBase().getKiePackages()){
//      for(Rule r:kp.getRules()){
//        System.out.println(" - "+r.getName());
//      }
//    }
//    
//    Order order=new Order("01", Country.GBR, 100, new String[]{});
//    s.insert(order);
//    
//    int result=s.fireAllRules();
//    System.out.println("duration (fired "+result+" rules)  = "+(System.currentTimeMillis()-start)+"ms");
//    return result;
//	}
	
}
