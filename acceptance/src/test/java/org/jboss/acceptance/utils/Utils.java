package org.jboss.acceptance.utils;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;

public class Utils {
  private static final Logger log=Logger.getLogger(Utils.class);
  
  public static void beforeScenarios(){
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
        if (result)
          log.debug("Deployment found ["+response+"]");
//        log.debug("[found? "+result+"] deployments on BPMS = "+response);
        return result;
      }
    }, "Unable to find deployed rules");
    assertEquals("Rules were not deployed", true, successfulRuleDeployment);
  }
}
