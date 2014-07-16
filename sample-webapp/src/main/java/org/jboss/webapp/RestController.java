package org.jboss.webapp;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieRepositoryImpl.ComparableVersion;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

@Path("/rules")
public class RestController {
	private static KieScanner kScanner=null;
	private static KieContainer kContainer=null;
	
  @GET
  @Path("/execute/{fact}")
  public Response execute(@PathParam("fact") String fact) {
    
    
//    String oldSettings=System.getProperty("kie.maven.settings.custom");
//    System.setProperty("kie.maven.settings.custom", "/home/mallen/.m2/settings.xml");
//    System.setProperty("kie.maven.settings.custom", "");
//    System.setProperty("kie.maven.settings.custom", "/home/mallen/Applications/apache-maven-3.2.1/conf/settings.xml");
//    System.setProperty("kie.maven.settings.custom", oldSettings);
//    System.clearProperty("kie.maven.settings.custom");
//    System.setProperty("kie.maven.settings.custom", "/home/mallen/Work/poc/acceptance-brms6-quickstart/acceptance/target/classes/tomcat7x/client-settings.xml");
    
    if (null==kScanner){
      KieServices kieServices = KieServices.Factory.get();
      
      ReleaseId releaseId = kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT");
      
      Object x=new NaughtyPrivateAccessor(kieServices.getRepository()).getField("kieModuleRepo");
      System.out.println(x);
//      HashMap xx=(HashMap)new NaughtyPrivateAccessor(x).getField("kieModules");
//      System.out.println(xx.size());
//      System.out.println(xx);
//      System.out.println(xx.get(releaseId.getGroupId() + ":" + releaseId.getArtifactId()));
      
      //org.drools.compiler.kie.builder.impl.KieRepositoryImpl$KieModuleRepo@23691932
      //kieModuleRepo
      
      InternalKieModule kieModule = (InternalKieModule) kieServices.getRepository().getKieModule(releaseId);
      System.out.println(kieModule);
      
      kContainer = kieServices.newKieContainer(releaseId);
      KieScanner kScanner = kieServices.newKieScanner(kContainer);
      
      // poll m2 repo every 10 seconds
      kScanner.start(10000l);
      kScanner.scanNow();
    }
//    System.setProperty("kie.maven.settings.custom", oldSettings);
    
//    KieServices ks = KieServices.Factory.get();
//    KieContainer kContainer = ks.getKieClasspathContainer();
//    KieBase kBase = kContainer.getKieBase("S01.kbase");
    KieSession s = kContainer.newKieSession("S01.ksession");
    System.out.println("Rules:");
    for(KiePackage kp:s.getKieBase().getKiePackages()){
      for(Rule r:kp.getRules()){
        System.out.println(r.getName());
      }
    }
    int rules = s.fireAllRules();
    
  	return Response.status(200).entity(String.valueOf(rules)).build();
  }
}
