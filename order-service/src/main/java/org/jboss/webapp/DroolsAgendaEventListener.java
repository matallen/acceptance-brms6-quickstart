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

package org.jboss.webapp;

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
