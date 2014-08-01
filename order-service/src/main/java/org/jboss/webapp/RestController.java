package org.jboss.webapp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RestController {
//  private static final Logger log=Logger.getLogger(RestController.class);
  private static final Logger log = LoggerFactory.getLogger(RestController.class);
  
  private static KieScanner kScanner=null;
  private static KieContainer kContainer=null;
  
  @GET
  @Path("/version")
  public Response displayVersion() {
    System.out.println("Version info being requested");
    String result="Version info unknown";
    try {
      result = IOUtils.toString(this.getClass().getResourceAsStream("/META-INF/MANIFEST.MF")).replaceAll("\n", "<br/>");
    } catch (Exception sink) {}
    return Response.status(200).entity(result).build();
  }
  
//  public static void main(String[] asd) throws JsonGenerationException, JsonMappingException, IOException{
//    System.out.println(Json.toJson(new Order("01", Country.GBR, 100, new String[]{})));
//    System.out.println(Json.toObject("{\"id\":\"01\",\"country\":\"GBP\",\"amount\":100.0,\"items\":[]}", Order.class));
//    System.setProperty("kie.maven.client.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/client-settings.xml");
//    Order order=new Order("1", Country.GBR, 100, new String[]{});
//    System.out.println(new RestController().execute(order).getEntity());
//  }

  @POST
  @Path("/riskcheck")
  public Response execute(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    System.out.println("xxxx");
    String payload=IOUtils.toString(request.getInputStream());
    log.info("[/riskcheck] Called with payload "+payload);
    System.out.println("[/riskcheck] Called with payload \"+payload");
    Order order=(Order)Json.toObject(payload, Order.class);
    
    return execute(order);
  }
  
//  private boolean useExternalSettingXml=true;
  
  public Response execute(Order order) {
    try{
      
      // defaults - this is because if you operate on all facts then drools is slower than java, so set your defaults outside of the drools engine if you want performance
      order.setRisk("HIGH");
      order.setRecommendation("REJECT");
      
      if (order.getId().equals("01")){
        System.err.println("\n***************\nWARNING: RULES ARE NOT DOING ANYTHING!!!!!!!\n****************");
        String result=Json.toJson(order);
        log.info("[/riskcheck] Returning payload ["+result+"]");
        return Response.status(200).entity(result).build();
      }
      
      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
//      if (useExternalSettingXml){
        String kieMavenSettingsCustom=System.getProperty("kie.maven.client.settings.custom");
        System.setProperty("kie.maven.settings.custom", kieMavenSettingsCustom);
        log.debug("using [kie.maven.settings.custom] = "+kieMavenSettingsCustom);
//      }else{
//        System.clearProperty("kie.maven.settings.custom");
//      }
      
      Metrics metrics=new Metrics();
      
      if (null==kScanner){
        metrics.start();
        KieServices kieServices = KieServices.Factory.get();
        metrics.end("InitkieServices");
        
        // finds the kjar in the maven repo pointed to by the "-Dkie.maven.settings.custom=settings.xml"
//        String version="LATEST"; // doesnt work!
//        String version="6.0.0-SNAPSHOT";
//        kContainer=kieServices.newKieContainer(kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", version)); //6.0.0-SNAPSHOT
        
        kContainer=kieServices.newKieClasspathContainer(); // uses kie modules from the maven dependencies
        
        metrics.end("InitKieContainer");
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
//        boolean usePhreak=false;
        
//        if (usePhreak){
//          log.debug("Using Phreak");
          s = kContainer.newKieSession("defaultKieBase.session");
//        }else{
//          log.debug("Using Reteoo");
//          KieBaseConfiguration kconf = org.kie.internal.KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
//          kconf.setOption(RuleEngineOption.RETEOO);
//          s=kContainer.newKieBase("risk.kbase", kconf).newKieSession();
//        }
        metrics.end("initKieSession");
        
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
        metrics.end("fireAllRules");
        
        log.debug("Order after rules = "+order);
        
        log.debug("Drools execution metrics:");
        log.debug(String.format("%-10s | %-10s | %-10s | %-10s |", "kServ(ms)","kCont(ms)","kSess(ms)","fireRules"));
        log.debug(String.format("%-10s | %-10s | %-10s | %-10s |", metrics.get("InitkieServices"),metrics.get("InitKieContainer"),metrics.get("initKieSession"),metrics.get("fireAllRules")));
        
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
