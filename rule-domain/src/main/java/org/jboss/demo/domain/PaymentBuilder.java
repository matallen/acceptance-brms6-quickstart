package org.jboss.demo.domain;

public class PaymentBuilder {
  private String id;
  private String fromAccount;
  private String toAccount;
  private double value;
  
  public PaymentBuilder fromAccount(String fromAccount){
    this.fromAccount=fromAccount; return this;
  }
  public PaymentBuilder id(String id){
    this.id=id; return this;
  }
  public PaymentBuilder toAccount(String toAccount){
    this.toAccount=toAccount; return this;
  }
  public PaymentBuilder value(double value){
    this.value=value; return this;
  }
  public Payment build(){
    return new Payment(id, fromAccount, toAccount, value);
  }
}
