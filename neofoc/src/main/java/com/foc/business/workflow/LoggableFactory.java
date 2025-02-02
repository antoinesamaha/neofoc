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
package com.foc.business.workflow;

import java.util.ArrayList;

import com.foc.business.workflow.implementation.ILoggableDesc;
import com.foc.business.workflow.implementation.IWorkflowDesc;
import com.foc.business.workflow.implementation.LoggableDesc;
import com.foc.desc.FocDesc;

public class LoggableFactory {
	ArrayList<ILoggableDesc> loggableArray = null;
	
	public LoggableFactory(){
		loggableArray = new ArrayList<ILoggableDesc>();
	}
	
	public void dispose(){
		if(loggableArray != null){
			loggableArray.clear();
			loggableArray = null;
		}
	}

	public ILoggableDesc findWorkflowDescForDBName(String storageName){
		ILoggableDesc foundDesc = null;
		for(int i=0; i<LoggableFactory.getInstance().getFocDescCount() && foundDesc == null; i++){
			ILoggableDesc iWorkflowDesc = LoggableFactory.getInstance().getIWorkflowDesc(i);
			if(iWorkflowDesc != null && iWorkflowDesc.iWorkflow_getWorkflowDesc() != null){
				LoggableDesc workflowDesc = iWorkflowDesc.iWorkflow_getWorkflowDesc();
				String iworkflowDescName = workflowDesc.getFocDesc() != null ? workflowDesc.getFocDesc().getStorageName() : null;
				if(iworkflowDescName != null && iworkflowDescName.equals(storageName)){
					foundDesc = iWorkflowDesc;
				}
			}
  	}
		return foundDesc;
	}
	
	public ILoggableDesc findWorkflowDesc_ByDBTitle(String className){
		ILoggableDesc foundDesc = null;
		for(int i=0; i<LoggableFactory.getInstance().getFocDescCount() && foundDesc == null; i++){
			ILoggableDesc iWorkflowDesc = LoggableFactory.getInstance().getIWorkflowDesc(i);
			if(			iWorkflowDesc != null 
					&&  iWorkflowDesc.iWorkflow_getWorkflowDesc() != null 
					&& 	iWorkflowDesc.iWorkflow_getWorkflowDesc().getFocDesc() != null){
				String dbTitle = iWorkflowDesc.iWorkflow_getWorkflowDesc().getFocDesc().getStorageName();
				if(dbTitle.equals(className)){
					foundDesc = iWorkflowDesc;
				}
			}
  	}
		return foundDesc;
	}
	
	public int getFocDescCount(){
		return loggableArray.size();
	}
	
	public FocDesc getFocDescAt(int i){
		return (FocDesc) loggableArray.get(i);
	}
	
	public ILoggableDesc getIWorkflowDesc(int i){
		return (ILoggableDesc) loggableArray.get(i);
	}
	
	public void add(ILoggableDesc iLoggableDesc){
		loggableArray.add(iLoggableDesc);
	}
	
	private static LoggableFactory instance = null;
	public static LoggableFactory getInstance(){
		if(instance == null){
			instance = new LoggableFactory();
		}
		return instance;
	}
}
