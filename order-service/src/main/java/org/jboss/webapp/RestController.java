package org.jboss.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.order.domain.Order;
import org.jboss.webapp.utils.Json;
import org.jboss.webapp.utils.RulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RestController {
  private static final Logger log = LoggerFactory.getLogger(RestController.class);
  
  @POST
  @Path("/start")
  public Response start(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    try{
      String payload=IOUtils.toString(request.getInputStream());
      log.info("[/start] Called with payload "+payload);
      Order order=(Order)Json.toObject(payload, Order.class);
      
      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
      System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
      
      Map<String,Object> parameters=new HashMap<String, Object>();
      parameters.put("order", order);
      
      long processId=new RulesService(){public String getKieSessionName(){
          return "order.process";
      }}.startProcess("OrderProcess", parameters);
      
      order.setProcessId(processId);
      
      if (null==originalKieMavenSettingsCustom){
        System.clearProperty("kie.maven.settings.custom");
      }else
        System.setProperty("kie.maven.settings.custom", originalKieMavenSettingsCustom);
      
      
      // perhaps we have another async call to check on the order process status?
      
      String result=Json.toJson(order);
      log.info("[/start] Returning payload ["+result+"]");
      return Response.status(200).entity(result).build();
    }catch(Exception e){
      return Response.status(500).entity(e.getMessage()).build();
    }
  }
  
  @POST
  @Path("/riskcheck")
  public Response riskCheck(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    String payload=IOUtils.toString(request.getInputStream());
    log.info("[/riskcheck] Called with payload "+payload);
    Order order=(Order)Json.toObject(payload, Order.class);
    
    try{
      // best practice - initialise default values outside of rules if possible. Don't write rules that you know will operate on all facts - it will be slower to execute than pure java.
      order.setRisk("HIGH");
      order.setRecommendation("REJECT");
      
      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
      System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
      
//      new RulesService().execute(order);
      
      new RulesService(){
        public String getKieSessionName(){
          return "order.risk";
        }
      }.execute(order);
      
      
      if (null==originalKieMavenSettingsCustom){
        System.clearProperty("kie.maven.settings.custom");
      }else
        System.setProperty("kie.maven.settings.custom", originalKieMavenSettingsCustom);
      
      
      String result=Json.toJson(order);
      log.info("[/riskcheck] Returning payload ["+result+"]");
      return Response.status(200).entity(result).build();
      
    }catch(IOException e){
      return Response.status(500).entity(e.getMessage()).build();
    }
  }
}
