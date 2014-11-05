package org.jboss.rules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.jboss.demo.domain.Account;
import org.jboss.demo.domain.Payment;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;

public class PaymentsTest extends RulesTestBase {
  
  @Test
  public void basicTestThatShowsTheRulesMakePaymentWhenFundsAreAvailable() throws FileNotFoundException{
    
    System.out.println(parseDRLFromDSL(new File("src/main/resources/payments/language.dsl"), new File("src/main/resources/payments/payments.dslr")));
    
    KieSession s=super.loadKieSession();// compileAndLoadKieSession("payments");
    Payment payment=new Payment("A","1","2",100, false);
    s.insert(new Account("1","Mat","Allen",100,0));
    s.insert(new Account("2","Fred","Bloggs",0,0));
    s.insert(payment);
    
    AgendaEventListener l=new SysErrAgendaEventListener();
    s.addEventListener((AgendaEventListener)l);
    
    try{
      s.fireAllRules();
    }finally{
      s.removeEventListener((AgendaEventListener)l);
    }
    
    Assert.assertTrue(payment.getSent());
    Assert.assertEquals("1", payment.getNotifiedAccount());
    Assert.assertEquals("SUCCESS", payment.getNotifiedOutcome());
  }
}
