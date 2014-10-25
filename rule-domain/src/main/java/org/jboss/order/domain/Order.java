package org.jboss.order.domain;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Order {
  
  private String id;
  private Country country;
  private double amount;
  private String[] items;
  private long processId;
  
  // rule setters
  private String risk;
  private String recommendation;
  
  
  public Order(){} //for json
  public Order(String id, Country country, double amount, String[] items) {
    super();
    this.id=id;
    this.country=country;
    this.amount=amount;
    this.items=items;
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
  public void setItems(String[] items) {
    this.items=items;
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
  public String[] getItems() {
    return items;
  }
  public String getRisk() {
    return risk;
  }
  public void setRisk(String risk) {
    this.risk=risk;
  }
  public String getRecommendation() {
    return recommendation;
  }
  public void setRecommendation(String recommendation) {
    this.recommendation=recommendation;
  }
  public void setProcessId(long processId) {
    this.processId=processId;
  }
  public long getProcessId(){
    return processId;
  }
    
  public String toString(){
    return ToStringBuilder.reflectionToString(this);
  }
  
}
