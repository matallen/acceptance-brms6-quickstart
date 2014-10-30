package org.jboss.demo.domain;

public class Payment {
  private String id;
  private String fromAccount;
  private String toAccount;
  private double value;
  private String notifiedAccount;
  private String notifiedOutcome;
  public Payment(){
    //simple constructor for JSON
  }
  public Payment(String id,String fromAccount, String toAccount, double value) {
    super();
    this.id=id;
    this.fromAccount=fromAccount;
    this.toAccount=toAccount;
    this.value=value;
  }
  public String getFromAccount() {
    return fromAccount;
  }
  public String getToAccount() {
    return toAccount;
  }
  public String getId() {
    return id;
  }
  public double getValue() {
    return value;
  }
  public boolean hasBeenNotified(){
    return null!=notifiedAccount;
  }
  public String getNotifiedAccount() {
    return notifiedAccount;
  }
  public void notify(String accountId, String outcome) {
    this.notifiedAccount=accountId;
    this.notifiedOutcome=outcome;
  }
  public String getNotifiedOutcome() {
    return notifiedOutcome;
  }
  
  // for JSON
  public void setId(String id) {
    this.id=id;
  }
  public void setFromAccount(String fromAccount) {
    this.fromAccount=fromAccount;
  }
  public void setToAccount(String toAccount) {
    this.toAccount=toAccount;
  }
  public void setValue(double value) {
    this.value=value;
  }
  public void setNotifiedAccount(String notifiedAccount) {
    this.notifiedAccount=notifiedAccount;
  }
  public void setNotifiedOutcome(String notifiedOutcome) {
    this.notifiedOutcome=notifiedOutcome;
  }
}
