package org.jboss.rules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class RulesTestBase {
	private KieSession session;
	public static final String ruleDirectoryDefault="src/main/rules";
	private KieServices kieServices=KieServices.Factory.get();
	private KieFileSystem kfs=kieServices.newKieFileSystem();
	
	public String getRuleDirectory(){ return ruleDirectoryDefault; }
	
	private void compileFromDisk(String basePath, String packageId, String version) throws IOException{
    File packageFile=new File(basePath, packageId.replaceAll("\\.", File.separator)+File.separator+version);
    if (!packageFile.exists()) throw new RuntimeException("unable to find the directory containing the rules to compile ["+packageFile.getAbsolutePath()+"]");
    for(File file:packageFile.listFiles()){
      String drl=null;
      if (file.getName().toLowerCase().matches(".+drl$")){
        drl=IOUtils.toString(new FileInputStream(file));
//        kfs.write("src/main/resources/test.drl",kieServices.getResources().newInputStreamResource(new FileInputStream(file)).setResourceType(ResourceType.DRL));
      }else if (file.getName().toLowerCase().matches(".+xls$")){
        drl=new SpreadsheetCompiler().compile(new FileInputStream(file), InputType.XLS);
        System.out.println(drl);
//        kfs.write("src/main/resources/test.drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl.getBytes())).setResourceType(ResourceType.DRL));
      }
      kfs.write("src/main/resources/"+file.getName()+".drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl.getBytes())).setResourceType(ResourceType.DRL));
    }
    KieBuilder builder=kieServices.newKieBuilder(kfs);
    builder.buildAll();
    System.out.println(builder.getResults());
	}
	
	public KieSession loadRules(String packageName, String version) {
		try {
			kieServices.getRepository().removeKieModule(kieServices.getRepository().getDefaultReleaseId());			
	    compileFromDisk(ruleDirectoryDefault, packageName, version);
			
	    KieContainer kieContainer=kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
	    this.session=kieContainer.newKieSession();
		    		
			return session;
		} catch (Exception e) {
			throw new RuntimeException("Unable to compile rules - more details in cause:", e);
		}
	}
	
	public <T> void fireAllRules(T... facts ){
//		EventListener listener=new EventListener();
		try{
//			session.addEventListener((WorkingMemoryEventListener)listener);
			for(T fact:facts)
				session.insert(fact);
			session.fireAllRules();
//			session.removeEventListener((WorkingMemoryEventListener)listener);
		}finally{
			session.dispose();
		}
	}
}
