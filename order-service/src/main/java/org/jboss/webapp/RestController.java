/*
* JBoss, Home of Professional Open Source
* Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
* contributors by the @authors tag. See the copyright.txt in the
* distribution for a full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.jboss.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.order.domain.Order;
import org.jboss.webapp.utils.Json;
import org.jboss.webapp.utils.RulesService;
import org.jbpm.process.core.Process;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.ContextInstance;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RestController {
  private static final Logger log = LoggerFactory.getLogger(RestController.class);
  private static Map<String,ProcessInstance> orders=new HashMap<String, ProcessInstance>();
  
  @POST
  @Path("/order/{orderId}")
  public Response get(@PathParam("orderId") String orderId, @Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    
//    KieServices kieServices=KieServices.Factory.get();
//    KieContainer kContainer=kieServices.newKieContainer(kieServices.newReleaseId("org.jboss.quickstarts.brms6", "business-rules", "6.0.0-SNAPSHOT"));
//    KieSession kSession=kContainer.newKieSession("order.process");
//    org.jbpm.process.instance.ProcessInstance processInstance=(org.jbpm.process.instance.ProcessInstance)kSession.getProcessInstance(Long.parseLong(orderId));
//    VariableScopeInstance ctx=(VariableScopeInstance)processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
//    Order order=(Order)ctx.getVariable("order");
    
    ProcessInstance process=orders.get(orderId);
    org.jbpm.process.instance.ProcessInstance processInstance=(org.jbpm.process.instance.ProcessInstance)process;
    VariableScopeInstance ctx=(VariableScopeInstance)processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);
    Order order=(Order)ctx.getVariable("order");
    
    String result=Json.toJson(order);
    
    log.info("[/order/"+orderId+"] Returning payload ["+result+"]");
    return Response.status(200).entity(result).build();
  }
  
  @POST
  @Path("/order/new")
  public Response newOrder(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    try{
      String payload=IOUtils.toString(request.getInputStream());
      log.info("[/order/new] Called with payload "+payload);
      Order order=(Order)Json.toObject(payload, Order.class);
      
      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
      System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
      
      Map<String,Object> parameters=new HashMap<String, Object>();
      parameters.put("order", order);
      
      ProcessInstance process=new RulesService(){public String getKieSessionName(){
          return "order.process";
      }}.startProcess("OrderProcess", parameters);
      
      order.setProcessId(process.getId());
      
      orders.put(order.getId(), process);
      
      if (null==originalKieMavenSettingsCustom){
        System.clearProperty("kie.maven.settings.custom");
      }else
        System.setProperty("kie.maven.settings.custom", originalKieMavenSettingsCustom);
      
      // perhaps we have another async call to check on the order process status?
      
      String result=Json.toJson(order);
      log.info("[/order/new] Returning payload ["+result+"]");
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
      order.setRiskStatus("HIGH");
      order.setRiskReason("");
      
      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
      System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
      
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
