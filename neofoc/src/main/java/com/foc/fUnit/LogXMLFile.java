/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.fUnit;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class LogXMLFile extends LogResultFileAbstract{
	private ArrayList<String> nodes = null;

  public LogXMLFile(String name){
  	nodes = new ArrayList<String>(); 
    try{
    	fileWriter = new FileWriter("quality/FocUnitResults.xml");
      out = new PrintWriter(fileWriter, true);
      out.println("<Barmaja>");
      out.println("<Date>"+new Date()+"</Date>");
      out.println("<Suite name=\'"+name+"\'>");
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public void dispose(){
    try{
    	if(out != null){
	      out.println("</Suite>");
	      out.println("</Barmaja>");
	      out.flush();
    	}
    	
      if(fileWriter != null){
      	fileWriter.close();
      }
      fileWriter = null;
      
      if(nodes != null){
      	nodes.clear();
      }
      nodes = null;
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  
  private String addNode(String node){
  	nodes.add(node);
  	return getLineBeginningWithIndentation()+"<"+node;
  }

  private String closeNode(boolean withIndentation){
  	String close = null;
  	if(nodes != null && nodes.size()-1>=0){
  		String node = nodes.get(nodes.size()-1);
  		close = (withIndentation ? getLineBeginningWithIndentation() : "")+"</"+node+">";
  		nodes.remove(nodes.size()-1);
  	}
  	return close;
  }

  private String closeNode(){
  	return closeNode(true);
  }

  private void closeAllTestNodes(){
  	String node = closeNode();
  	while(node != null){
  		out.println(node);
  		node = closeNode();
  	}
  }

	protected String getLineBeginningWithIndentation() {
		return getIndentationSpaces(nodes.size());
	}

  public void logSuiteName(String suiteName){
  	
  }
  
  public void logBeginTest(String testName){
  	step = 0;
  	out.println(addNode("Test")+" name=\'"+testName+"\'>");
  	out.flush();
  }
  
	public void logEndTest(String testName){
		closeAllTestNodes();
		out.flush();
	}
	
  public void logBeginStep(String message){
  	out.println(addNode("Step")+" sequence=\'"+(++step)+"\'>"+message);
  	out.flush();
  }
  
  public void logEndStep(){
		out.println(closeNode());
  	out.flush();
  }
  
  public void logBeginGuiAction(String comp, String compName, String action){
  	out.println(addNode("GuiAction")+" componentType=\'"+comp+"\' componentName=\'"+compName+"\' action=\'"+action+"\'>");
  	out.flush();
  }
  
  public void logEndGuiAction(){
    out.println(closeNode());
    out.flush();
  }
  
  public void logBeginAssert(String message){
  	out.println(addNode("Assert")+">"+message);
  	out.flush();
  }
  
  public void logEndAssert(){
  	out.println(closeNode());
  	out.flush();
  }
  
  public void logStatus(boolean success){
  	out.println(addNode("Status")+">"+(success?"Success":"Fail")+closeNode(false));
  	out.flush();
  }
  
  public void logTestResult(int testCount){
  	if(out != null){
	  	out.println(addNode("Results")+">");
	  	out.println(addNode("Successfull")+">"+testResult.wasSuccessful()+closeNode());
	  	out.println(addNode("Errors")+">"+testResult.errorCount()+closeNode());
	  	out.println(addNode("Failures")+">"+testResult.failureCount()+closeNode());
	  	out.println(addNode("Tested")+">"+testResult.runCount()+closeNode());
	  	out.println(addNode("Total")+">"+testCount+closeNode());
	  	out.println(closeNode());
	  	out.flush();
  	}
  }
}
