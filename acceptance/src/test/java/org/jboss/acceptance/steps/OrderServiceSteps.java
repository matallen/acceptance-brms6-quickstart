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

package org.jboss.acceptance.steps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.plexus.util.StringUtils;
import org.jboss.acceptance.utils.Json;
import org.jboss.acceptance.utils.Utils;
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.response.Response;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class OrderServiceSteps{
  private static final Logger log=LoggerFactory.getLogger(OrderServiceSteps.class);
//  private static final String ORDER_SERVICE_URL=System.getProperty("target.server")+"/order-service";
  private static final String ORDER_SERVICE_URL="http://localhost:16080/order-service";
  private List<Order> orders=new ArrayList<Order>();
  
  private static boolean initialised = false;
  @Before public void beforeAll(){
    if(!initialised) initialised=Utils.beforeScenarios();
  }

  @Given("^the order service is deployed$")
  public void the_order_service_is_deployed() throws Throwable {
    assertEquals(200, given().when().get(ORDER_SERVICE_URL+"/version").getStatusCode());
  }

//  @Given("^an order exists with the following details:$")
//  public void an_order_exists_with_the_following_details(List<Map<String,String>> table) throws Throwable {
//  }
  @Then("a new order is created with the following details:$")
  public void a_new_order_is_created_with_the_following_details(List<Map<String,String>> table) throws Throwable {
//    an_order_exists_with_the_following_details(table);
    orders.clear();
    for(Map<String,String> row:table){
      String id=row.get("ID");
      String country=row.get("Country");
      double amount=Double.valueOf(row.get("Amount"));
//      String[] items=row.get("Items").split(",");
      orders.add(new Order(id, Country.valueOf(country), amount));
    }
  }

  @When("^the risk check is performed$")
  public void the_risk_check_is_performed() throws Throwable {
    for(Order order:orders)
      riskCheckOrder(order);
  }
  
  @When("^the order is submitted$")
  public void the_order_is_submitted() throws Throwable {
    for(Order order:orders){
      String payload="{\"id\":\""+order.getId()+"\",\"country\":\""+order.getCountry().name()+"\",\"amount\":"+order.getAmount()+"}";
      Response response=given().when().body(payload).post(ORDER_SERVICE_URL+"/rest/order/new");
      String responseString=response.asString();
      if (response.getStatusCode()!=200)
        throw new RuntimeException("Response was ["+response.getStatusLine()+"], with content of ["+responseString+"]");
      assertEquals(200, response.getStatusCode());
      Order responseOrder=Json.toObject(responseString, Order.class);
      assertTrue("The response order should have a positive processId, "+responseOrder.getProcessId()+" was returned. Whole response = "+responseString, responseOrder.getProcessId()>0);
    }
  }
  
  public void riskCheckOrder(Order order) throws JsonParseException, JsonMappingException, IOException{
    String payload="{\"id\":\""+order.getId()+"\",\"country\":\""+order.getCountry().name()+"\",\"amount\":"+order.getAmount()+",\"items\":[]}";
    Response response=given().when().body(payload).post(ORDER_SERVICE_URL+"/rest/riskcheck");
    String responseString=response.asString();
    if (response.getStatusCode()!=200)
      throw new RuntimeException("Response was ["+response.getStatusLine()+"], with content of ["+responseString+"]");
    assertEquals(200, response.getStatusCode());
    Order responseOrder=Json.toObject(responseString, Order.class);
    order.setRiskStatus(responseOrder.getRiskStatus());
    order.setRiskReason(responseOrder.getRiskReason());
  }
  
  @Then("^the responses should be:$")
  public void the_result_should_be(List<Map<String,String>> table) throws Throwable {
    int ordersCheckedCount=0;
    assertEquals("You must provide expected responses for all orders", table.size(), orders.size());
    for(Map<String,String> row:table){
      Response response=given().when().post(ORDER_SERVICE_URL+"/rest/order/"+row.get("ID"));
      Order order=(Order)Json.toObject(response.asString(), Order.class);
      assertEquals(row.get("Risk Rating"), order.getRiskStatus());
      // allow null or empty
      assertTrue("Found \""+order.getRiskReason()+"\"","".equals(row.get("Reason"))?StringUtils.isEmpty(order.getRiskReason()):order.getRiskReason().equalsIgnoreCase(row.get("Reason")));
      ordersCheckedCount+=1;
    }
    assertEquals(table.size(), ordersCheckedCount);
  }
  
  
}
