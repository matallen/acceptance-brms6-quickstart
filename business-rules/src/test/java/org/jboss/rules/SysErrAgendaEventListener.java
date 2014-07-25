package org.jboss.rules;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;

public class SysErrAgendaEventListener implements AgendaEventListener{

    public void matchCreated(MatchCreatedEvent event) {}
    public void matchCancelled(MatchCancelledEvent event) {}
    public void beforeMatchFired(BeforeMatchFiredEvent event) {}
    public void afterMatchFired(AfterMatchFiredEvent event) {
      System.err.println("Fired: "+event.getMatch().getRule().getName() +" ["+event.getMatch().getObjects()+"]");
    }
    
    public void agendaGroupPushed(AgendaGroupPushedEvent event) {}
    public void agendaGroupPopped(AgendaGroupPoppedEvent event) {}
    public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
    public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
    public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
    public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
}
