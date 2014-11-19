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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import net.java.quickcheck.generator.PrimitiveGenerators;

import org.jboss.acceptance.utils.Utils;
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.jboss.order.domain.OrderBuilder;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class OrderServiceNFRSteps{
  private static final Logger log=LoggerFactory.getLogger(OrderServiceNFRSteps.class);
  private List<Order> orders=new ArrayList<Order>();
  
  private static boolean initialised = false;
  @Before public void beforeAll(){
    if(!initialised) initialised=Utils.beforeScenarios();
  }

  public class OrderFuture implements Callable<String> {
    private Order order;
    public OrderFuture(Order order){
        this.order=order;
    }
    @Override public String call() throws Exception {
        new OrderServiceSteps().riskCheckOrder(order);
        return order.getRiskStatus();
    }
  }
  
  long duration=-1;
  
  @Given("^(\\d+) random orders are generated$")
  public void random_orders_are_generated(int numberOfOrders, List<Map<String,String>> table) throws Throwable {
    String amountRange=table.get(0).get("Amount Range").trim();
    double amountLow=Double.valueOf(amountRange.split("-")[0]);
    double amountHigh=Double.valueOf(amountRange.split("-")[1]);
    for (int i=0;i<=numberOfOrders;i++){
      orders.add(new OrderBuilder().id(String.valueOf(i))
          .country(PrimitiveGenerators.enumValues(Country.class).next())
          .amount(PrimitiveGenerators.doubles(amountLow, amountHigh).next())
          .build());
    }
  }
  
  @SuppressWarnings("rawtypes")
  @When("^the risk checks are submitted with a concurrency of (\\d+)$")
  public void the_orders_are_submitted_with_a_concurrency_of(int concurrency) throws Throwable {
    List<FutureTask> futures=new ArrayList<FutureTask>();
    ExecutorService executor = Executors.newFixedThreadPool(concurrency);
    long start=System.currentTimeMillis();
    
    for(Order order:orders){
      FutureTask ft=new FutureTask<String>(new OrderFuture(order));
      futures.add(ft);
      executor.submit(ft);
    }
    
    while (futures.size()>0){
      List<FutureTask> remove=new ArrayList<FutureTask>();
      for(FutureTask f:futures){
        if (f.isDone()) remove.add(f);
      }
      for(FutureTask f:remove)
        futures.remove(f);
    }
    executor.shutdown();
    
    duration=System.currentTimeMillis()-start;
  }
  
  @Then("^all responses should be returned within (\\d+) seconds$")
  public void all_responses_should_be_returned_within_seconds(int totalResponsesTimeInSeconds) throws Throwable {
    System.out.println("Actual duration was :"+duration+"ms");
    Assert.assertTrue("Duration exceeded specified duration. Expected under "+totalResponsesTimeInSeconds+", actual was "+duration, duration<(totalResponsesTimeInSeconds*1000));
  }

}
