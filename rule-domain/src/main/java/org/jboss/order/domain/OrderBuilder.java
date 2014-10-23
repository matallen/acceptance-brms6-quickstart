package org.jboss.order.domain;

public class OrderBuilder {
  private String id;
  private Country country;
  private double amount;
  private String[] items;
  
  public OrderBuilder id(String id){
    this.id=id; return this;
  }
  public OrderBuilder country(Country country){
    this.country=country; return this;
  }
  public OrderBuilder amount(double amount){
    this.amount=amount; return this;
  }
  public Order build(){
    Order order=new Order(id, country, amount, new String[]{});
    // set defaults
    order.setRisk("HIGH");
    order.setRecommendation("REJECT");
    return order;
  }
}