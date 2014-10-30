package org.jboss.demo.webapp;

import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DroolsAgendaEventListener implements AgendaEventListener {
  private static final Logger log = LoggerFactory.getLogger(DroolsAgendaEventListener.class);
  public void matchCreated(MatchCreatedEvent event) {}
  public void matchCancelled(MatchCancelledEvent event) {}
  public void beforeMatchFired(BeforeMatchFiredEvent event) {}
  public void afterMatchFired(AfterMatchFiredEvent event) {
    log.debug("Fired: "+event.getMatch().getRule().getName() +" ["+event.getMatch().getObjects()+"]");
  }
  public void agendaGroupPushed(AgendaGroupPushedEvent event) {}
  public void agendaGroupPopped(AgendaGroupPoppedEvent event) {}
  public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
  public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
  public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) {}
  public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {}
}
