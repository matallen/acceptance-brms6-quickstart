package org.jboss.demo.domain;

public class Account {
  private String accountId;
  private String firstName;
  private String surname;
  private double balance;
  private double overdraft;
  public Account(){
    //simple constructor for JSON
  }
  public Account(String accountId, String firstName, String surname, double balance, double overdraft) {
    super();
    this.accountId=accountId;
    this.firstName=firstName;
    this.surname=surname;
    this.balance=balance;
    this.overdraft=overdraft;
  }
  public String getAccountId(){
    return accountId;
  }
  public String getFirstName() {
    return firstName;
  }
  public String getSurname() {
    return surname;
  }
  public double getBalance() {
    return balance;
  }
  public double getOverdraft() {
    return overdraft;
  }
  public void debit(double value) {
    this.balance=this.balance-value;
  }
  public void credit(double value) {
    this.balance=this.balance+value;
  }
  
  // for JSON
  public void setAccountId(String accountId) {
    this.accountId=accountId;
  }
  public void setFirstName(String firstName) {
    this.firstName=firstName;
  }
  public void setSurname(String surname) {
    this.surname=surname;
  }
  public void setBalance(double balance) {
    this.balance=balance;
  }
  public void setOverdraft(double overdraft) {
    this.overdraft=overdraft;
  }
}
