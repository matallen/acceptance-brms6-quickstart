package org.jboss.demo.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.demo.domain.*;
import org.jboss.demo.webapp.utils.Json;
import org.jboss.demo.webapp.utils.RulesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class RestController {
  private static final Logger log = LoggerFactory.getLogger(RestController.class);
  private static final Map<String, Account> demoAccountRepository=new HashMap<String, Account>();
  private static final Map<String,Payment> demoPaymentsRepository=new HashMap<String, Payment>();
  
  @POST
  @Path("/accounts/create")
  public Response createAccount(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    String payload=IOUtils.toString(request.getInputStream());
    log.info("[/accounts/create] Called with payload "+payload);
    Account r=(Account)Json.toObject(payload, Account.class);
    
    Account newAccount=new AccountBuilder()
      .accountId(r.getAccountId())
      .firstName(r.getFirstName())
      .surname(r.getSurname())
      .balance(r.getBalance())
      .overdraft(r.getOverdraft())
      .build();
    
    demoAccountRepository.put(newAccount.getAccountId(), newAccount);
    
    String result=Json.toJson(newAccount);
    log.info("[/accounts/create] Returning payload ["+result+"]");
    return Response.status(200).entity(result).build();
  }
  
  @POST
  @Path("/payments/create")
  public Response createPayment(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    String payload=IOUtils.toString(request.getInputStream());
    log.info("[/payments/create] Called with payload "+payload);
    Payment r=(Payment)Json.toObject(payload, Payment.class);
    
    Payment newPayment=new PaymentBuilder()
      .id(r.getId())
      .fromAccount(r.getFromAccount())
      .toAccount(r.getToAccount())
      .value(r.getValue())
      .build();
    
    demoPaymentsRepository.put(newPayment.getId(), newPayment);
    
    String result=Json.toJson(newPayment);
    log.info("[/payments/create] Returning payload ["+result+"]");
    return Response.status(200).entity(result).build();
  }
  
  @GET
  @Path("/payments/{paymentId}")
  public Response getPayment(@PathParam("paymentId") String paymentId, @Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    String result=Json.toJson(demoPaymentsRepository.get(paymentId));
    log.info("[/payments/"+paymentId+"] Returning payload ["+result+"]");
    return Response.status(200).entity(result).build();
  }
  
  @GET
  @Path("/accounts/{accountId}/balance")
  public Response getBalance(@PathParam("accountId") String accountId, @Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    Account account=demoAccountRepository.get(accountId);
    return Response.status(200).entity(String.valueOf(account.getBalance())).build();
  }
  
  @POST
  @Path("/payments/process")
  public Response processPayments(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    String originalKieMavenCustomSettings=setCustomMavenSettings();
    
    for(Entry<String,Payment> e:demoPaymentsRepository.entrySet()){
      Payment payment=e.getValue();
      Account fromAccount=demoAccountRepository.get(payment.getFromAccount());
      Account toAccount=demoAccountRepository.get(payment.getToAccount());
      
      // execute the rules to determine if Payment can/should be made
      new RulesService(){
        public String getKieSessionName(){
          return "payments";
      }}.execute(payment, fromAccount, toAccount);
      
      // "sent" flag means Payment should be made, so make it
      if (payment.getSent()){ // rules determine payment should be sent
        toAccount.credit(payment.getValue());
        fromAccount.debit(payment.getValue());
      }
      
      // OLD CODE for making payment - its moved to rules now
      
//      double value=e.getValue().getValue();
//      if ((fromAccount.getBalance()+fromAccount.getOverdraft())>value){
//        fromAccount.debit(value);
//        toAccount.credit(value);
//        e.getValue().notify(fromAccount.getAccountId(), "SUCCESS");
//        System.out.println("payment made");
//      }else{
//        System.err.println("no payment made");
//        e.getValue().notify(fromAccount.getAccountId(), "FAILURE");
//      }
    }
    
    restoreMavenSettings(originalKieMavenCustomSettings);
    
    return Response.status(200).entity("OK").build();
  }
  
  @POST
  @Path("/clear")
  public Response clear(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
    demoAccountRepository.clear();
    demoPaymentsRepository.clear();
    return Response.status(200).entity("OK").build();
  }
  
  
  private String setCustomMavenSettings(){
    String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
    System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
    return originalKieMavenSettingsCustom;
  }
  private void restoreMavenSettings(String originalValue){
    if (null==originalValue){
      System.clearProperty("kie.maven.settings.custom");
    }else
      System.setProperty("kie.maven.settings.custom", originalValue);
  
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
//  @POST
//  @Path("/start")
//  public Response start(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
//    try{
//      String payload=IOUtils.toString(request.getInputStream());
//      log.info("[/start] Called with payload "+payload);
//      Order order=(Order)Json.toObject(payload, Order.class);
//      
//      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
//      System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
//      
//      Map<String,Object> parameters=new HashMap<String, Object>();
//      parameters.put("order", order);
//      
//      long processId=new RulesService(){public String getKieSessionName(){
//          return "order.process";
//      }}.startProcess("OrderProcess", parameters);
//      
//      order.setProcessId(processId);
//      
//      if (null==originalKieMavenSettingsCustom){
//        System.clearProperty("kie.maven.settings.custom");
//      }else
//        System.setProperty("kie.maven.settings.custom", originalKieMavenSettingsCustom);
//      
//      
//      // perhaps we have another async call to check on the order process status?
//      
//      String result=Json.toJson(order);
//      log.info("[/start] Returning payload ["+result+"]");
//      return Response.status(200).entity(result).build();
//    }catch(Exception e){
//      return Response.status(500).entity(e.getMessage()).build();
//    }
//  }
//  
//  @POST
//  @Path("/riskcheck")
//  public Response riskCheck(@Context HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException {
//    String payload=IOUtils.toString(request.getInputStream());
//    log.info("[/riskcheck] Called with payload "+payload);
//    Order order=(Order)Json.toObject(payload, Order.class);
//    
//    try{
//      // best practice - initialise default values outside of rules if possible. Don't write rules that you know will operate on all facts - it will be slower to execute than pure java.
//      order.setRisk("HIGH");
//      order.setRecommendation("REJECT");
//      
//      String originalKieMavenSettingsCustom=System.getProperty("kie.maven.settings.custom");
//      System.setProperty("kie.maven.settings.custom", System.getProperty("kie.maven.client.settings.custom"));
//      
////      new RulesService().execute(order);
//      
//      new RulesService(){
//        public String getKieSessionName(){
//          return "order.risk";
//        }
//      }.execute(order);
//      
//      
//      if (null==originalKieMavenSettingsCustom){
//        System.clearProperty("kie.maven.settings.custom");
//      }else
//        System.setProperty("kie.maven.settings.custom", originalKieMavenSettingsCustom);
//      
//      
//      String result=Json.toJson(order);
//      log.info("[/riskcheck] Returning payload ["+result+"]");
//      return Response.status(200).entity(result).build();
//      
//    }catch(IOException e){
//      return Response.status(500).entity(e.getMessage()).build();
//    }
//  }
}
