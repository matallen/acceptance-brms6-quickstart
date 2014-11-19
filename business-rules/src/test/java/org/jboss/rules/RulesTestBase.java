/*
* JBoss, Home of Professional Open Source
* Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
* contributors by the @authors tag. See the copyright.txt in the
* distribution for a full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.jboss.rules;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.lang.dsl.DSLTokenizedMappingFile;
import org.drools.compiler.lang.dsl.DefaultExpander;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Assert;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RulesTestBase {
  private static final Logger log = LoggerFactory.getLogger(RulesTestBase.class);
	public KieSession session;
	public static final String ruleDirectoryDefault="src/main/resources";
	private KieServices kieServices=KieServices.Factory.get();
	private KieFileSystem kfs=kieServices.newKieFileSystem();
	
	public String getRuleDirectory(){ return ruleDirectoryDefault; }
	
	private void compileFromDisk(String basePath, String packageNames) throws IOException{
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
          log.debug(drl);
//          kfs.write("src/main/resources/test.drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl.getBytes())).setResourceType(ResourceType.DRL));
        }else if (file.getName().toLowerCase().matches(".+dslr$")){
          File[] dslFiles=file.getParentFile().listFiles(new FilenameFilter() {
            @Override public boolean accept(File dir, String name) {
              return name.endsWith(".dsl");
          }});
          Assert.assertEquals("There should be 1 dsl file ", 1, dslFiles.length);
//          File dsl=[0];
          
          String drl2=parseDRLFromDSL(dslFiles[0], file);
          System.out.println(drl2);
          
//          String dslr=IOUtils.toString(new FileInputStream(file));
          kfs.write("src/main/resources/"+packageId+"/"+file.getName()+".drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl2.getBytes())).setResourceType(ResourceType.DSL));
        }
        if (null!=drl)
          kfs.write("src/main/resources/"+packageId+"/"+file.getName()+".drl",kieServices.getResources().newInputStreamResource(new ByteArrayInputStream(drl.getBytes())).setResourceType(ResourceType.DRL));
      }
	  }
    KieBuilder builder=kieServices.newKieBuilder(kfs);
    builder.buildAll();
    Assert.assertEquals( 0, builder.getResults().getMessages( Message.Level.ERROR ).size() );
    log.debug(builder.getResults().toString());
	}
	
	/**
	 * Loads the "defaultKieBase.session" kieSession
	 * @return
	 */
	public KieSession loadKieSession() {
	  return loadKieSession("defaultKieBase.session");
	}
	
  /**
   * Loads the specified @param kSessionId kieSession
   * @return
   */
  public KieSession loadKieSession(String kSessionId) {
    session=KieServices.Factory.get().getKieClasspathContainer().newKieSession(kSessionId);
    debugKieBase(session.getKieBase());
    return session;
  }
  
  private void debugKieBase(KieBase kBase){
    if (0==kBase.getKiePackages().size()) throw new RuntimeException("No rules in kBase!!!");
    System.out.println("\nRules:");
    for (KiePackage kp : kBase.getKiePackages()) {
      for (Rule r : kp.getRules()) {
        System.out.println(kp.getName()+"->"+r.getName());
      }
    }
  }

	public KieSession compileAndLoadKieSession(String packageNames) {
		try {
			kieServices.getRepository().removeKieModule(kieServices.getRepository().getDefaultReleaseId());			
	    compileFromDisk(ruleDirectoryDefault, packageNames);
			
	    KieContainer kieContainer=kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
	    this.session=kieContainer.newKieSession();
	    debugKieBase(session.getKieBase());
		  
			return session;
		} catch (Exception e) {
			throw new RuntimeException("Unable to compile rules - more details in cause:", e);
		}
	}
	
	public <T> int fireAllRules(){
	  return fireAllRules(new String[]{});
	}
	public <T> int fireAllRules(T... facts){
	  return fireAllRules(Arrays.asList(facts));
	}
	@SuppressWarnings("rawtypes")
  public <T> int fireAllRules(List<T> facts){
    AgendaEventListener listener=new SysErrAgendaEventListener();
		try{
		  session.addEventListener((AgendaEventListener)listener);
		  if (null!=facts){
  			for(T fact:facts){
  			  if (Collection.class.isAssignableFrom(fact.getClass())){
  			    for(Object f:(Collection)fact)
  			      session.insert(f);
  			  }else
  			    session.insert(fact);
  			}
		  }
			return session.fireAllRules();
		}finally{
		  session.removeEventListener(listener);
			session.dispose();
		}
	}
	
  public String parseDRLFromDSL(File dslFile, File dslrFile) throws FileNotFoundException {
    DSLTokenizedMappingFile mapfile=new DSLTokenizedMappingFile();
    DefaultExpander expander=new DefaultExpander();
    Reader dslReader=new FileReader(dslFile);
    Reader dslrReader=new FileReader(dslrFile);
    try {
      mapfile.parseAndLoad(dslReader);
      expander.addDSLMapping(mapfile.getMapping());
      return expander.expand(dslrReader);
    } catch (IOException e) {
      return expander.getErrors().toString();
    }
  }
}
