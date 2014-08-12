package org.jboss.acceptance.steps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.acceptance.utils.Json;
import org.jboss.acceptance.utils.Utils;
import org.jboss.order.domain.Country;
import org.jboss.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.response.Response;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class OrderServiceSteps{
  private static final Logger log=LoggerFactory.getLogger(OrderServiceSteps.class);
  
  private static final String ORDER_SERVICE_URL="http://127.0.0.1:16080/order-service";
  private List<Order> orders=new ArrayList<Order>();
  
  // this doesnt work cos the cucumber api annotation causes an indexOutOfBoundsError - upgrade cukes?
  private static boolean dunit = false;
  @Before
  public void beforeAll(){
    if(!dunit) {
      Runtime.getRuntime().addShutdownHook(new Thread() {
        public void run() {
          log.debug("@AfterAll");
        }
      });
      log.debug("@BeforeAll");
      Utils.beforeScenarios();
      dunit = true;
    }
  }
  
  @Given("^the order service is deployed$")
  public void the_order_service_is_deployed() throws Throwable {
    assertEquals(200, given().when().get(ORDER_SERVICE_URL+"/version").getStatusCode());
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
      Response response=given().when().body(payload).post(ORDER_SERVICE_URL+"/rest/riskcheck");
      String responseString=response.asString();
      if (response.getStatusCode()!=200)
        throw new RuntimeException("Response was ["+response.getStatusLine()+"], with content of ["+responseString+"]");
      
      log.debug("riskcheck response was ["+responseString+"]");
      assertEquals(200, response.getStatusCode());
      
      Order responseOrder=Json.toObject(responseString, Order.class);
      order.setRisk(responseOrder.getRisk());
      order.setRecommendation(responseOrder.getRecommendation());
    }
  }
  
  @Then("^the results should be:$")
  public void the_result_should_be(List<Map<String,String>> table) throws Throwable {
    assertEquals(table.size(), orders.size());
    int ordersCheckedCount=0;
    for(Map<String,String> row:table){
      String id=row.get("ID");
      String riskRating=row.get("Risk Rating");
      String recommendation=row.get("Recommendation");
      for(Order order:orders){
        if (id.equals(order.getId())){
          assertEquals(riskRating, order.getRisk());
          assertEquals(recommendation, order.getRecommendation());
          ordersCheckedCount+=1;
        }
      }
    }
    assertEquals(table.size(), ordersCheckedCount);
  }
  
}
