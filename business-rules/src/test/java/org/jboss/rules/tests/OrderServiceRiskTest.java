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

package org.jboss.rules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.apache.commons.lang.StringUtils;
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.jboss.order.domain.OrderBuilder;
import org.jboss.rules.RulesTestBase;
import org.junit.Test;

public class OrderServiceRiskTest extends RulesTestBase{
  /**
   * Good Practices - unit tests...
   * 1) should be self-contained
   * 2) should re-initialise variables so one test doesn't not affect another
   * 
   */
  
  @Test
  public void test_EuroLowValue_shouldAccept() {
//    compileAndLoadKieSession("order.risk");
    loadKieSession("order.risk");
    
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .amount(50.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("ACCEPT", order.getRiskStatus());
    assertTrue(StringUtils.isEmpty(order.getRiskReason()));
    assertEquals(1, rules);
  }
  
  @Test
  public void test_EuroMediumValue_shouldRefer() {
    loadKieSession("order.risk");
    
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .amount(200.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("REFER", order.getRiskStatus());
    assertEquals("MEDIUM ORDER VALUE", order.getRiskReason());
    assertEquals(1, rules);
  }
  

  @Test
  public void test_EuroHighValue_shouldReject() {
    loadKieSession("order.risk");
    
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .amount(1200.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("REJECT", order.getRiskStatus());
    assertEquals("ORDER AMOUNT TOO HIGH", order.getRiskReason());
    assertEquals(1, rules);
  }
  
  @Test
  public void test_UnknownCountry_shouldReject() {
    loadKieSession("order.risk");
    
    Order order=new OrderBuilder().id("1")
      .country(Country.AFG)
      .amount(10.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("REJECT", order.getRiskStatus());
    assertEquals("COUNTRY NOT KNOWN", order.getRiskReason());
    assertEquals(1, rules);
  }
}
