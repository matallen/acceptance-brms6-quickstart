package org.jboss.acceptance.steps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.acceptance.CommonSteps;
import org.jboss.acceptance.utils.Json;
import org.jboss.acceptance.utils.ToHappen;
import org.jboss.acceptance.utils.Wait;
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.junit.BeforeClass;

import com.google.common.base.Preconditions;
import com.jayway.restassured.response.Response;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RiskServiceSteps{
  private List<Order> orders=new ArrayList<Order>();
  
  // this doesnt work cos the cucumber api annotation causes an indexOutOfBoundsError - upgrade cukes?
  private static boolean dunit = false;
//  @Before
  public void beforeAll(){
    if(!dunit) {
      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          System.out.println("@AfterAll");
        }
      });
      System.out.println("@BeforeAll");
      beforeEachScenario();
      dunit = true;
    }
  }
  
  public void beforeEachScenario(){
    boolean successfulRuleDeployment=Wait.For(20, new ToHappen() {
      @Override
      public boolean hasHappened() {
        String username=System.getProperty("bpms.username")!=null?System.getProperty("bpms.username"):"admin";
        String password=System.getProperty("bpms.password")!=null?System.getProperty("bpms.password"):"admin";
        String serverUrl=System.getProperty("bpms.base.url")!=null?System.getProperty("bpms.base.url"):"http://localhost:8080/business-central";
        Preconditions.checkArgument(username!=null, "bpms.username cannot be null");
        Preconditions.checkArgument(password!=null, "bpms.password cannot be null");
        Preconditions.checkArgument(serverUrl!=null, "bpms.base.url cannot be null");
        String response=given().when().auth().preemptive().basic(username, password).get(serverUrl+"/rest/deployment").asString();
        boolean result=response.contains("business-rules");
//        System.out.println("[found? "+result+"] deployments on BPMS = "+response);
        return result;
      }
    });
    assertEquals("Rules were not deployed", true, successfulRuleDeployment);
//    new CommonSteps().beforeEachScenario(); // you cannot extend classes that define cucumber step definitions or hooks
  }
  
  @Given("^the order service is deployed$")
  public void the_order_service_is_deployed() throws Throwable {
    beforeEachScenario(); // you cannot extend classes that define cucumber step definitions or hooks
    assertEquals(200, given().when().get("http://localhost:8080/order-service/rest/version").getStatusCode());
  }

  @Given("^an order exists with the following details:$")
  public void an_order_exists_with_the_following_details(List<Map<String,String>> table) throws Throwable {
    orders.clear();
    for(Map<String,String> row:table){
      String id=row.get("ID");
      String country=row.get("Country");
      double amount=Double.valueOf(row.get("Amount"));
      String[] items=row.get("Items").split(",");
      orders.add(new Order(id, Country.valueOf(country), amount, items));
    }
  }

  @When("^the order is submitted$")
  public void the_order_is_submitted() throws Throwable {
    for(Order order:orders){
      String payload="{\"id\":\""+order.getId()+"\",\"country\":\""+order.getCountry().name()+"\",\"amount\":"+order.getAmount()+",\"items\":[]}";
      Response response=given().when().body(payload).post("http://localhost:8080/order-service/rest/riskcheck");
      String responseString=response.asString();
      System.out.println("riskcheck response = "+responseString);
      assertEquals(200, response.getStatusCode());
      
      Order responseOrder=Json.toObject(responseString, Order.class);
      order.setRisk(responseOrder.getRisk());
      order.setRecommendation(responseOrder.getRecommendation());
    }
  }
  
  @Then("^the results should be:$")
  public void the_result_should_be(List<Map<String,String>> table) throws Throwable {
    for(Map<String,String> row:table){
      String id=row.get("ID");
      String riskRating=row.get("Risk Rating");
      String recommendation=row.get("Recommendation");
      for(Order order:orders){
        if (id.equals(order.getId())){
          assertEquals(riskRating, order.getRisk());
          assertEquals(recommendation, order.getRecommendation());
        }
      }
    }
    assertEquals(table.size(), orders.size());
  }
  
}
