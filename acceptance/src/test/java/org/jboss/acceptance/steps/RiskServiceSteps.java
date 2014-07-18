package org.jboss.acceptance.steps;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.jboss.acceptance.utils.Json;
import org.jboss.order.domain.Order;
import org.jboss.order.domain.Country;

import com.google.common.collect.Lists;
import com.jayway.restassured.response.Response;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class RiskServiceSteps {
  private List<Order> orders=Lists.newArrayList();
  
  @Given("^the order service is deployed$")
  public void the_risk_service_is_deployed() throws Throwable {
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
  public void the_order_is_submitted_to_the_risk_service() throws Throwable {
    for(Object key:System.getProperties().keySet()){
      System.out.println("[SYSPROP] "+key+"="+System.getProperty((String)key));
    }
    
    for(Order order:orders){
      String payload="{\"id\":\""+order.getId()+"\",\"country\":\""+order.getCountry().name()+"\",\"amount\":"+order.getAmount()+",\"items\":[]}";
      Response response=given().when().body(payload).post("http://localhost:8080/order-service/rest/riskcheck");
      String responseString=response.asString();
      System.out.println(responseString);
      assertEquals(200, response.getStatusCode());
      
      Order responseOrder=Json.toObject(responseString, Order.class);
      order.setRisk(responseOrder.getRisk());
      order.setRecommendation(responseOrder.getRecommendation());
    }
  }

  @Then("^the results should be:$")
  public void the_result_is(List<Map<String,String>> table) throws Throwable {
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
  
//  @Given("^brms is accessible on \"([^\"]*)\"$")
//  public void brmsIsUp(String brmsUrl) throws Exception{
//    HttpClient client=new DefaultHttpClient();
//    HttpGet get=new HttpGet(brmsUrl+"/rest/packages");
//    HttpResponse response=client.execute(get);
//    System.out.println("XXXXXXXX="+response.getStatusLine().getStatusCode());
//    assertEquals(response.getStatusLine().getStatusCode(), 200);
//  }
//  
//  @Given("^the url \"([^\"]*)\" returns an http \"([^\"]*)\"$")
//  public void the_url_returns_an_http(String url, String responseCode) throws Throwable {
//    HttpClient client=new DefaultHttpClient();
//    HttpResponse response=client.execute(new HttpGet(url));
//    if (200!=response.getStatusLine().getStatusCode())
//      System.out.println(IOUtils.toString(response.getEntity().getContent()));
//    assertEquals(Integer.parseInt(responseCode), response.getStatusLine().getStatusCode());
//  }
//  
//  @Given("^the fuse command \"([^\"]*)\" contains \"([^\"]*)\"$")
//  public void the_fuse_command_contains(String arg1, String arg2) throws Throwable {
////    System.out.println("\n\nXXXXXXXXXXXXX");
////    
////    String osgiListResult=executeCommand("features:list | grep installed");
////    System.out.println("osgiListResult = \n"+osgiListResult);
////    System.out.println("XXXXXXXXXXXXX\n\n");
//    
////    String result=executeCommand("osgi:list -t 0", 3000l, false);
////    System.out.println(result);
//  }
//
//  
//  /*
//  @Given("^the url \"([^\"]*)\" returns an http \"([^\"]*)\"$")
//  public void httpPingReturns(String url, String responseCode) throws Exception{
//    HttpClient client=new DefaultHttpClient();
//    HttpGet get=;
//    HttpResponse response=client.execute(new HttpGet(url));
//    assertEquals(response.getStatusLine().getStatusCode(), Integer.parseInt(responseCode));
//  }
//  */
//  
//  public String executeCommand(String command){
//    try{
//      System.out.println("executing [/home/mallen/jboss-fuse-6.0.0.redhat-024/bin/client -u admin -p admin "+command+"]");
//      Process p=Runtime.getRuntime().exec(new String[]{"/home/mallen/jboss-fuse-6.0.0.redhat-024/bin/client","-u","admin","-p","admin", command});
//      String result=IOUtils.toString(p.getInputStream());
//      System.out.println("result of ["+command+"]\n"+result);
//      return result;
//    }catch(Exception sink){
//      return null;
//    }
//  }

}
