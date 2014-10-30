package org.jboss.demo.domain;

public class AccountBuilder {
  private String accountId;
  private String firstName;
  private String surname;
  private double balance;
  private double overdraft;
  public AccountBuilder accountId(String accountId){
    this.accountId=accountId;return this;
  }
  public AccountBuilder firstName(String firstName){
    this.firstName=firstName;return this;
  }
  public AccountBuilder surname(String surname){
    this.surname=surname;return this;
  }
  public AccountBuilder balance(double balance){
    this.balance=balance;return this;
  }
  public AccountBuilder overdraft(double overdraft) {
    this.overdraft=overdraft;return this;
  }
  public Account build(){
    return new Account(accountId, firstName, surname, balance, overdraft);
  }
}
