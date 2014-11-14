package org.jboss.demo.webapp.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.demo.webapp.DroolsAgendaEventListener;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesService {
  private static final Logger log = LoggerFactory.getLogger(RulesService.class);
  private static KieContainer kContainer=null;
  private static KieScanner kScanner=null;
  private static boolean headerPrinted=false;
  private static Map<String,Object> emptyMap=new HashMap<String, Object>();
  
  /**
   * Override this to change the kieSessionName. Default is defaultKieBase.session
   */
  public String getKieSessionName(){
    return "defaultKieBase.session";
  }
  
  public long startProcess(String processId){
    return startProcess(processId, emptyMap);
  }
  
  public long startProcess(String processId, Map<String,Object> parameters){
    Metrics metrics=new Metrics();
    
    metrics.start();
    KieServices kieServices = KieServices.Factory.get();
    metrics.end("InitkieServices");
    
    if (kContainer==null){
      kContainer=kieServices.newKieClasspathContainer(); // uses kie modules from the maven pom dependencies
      metrics.end("InitKieContainer");
    }
    
    KieSession session=null;
    try{
      session = kContainer.newKieSession(getKieSessionName());
      metrics.end("initKieSession");
      ProcessInstance processInstance=session.startProcess(processId, parameters);
      return processInstance.getId();
    }finally{
//      session.removeEventListener((AgendaEventListener)listener);
      if (null!=session) session.dispose();
    }
  }
  
  public <T> int execute(){
    return execute(new String[]{});
  }
  
  public <T> int execute(T... facts){
    return execute(Arrays.asList(facts));
  }
  
  public <T> int execute(Iterable<T> facts){
    Metrics metrics=new Metrics();
    
    metrics.start();
    KieServices kieServices = KieServices.Factory.get();
    metrics.end("InitkieServices");
    
    if (kScanner!=null)
      kScanner.scanNow();
    
    if (kContainer==null){
//      kContainer=kieServices.newKieClasspathContainer(); // uses kie modules from the maven pom dependencies
      kContainer=kieServices.newKieContainer(kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT"));
      metrics.end("InitKieContainer");
      
      kScanner=kieServices.newKieScanner(kContainer);
      kScanner.start(10000l);
    }
    
    AgendaEventListener listener=new DroolsAgendaEventListener();
    KieSession session=null;
    try{
      KieBaseConfiguration kConfig = kieServices.newKieBaseConfiguration();
      kConfig.setOption(EventProcessingOption.CLOUD);
      session = kContainer.newKieBase("defaultKieBase", kConfig).newKieSession();
//      session = kContainer.newKieSession(getKieSessionName());
      metrics.end("initKieSession");
      
      if (null==session) throw new RuntimeException("Unable to find ksession: "+getKieSessionName());
      if (0==session.getKieBase().getKiePackages().size()) throw new RuntimeException("No Rules in kBase!!!");
      
      if (log.isDebugEnabled()){
        log.debug("Rules in kieContainer:");
        for(KiePackage kp:session.getKieBase().getKiePackages()){
          for(Rule r:kp.getRules()){
            log.debug(" - "+kp.getName()+".\""+r.getName()+"\"");
          }
        }
      }
      
      session.addEventListener((AgendaEventListener)listener);
      
      for (Object fact:facts)
        session.insert(fact);
      metrics.end("insertFacts");
      
      int numberOfRulesFired=session.fireAllRules();
      metrics.end("fireAllRules");
      
      if (log.isDebugEnabled()){
        log.debug("Drools execution metrics:");
        if (!headerPrinted){
          log.debug(String.format("%-10s | %-10s | %-10s | %-10s |", "kServ(ms)","kCont(ms)","kSess(ms)","insertFacts","fireRules"));
          headerPrinted=true;
        }
        log.debug(String.format("%-10s | %-10s | %-10s | %-10s |", metrics.get("InitkieServices"),metrics.get("InitKieContainer"),metrics.get("initKieSession"),metrics.get("insertFacts"), metrics.get("fireAllRules")));
      }
      
      return numberOfRulesFired; // perhaps return the list of rule names fired provided by the event listener would be more useful
      
    }finally{
      if (null!=session){
        session.removeEventListener((AgendaEventListener)listener);
        session.dispose();
      }
    }
  }
}
