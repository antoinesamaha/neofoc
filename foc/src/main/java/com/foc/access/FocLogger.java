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
package com.foc.access;

import com.foc.FocUnitTestFailureException;
import com.foc.Globals;

public class FocLogger {

  private FocLogLineList logLineList = null;
  private FocLogLineTree logLineTree = null;
  private FocLogLine     currentNode = null;
  private boolean        hasFailure  = false;
  
  private boolean        enabled     = true;
	
  public FocLogger(){
    logLineList = new FocLogLineList();
  }
  
  public void dispose(){
    currentNode = null;
    if(logLineList != null){
      logLineList.dispose();
      logLineList = null;
    }
    if(logLineTree != null){
      logLineTree.dispose();
      logLineTree = null;
    }
  }
  
  public FocLogLineTree getTree(){
    if(logLineTree == null){
      logLineTree = new FocLogLineTree();
      logLineTree.growTreeFromFocList(getLogLineList());      
    }
    return logLineTree;
  }
  
  public FocLogLineList getLogLineList(){
    if(logLineList == null){
      logLineList = new FocLogLineList();
    }
    return logLineList;
  }
  
  public FocLogLine findAncestor(int type){
  	FocLogLine found = null;
  	FocLogLine curr = currentNode;
  	while(curr != null && curr.getType() != type){
  		curr = (FocLogLine) curr.getFatherObject();
  	}
  	if(curr != null && curr.getType() == type) found = curr; 
  	return found;
  }
  
  public boolean openTest(String message){
  	boolean error = true;
  	FocLogLine foundTest = findAncestor(FocLogLine.TYPE_TEST);
  	if(foundTest == null){
  		error = false;
		  FocLogLine line = addTest(message);
		  if(line != null) {
			  line.setFatherObject(currentNode);
			  currentNode = line;
		  }
  	}
  	return error;
  }

  public boolean openCommand(String message){
  	boolean error = true;
  	FocLogLine foundCmd = findAncestor(FocLogLine.TYPE_COMMAND);
  	if(foundCmd == null){
  		error = false;
		  FocLogLine line = addCommand(message);
		  if(line != null) {
			  line.setFatherObject(currentNode);
			  currentNode = line;
		  }
  	}
  	return error;
  }
  
  public FocLogLine openNode(String message){
	  //
	  //use the current node the current FocLogLine 
	  //addLogLine();
  	FocLogLine line = null;
  	if(isEnabled()) {
		  line = addInfo(message);
		  line.setFatherObject(currentNode);
		  currentNode = line;
		  Globals.logString(" FocUnitTest OpenNode: "+message);
  	}
  	return line;
  }

  public void closeNode(){
  	closeNode(null);
  }

  public void closeNodeUntil(FocLogLine logLine){
  	if(logLine == null) {
  		closeNode(null);
  	} else {
	  	boolean stop = currentNode == logLine;
	  	closeNode(null);
	  	while(!stop) {
	  		stop = currentNode == logLine;
	  		closeNode(null);
	  	}
  	}
  }
  
  public void closeNode(String message){
  	if(isEnabled()) {
	    if(message != null && !message.isEmpty()){
	      addInfo(message);
	    }
	    if(currentNode != null){
	    	//currentNode.setClosed(true);
	    }
	    currentNode = (FocLogLine) (currentNode != null ? currentNode.getFatherObject() : null);
  	}
  }
  
  public FocLogLine addTest(String message) {
  	FocLogLine line = null;
  	if(isEnabled()) {
	    line = addLogLine(FocLogLine.TYPE_TEST, message);
	    line.setSuccessful(true);
  	}
	  return line;
  }
  
  public FocLogLine addCommand(String message) {
  	FocLogLine line = null;
  	if(isEnabled()) {
	    line = addLogLine(FocLogLine.TYPE_COMMAND, message);
	    line.setSuccessful(true);
  	}
    return line;
  }
  
  public FocLogLine addInfo(String message) {
  	FocLogLine line = null;
  	if(isEnabled()) {
	    line = addLogLine(FocLogLine.TYPE_INFO, message);
	    line.setSuccessful(true);
  	}
    return line;
  }

  public FocLogLine addError(String message) throws Exception {
  	FocLogLine line = null; 
  	if(isEnabled()) {
	    line = addLogLine(FocLogLine.TYPE_ERROR, message);
	    line.setSuccessful(false);
	    setHasFailure(true);
  	}
    return line;
  }
  
  public FocLogLine addFailure(String message) throws Exception {
  	FocLogLine line = null;
  	if(isEnabled()) {
	    line = addLogLine(FocLogLine.TYPE_FAILURE, message);
	    line.setSuccessful(false);
	    setHasFailure(true);
  	}
    return line;
  }

  public FocLogLine addWarning(String message) {
  	FocLogLine line = null;
  	if(isEnabled()) {
	    line = addLogLine(FocLogLine.TYPE_WARNING, message);
	    line.setSuccessful(true);
  	}
    return line;
  }
    
  private FocLogLine addLogLine(int type, String message) {
    FocLogLine line = (FocLogLine) getLogLineList().newEmptyItem();
    line.setDateTime(new java.sql.Date(System.currentTimeMillis()));
    line.setType(type);
    line.setMessage(message);
    line.setFatherObject(currentNode);
    boolean backup = getLogLineList().isDisableReSortAfterAdd();
    getLogLineList().setDisableReSortAfterAdd(true);
    getLogLineList().add(line);
    getLogLineList().setDisableReSortAfterAdd(backup);
    return line;
  }
  
  public void displayLogConsole() {
    for(int i=0; i<getLogLineList().size(); i++){
      FocLogLine logLine = (FocLogLine) getLogLineList().getFocObject(i);
      Globals.logString(logLine.getLogLineString());
    }
  }
  
  public static FocLogger getInstance(boolean createIfNeeded) {
    FocLogger logger = null;
    if (Globals.getApp() != null) {
      logger = Globals.getApp().getFocLogger(createIfNeeded);
    }
    return logger;
  }
  
  public static FocLogger getInstance() {
    return getInstance(true);
  }

	public boolean isHasFailure() {
		return hasFailure;
	}

	public void setHasFailure(boolean hasFailure) throws Exception {
		this.hasFailure = hasFailure;
		if(hasFailure && Globals.getApp().isUnitTest()) {
			throw new FocUnitTestFailureException();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
  
