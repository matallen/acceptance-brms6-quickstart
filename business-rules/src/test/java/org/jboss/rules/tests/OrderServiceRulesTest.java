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
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.jboss.order.domain.OrderBuilder;
import org.jboss.rules.RulesTestBase;
import org.junit.Test;

public class OrderServiceRulesTest extends RulesTestBase{
  /**
   * Good Practices - unit tests...
   * 1) should be self-contained
   * 2) should re-initialise variables so one test doesn't not affect another
   * 
   */
  
  @Test
  public void test_G3LowValue_shouldAccept() {
//    compileAndLoadKieSession("order.rules");
    loadKieSession();
    
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .amount(50.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("LOW", order.getRisk());
    assertEquals("ACCEPT", order.getRecommendation());
    assertEquals(2, rules);
  }
  
  @Test
  public void test_G3MediumValue_shouldRefer() {
    loadKieSession();
    
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .amount(200.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("MEDIUM", order.getRisk());
    assertEquals("REFER", order.getRecommendation());
    assertEquals(1, rules);
  }
  

  @Test
  public void test_G3HighValue_shouldReject() {
    loadKieSession();
    
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .amount(1200.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("HIGH", order.getRisk());
    assertEquals("REJECT", order.getRecommendation());
    assertEquals(2, rules);
  }
  
  @Test
  public void test_nonG3_shouldReject() {
    loadKieSession();
    
    Order order=new OrderBuilder().id("1")
      .country(Country.AFG)
      .amount(10.00)
      .build();
    int rules=fireAllRules(order);
    
    assertEquals("HIGH", order.getRisk());
    assertEquals("REJECT", order.getRecommendation());
    assertEquals(1, rules);
  }
}
