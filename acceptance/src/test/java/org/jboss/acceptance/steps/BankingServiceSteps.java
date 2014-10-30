package org.jboss.acceptance.steps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jboss.acceptance.utils.Json;
import org.jboss.acceptance.utils.Utils;
import org.jboss.demo.domain.Account;
import org.jboss.demo.domain.AccountBuilder;
import org.jboss.demo.domain.Payment;
import org.jboss.demo.domain.PaymentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BankingServiceSteps{
  private static final Logger log=LoggerFactory.getLogger(BankingServiceSteps.class);
  private static final String SERVICE_URL="http://localhost:16080/banking-service";
  private Map<String, Account> accounts=new HashMap<String, Account>();
  private Map<String,Payment> payments=new HashMap<String,Payment>();
  
  private static boolean initialised = false;
  @Before public void beforeAll(){
    if(!initialised) initialised=Utils.beforeScenarios();
    assertEquals("Banking service is not deployed", 200, given().when().get(SERVICE_URL+"/version").getStatusCode());
    assertEquals("Clearing old account data failed", 200, given().when().post(SERVICE_URL+"/rest/clear").getStatusCode());
    accounts.clear();
    payments.clear();
  }
  
  @Given("^there is an account:$")
  public void there_is_an_account(List<Map<String, String>> table) throws Throwable {
    for(Map<String,String> row:table){
      
      Account account=new Account();
      account.setAccountId(row.get("Account Id"));
      account.setFirstName(row.get("FirstName"));
      account.setSurname(row.get("Surname"));
      account.setBalance(Double.parseDouble(row.get("Balance")));
      account.setOverdraft(Double.parseDouble(row.get("Overdraft")));
      
      Account newAccount=post(SERVICE_URL+"/rest/accounts/create", account, Account.class);
      
      accounts.put(newAccount.getAccountId(), newAccount);
    }
  }

  @When("^there is a payment scheduled:$")
  public void there_is_a_payment_scheduled(List<Map<String, String>> table) throws Throwable {
    payments.clear();
    for(Map<String,String> row:table){
      Payment payment=new Payment();
      payment.setId(row.get("Payment Id"));
      payment.setFromAccount(row.get("From Account"));
      payment.setToAccount(row.get("To Account"));
      payment.setValue(Double.parseDouble(row.get("Value")));
      
      Payment newPayment=post(SERVICE_URL+"/rest/payments/create", payment, Payment.class);
      
      payments.put(newPayment.getId(), newPayment);
    }
  }

  @When("^there is enough funds in the account$")
  public void there_is_enough_funds_in_the_account() throws Throwable {
    for(Payment payment:payments.values()){
      String balance=get(SERVICE_URL+"/rest/accounts/"+payment.getFromAccount()+"/balance");
      double valueInAccount=Double.parseDouble(balance);
      double valueToTransfer=payment.getValue();
      assertTrue("Not enough funds in account, found "+valueInAccount+", needed "+valueToTransfer, valueInAccount>=valueToTransfer);
    }
  }
  
  @When("^there is not enough funds in the account$")
  public void there_is_not_enough_funds_in_the_account() throws Throwable {
    for(Payment payment:payments.values()){
      String balance=get(SERVICE_URL+"/rest/accounts/"+payment.getFromAccount()+"/balance");
      double valueInAccount=Double.parseDouble(balance);
      double valueToTransfer=payment.getValue();
      assertTrue("Too much funds in account, found "+valueInAccount+", expected less", valueInAccount<valueToTransfer);
    }
  }

  @Then("^a payment will be generated$")
  public void a_payment_will_be_generated() throws Throwable {
    post(SERVICE_URL+"/rest/payments/process");
  }

  @Then("^the account owner will be notified:$")
  public void the_account_owner_will_be_notified(List<Map<String, String>> table) throws Throwable {
    for(Map<String,String> row:table){
      Response response=given().when().get(SERVICE_URL+"/rest/payments/"+row.get("Payment Id"));
      String responseString=response.asString();
      if (response.getStatusCode()!=200)
        throw new RuntimeException("Response was ["+response.getStatusLine()+"], with content of ["+responseString+"]");
      assertEquals(200, response.getStatusCode());
      
      Payment payment=Json.toObject(responseString, Payment.class);
      
      assertEquals(row.get("Account Id"), payment.getNotifiedAccount());
      assertEquals(row.get("Outcome").toUpperCase(), payment.getNotifiedOutcome().toUpperCase());
    }
  }
  
  
  // UTILITY FUNCTIONS
  
  private void post(String url) throws JsonGenerationException, JsonMappingException, IOException{
    post(url, null, null);
  }
  private <T> T post(String url, T requestObject, Class<T> clazz) throws JsonGenerationException, JsonMappingException, IOException{
    RequestSpecification spec=given().when();
    if (null!=requestObject)
      spec.body(Json.toJson(requestObject));
    Response response=spec.post(url);
    String responseString=response.asString();
    if (response.getStatusCode()!=200)
      throw new RuntimeException("Response was ["+response.getStatusLine()+"], with content of ["+responseString+"]");
    assertEquals(200, response.getStatusCode());
    return (T) (clazz!=null?Json.toObject(responseString, clazz):null);
  }
  
  private String get(String url){
    Response response=given().when().get(url);
    String responseString=response.asString();
    if (response.getStatusCode()!=200)
      throw new RuntimeException("Response was ["+response.getStatusLine()+"], with content of ["+responseString+"]");
    assertEquals(200, response.getStatusCode());
    return responseString;
  }
  
}
