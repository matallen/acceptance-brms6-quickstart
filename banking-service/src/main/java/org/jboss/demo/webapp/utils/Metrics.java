package org.jboss.demo.webapp.utils;

import java.util.HashMap;
import java.util.Map;

public class Metrics {
  private long start;
  private Map<String,Long> metrics=new HashMap<String, Long>();
  
  public void start(){
    start=System.currentTimeMillis();
  }
  public long end(String name){
    long result=System.currentTimeMillis()-start;
    metrics.put(name, result);
    start=System.currentTimeMillis();
    return result;
  }
  public Long get(String name){
    if (!metrics.containsKey(name)) return 0l;
    return metrics.get(name);
  }
}
