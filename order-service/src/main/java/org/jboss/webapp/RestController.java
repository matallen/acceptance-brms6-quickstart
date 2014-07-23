package org.jboss.webapp;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.kie.api.KieBaseConfiguration;
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
import org.kie.internal.builder.conf.RuleEngineOption;

@Path("/")
public class RestController extends javax.ws.rs.core.Application{
  private static final Logger log=Logger.getLogger(RestController.class);
  private static KieScanner kScanner=null;
  private static KieContainer kContainer=null;
  
  @GET
  @Path("/version")
  public Response displayVersion() {
    String result="Version info unknown";
    try {
      result = IOUtils.toString(this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF")).replaceAll("\n", "<br/>");
    } catch (Exception sink) {}
    return Response.status(200).entity(result).build();
  }
  
  public static void main(String[] asd) throws JsonGenerationException, JsonMappingException, IOException{
//    System.out.println(Json.toJson(new Order("01", Country.GBR, 100, new String[]{})));
//    System.out.println(Json.toObject("{\"id\":\"01\",\"country\":\"GBP\",\"amount\":100.0,\"items\":[]}", Order.class));
    
    System.setProperty("kie.maven.client.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/client-settings.xml");
    Order order=new Order("1", Country.GBR, 100, new String[]{});
    System.out.println(new RestController().execute(order).getEntity());
    
  }

  @POST
  @Path("/riskcheck")
  public Response execute(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    String payload=IOUtils.toString(request.getInputStream());
    log.info("[/riskcheck] Called with payload ["+payload+"]");
    
    Order order=(Order)Json.toObject(payload, Order.class);
    
    return execute(order);
  }
  
  public Response execute(Order order) {
    try{
      
      // defaults - this is because if you operate on all facts then drools is slower than java, so set your defaults outside of the drools engine if you want performance
      System.err.println("\n***************\nWARNING: RULES ARE NOT DOING ANYTHING!!!!!!!\n****************");
      order.setRisk("HIGH");
      order.setRecommendation("REJECT");
      
      if (System.getProperty("dummy-order-service")!=null){
        String result=Json.toJson(order);
        log.info("[/riskcheck] Returning payload ["+result+"]");
        return Response.status(200).entity(result).build();
      }
      
      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
      String kieMavenSettingsCustom=System.getProperty("kie.maven.client.settings.custom");
//    System.setProperty("kie.maven.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/client-settings.xml");
      System.setProperty("kie.maven.settings.custom", kieMavenSettingsCustom);
      
      long initKieServices=0,initKieContainer=0,initKieScanner=0,initKieSession,fireAllRules;
      
      if (null==kScanner){
        Metrics.start();
        KieServices kieServices = KieServices.Factory.get();
        initKieServices=Metrics.endReset();
        
        // finds the kjar in the maven repo pointed to by the "-Dkie.maven.settings.custom=settings.xml"
        log.debug("using [kie.maven.settings.custom] = "+kieMavenSettingsCustom);
        kContainer = kieServices.newKieContainer(kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT"));
        initKieContainer=Metrics.endReset();
      }
      
      AgendaEventListener listener=new AgendaEventListener() {
        public void matchCreated(MatchCreatedEvent event) {}
        public void matchCancelled(MatchCancelledEvent event) {}
        public void beforeMatchFired(BeforeMatchFiredEvent event) {}
        public void afterMatchFired(AfterMatchFiredEvent event) {
          log.error("Fired: "+event.getMatch().getRule().getName() +" ["+event.getMatch().getObjects()+"]");
        }
        
        public void agendaGroupPushed(AgendaGroupPushedEvent event) {}
        public void agendaGroupPopped(AgendaGroupPoppedEvent event) {}
        public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
        public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
        public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
      };
      
      KieSession s=null;
      try{
        Metrics.start();
        boolean usePhreak=false;
        
        if (usePhreak){
          log.debug("Using Phreak");
          s = kContainer.newKieSession("risk.ksession");
        }else{
          log.debug("Using Reteoo");
          KieBaseConfiguration kconf = org.kie.internal.KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
          kconf.setOption(RuleEngineOption.RETEOO);
          s=kContainer.newKieBase("risk.kbase", kconf).newKieSession();
        }
        initKieSession=Metrics.endReset();
        
        if (0==s.getKieBase().getKiePackages().size()) throw new RuntimeException("No Rules in kBase!!!");
        
        log.debug("Rules in kieContainer:");
        for(KiePackage kp:s.getKieBase().getKiePackages()){
          for(Rule r:kp.getRules()){
            log.debug(" - "+kp.getName()+".\""+r.getName()+"\"");
          }
        }
        
        s.addEventListener((AgendaEventListener)listener);
        
        s.insert(order);
        
        s.fireAllRules();
        fireAllRules=Metrics.endReset();
        
        log.error("Order after rules = "+order);
        
        log.debug("Drools execution metrics:");
        log.debug(String.format("%-10s | %-10s | %-10s | %-10s | %-10s |", "kServ(ms)","kCont(ms)","kScan(ms)","kSess(ms)","fireRules"));
        log.debug(String.format("%-10s | %-10s | %-10s | %-10s | %-10s |", initKieServices,initKieContainer,initKieScanner,initKieSession,fireAllRules));
        
        if (null==originalKieMavenSettingsCustom){
          System.clearProperty("kie.maven.settings.custom");
        }else
          System.setProperty("kie.maven.settings.custom", originalKieMavenSettingsCustom);
        log.debug("Restored [kie.maven.settings.custom] = "+System.getProperty("kie.maven.settings.custom"));
        
        String result=Json.toJson(order);
        log.info("[/riskcheck] Returning payload ["+result+"]");
        
        return Response.status(200).entity(result).build();
      
      }finally{
        s.removeEventListener((AgendaEventListener)listener);
        if (null!=s) s.dispose();
      }
      
    }catch(Exception e){
      return Response.status(500).entity(e.getMessage()).build();
    }
    
  }
  

}
