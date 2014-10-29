package org.jboss.rules;


import org.jboss.order.domain.Order;
import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.internal.runtime.StatefulKnowledgeSession;
public class RiskCheck {
  private ProcessContext kcontext;
  private String kieSession;
  private String variableName;
  
  public RiskCheck(ProcessContext kcontext){
    this.kcontext=kcontext;
  }
  public RiskCheck withSession(String kieSessionName){
    this.kieSession=kieSessionName; return this;
  }
  
  public RiskCheck withVariable(String variableName){
    this.variableName=variableName; return this;
  }
  
  public void fireRules(){
    Object variable=(Object)kcontext.getVariable(variableName);
    StatefulKnowledgeSession s=(StatefulKnowledgeSession)KieServices.Factory.get().getKieClasspathContainer().newKieSession(kieSession);
    
    if (0==s.getKieBase().getKiePackages().size()) throw new RuntimeException("No rules in kBase!!!");
    System.out.println("\nRules:");
    for (KiePackage kp : s.getKieBase().getKiePackages()) {
      for (Rule r : kp.getRules())
        System.out.println(r.getName());
    }
    s.insert(variable);
    System.out.println("BEFORE RULES EXECUTION = "+variable);
    int rulesFired=s.fireAllRules();
    System.out.println(rulesFired+" rule(s) fired");
    kcontext.setVariable(variableName, variable); // put it back after rules have modified it
    
    System.out.println("AFTER RULES EXECUTION = "+variable);
  }
  
}
