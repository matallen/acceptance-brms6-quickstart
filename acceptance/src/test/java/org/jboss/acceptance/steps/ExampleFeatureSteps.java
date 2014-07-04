package org.jboss.acceptance.steps;

import static org.junit.Assert.assertEquals;

//import org.apache.commons.io.IOUtils;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

import cucumber.api.java.en.*;

public class ExampleFeatureSteps {
  
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
