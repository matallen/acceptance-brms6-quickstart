package org.jboss.rules.tests;

import static org.junit.Assert.assertEquals;

import org.jboss.rules.RulesTestBase;
import org.junit.Test;

public class BusinessFunctionTest extends RulesTestBase{

	@Test
	public void testRuleForBusinessFunctionOutcomeXYZ() {
		loadRules("businessgrouping.servicegrouping", "1.0.0-SNAPSHOT");
		
		StringBuffer fact=new StringBuffer("this is");
		
		fireAllRules(fact);
		
		System.out.println(fact);
		
		assertEquals("magic", fact.toString());
	}
	
}
