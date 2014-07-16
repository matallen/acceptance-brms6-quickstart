package org.jboss.rules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Assert;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

public class RulesTestBase {
	private KieSession session;
	public static final String ruleDirectoryDefault="src/main/resources";
	private KieServices kieServices=KieServices.Factory.get();
	private KieFileSystem kfs=kieServices.newKieFileSystem();
	
	public String getRuleDirectory(){ return ruleDirectoryDefault; }
	
	private void compileFromDisk(String basePath, String packageNames) throws IOException{
//    File packageFile=new File(basePath, packageId.replaceAll("\\.", File.separator)+File.separator+version);
    
	  for (String packageId:packageNames.split(",")){
	    File packageFile=new File(basePath, packageId.replaceAll("\\.", File.separator));
      if (!packageFile.exists()) throw new RuntimeException("unable to find the directory containing the rules to compile ["+packageFile.getAbsolutePath()+"]");
      for(File file:packageFile.listFiles()){
        String drl=null;
        if (file.getName().toLowerCase().matches(".+drl$")){
          drl=IOUtils.toString(new FileInputStream(file));
//          kfs.write("src/main/resources/test.drl",kieServices.getResources().newInputStreamResource(new FileInputStream(file)).setResourceType(ResourceType.DRL));
        }else if (file.getName().toLowerCase().matches(".+xls$")){
          drl=new SpreadsheetCompiler().compile(new FileInputStream(file), InputType.XLS);
          System.out.println(drl);
//          kfs.write("src/main/resources/test.drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl.getBytes())).setResourceType(ResourceType.DRL));
        }
        kfs.write("src/main/resources/"+packageId+"/"+file.getName()+".drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl.getBytes())).setResourceType(ResourceType.DRL));
      }
	  }
    KieBuilder builder=kieServices.newKieBuilder(kfs);
    builder.buildAll();
    Assert.assertEquals( 0, builder.getResults().getMessages( Message.Level.ERROR ).size() );
    System.out.println(builder.getResults());
	}
	
	public KieSession loadRules(String packageNames) {
		try {
			kieServices.getRepository().removeKieModule(kieServices.getRepository().getDefaultReleaseId());			
	    compileFromDisk(ruleDirectoryDefault, packageNames);
			
	    KieContainer kieContainer=kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
	    this.session=kieContainer.newKieSession();
		    		
			return session;
		} catch (Exception e) {
			throw new RuntimeException("Unable to compile rules - more details in cause:", e);
		}
	}
	
	public <T> int fireAllRules(){
	  return fireAllRules(new String[]{});
	}
	public <T> int fireAllRules(T... facts){
//		EventListener listener=new EventListener();
		try{
//			session.addEventListener((WorkingMemoryEventListener)listener);
		  if (null!=facts){
  			for(T fact:facts){
  			  if (Collection.class.isAssignableFrom(Collection.class)){
  			    for(Object f:(Collection)fact)
  			      session.insert(f);
  			  }else
  			    session.insert(fact);
  			}
		  }
			return session.fireAllRules();
//			session.removeEventListener((WorkingMemoryEventListener)listener);
		}finally{
			session.dispose();
		}
	}
}
