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

public class OrderServiceRiskGuidedTest extends RulesTestBase{
  
  @Test
  public void test_EuroCountriesNeedRiskChecks() {
    loadKieSession("order.riskguided");
    Order order=new OrderBuilder().id("1")
      .country(Country.GBR)
      .build();
    int rules=fireAllRules(order);
    assertEquals(true, order.isRiskCheck());
    assertEquals(1, rules);
  }
  
  @Test
  public void test_NonEuroCountriesDontNeedRiskChecks() {
    loadKieSession("order.riskguided");
    Order order=new OrderBuilder().id("1")
      .country(Country.AFG)
      .build();
    int rules=fireAllRules(order);
    assertEquals(false, order.isRiskCheck());
    assertEquals(0, rules);
  }
}
