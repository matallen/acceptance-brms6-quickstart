package org.jboss.acceptance.utils;

public class Wait{
	  public static boolean For(int timeoutInSeconds, int intervalInSeconds, ToHappen toHappen, String errorMessage) {
	    long start=System.currentTimeMillis();
	    long end=start+(timeoutInSeconds*1000);
	    boolean timeout=false;
	    while(!toHappen.hasHappened() && !timeout){
	      try{
	        Thread.sleep((intervalInSeconds*1000));
	      }catch(InterruptedException ignor){}
//	      System.out.println("[Wait] - waiting... ["+((end-System.currentTimeMillis())/1000)+"s]");
	      timeout=System.currentTimeMillis()>end;
	      if (timeout) System.out.println("timed out waiting: "+errorMessage);
	    }
	    if (timeout) return false;
	    return true;
	  }
	  public static boolean For(int timeoutInSeconds, ToHappen toHappen, String errorMessage) {
	    return For(timeoutInSeconds, 1, toHappen, errorMessage);
	  }

    public static boolean For(int timeoutInSeconds, ToHappen toHappen) {
      return For(timeoutInSeconds, 1, toHappen, "dont know what we were waiting for");
    }
}