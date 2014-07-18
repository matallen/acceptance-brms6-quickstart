package org.jboss.webapp;

public class Metrics {
  private static long start;
  public static void start(){
    start=System.currentTimeMillis();
  }
  public static long endReset(){
    long result=System.currentTimeMillis()-start;
    start=System.currentTimeMillis();
    return result;
  }
}
