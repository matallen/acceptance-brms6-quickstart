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

package org.jboss.order.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Order {
  private String id;
  private Country country;
  private double amount;
  private long processId;
  
  // rule setters
  private boolean riskCheck;
  private String riskStatus;
  private String riskReason;
  
  
  public Order(){} //for json
  public Order(String id, Country country, double amount) {
    super();
    this.id=id;
    this.country=country;
    this.amount=amount;
  }
  
  public void setId(String id) {
    this.id=id;
  }
  public void setCountry(Country country) {
    this.country=country;
  }
  public void setAmount(double amount) {
    this.amount=amount;
  }
  public String getId() {
    return id;
  }
  public Country getCountry() {
    return country;
  }
  public double getAmount() {
    return amount;
  }
  public String getRiskStatus() {
    return riskStatus;
  }
  public void setRiskStatus(String riskStatus) {
    this.riskStatus=riskStatus;
  }
  public String getRiskReason() {
    return riskReason;
  }
  public void setRiskReason(String riskReason) {
    this.riskReason=riskReason;
  }
  public void setProcessId(long processId) {
    this.processId=processId;
  }
  public long getProcessId(){
    return processId;
  }
  public boolean isRiskCheck() {
    return riskCheck;
  }
  public void setRiskCheck(boolean riskCheck) {
    this.riskCheck=riskCheck;
  }
  public String toString(){
    return ToStringBuilder.reflectionToString(this);
  }
}
