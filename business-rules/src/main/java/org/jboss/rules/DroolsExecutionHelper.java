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

package org.jboss.rules;

import org.kie.api.KieServices;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsExecutionHelper {
  private static final Logger log = LoggerFactory.getLogger(DroolsExecutionHelper.class);
  private ProcessContext kcontext;
  private String kieSession;
  private String variableName;
  
  public DroolsExecutionHelper(ProcessContext kcontext){
    this.kcontext=kcontext;
  }
  public DroolsExecutionHelper withSession(String kieSessionName){
    this.kieSession=kieSessionName; return this;
  }
  
  public DroolsExecutionHelper withVariable(String variableName){
    this.variableName=variableName; return this;
  }
  
  public void fireRules(){
    Object variable=(Object)kcontext.getVariable(variableName);
    StatefulKnowledgeSession s=(StatefulKnowledgeSession)KieServices.Factory.get().getKieClasspathContainer().newKieSession(kieSession);
    
    if (0==s.getKieBase().getKiePackages().size()) throw new RuntimeException("No rules in kBase!!!");
    log.debug("Rules:");
    for (KiePackage kp : s.getKieBase().getKiePackages()) {
      for (Rule r : kp.getRules())
        log.debug(kp.getName()+"->"+r.getName());
    }
    s.insert(variable);
    int rulesFired=s.fireAllRules();
    log.debug(rulesFired+" rule(s) fired");
    kcontext.setVariable(variableName, variable); // put it back after rules have modified it
  }
  
}
